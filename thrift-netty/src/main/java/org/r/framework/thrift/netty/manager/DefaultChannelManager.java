package org.r.framework.thrift.netty.manager;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import org.r.framework.thrift.netty.core.codec.ThriftClientChannel;
import org.r.framework.thrift.netty.core.codec.ThriftClientChannelInitializer;
import org.r.framework.thrift.netty.core.events.ChannelConnectEvent;
import org.r.framework.thrift.netty.core.events.ChannelConnectionCloseEvent;
import org.r.framework.thrift.netty.core.events.Postman;
import org.r.framework.thrift.netty.exception.ChannelOpenFailException;
import org.r.framework.thrift.netty.wrapper.ChannelWrapper;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * date 2020/6/23 下午1:54
 *
 * @author casper
 **/
public class DefaultChannelManager implements ChannelManager {


    /**
     * channel的缓存
     */
    private final Map<Integer, ChannelWrapper> channels;

    /**
     * 事件处理器
     */
    private final Postman<ChannelConnectEvent> postman;

    /**
     * netty客户端启动类
     */
    private final Bootstrap bootstrap;


    public DefaultChannelManager(int workers, int maxFrameSize) {
        this.channels = new ConcurrentHashMap<>();
        this.postman = new Postman<>();
        this.postman.start();
        EventLoopGroup workThreads = new NioEventLoopGroup(workers);
        bootstrap = new Bootstrap();
        bootstrap.group(workThreads).channel(ThriftClientChannel.class);
        bootstrap.handler(new ThriftClientChannelInitializer(maxFrameSize, postman));
    }


    /**
     * 获取和目标主机的链接，如果没有则新建
     *
     * @param ip   远程服务器ip
     * @param port 远程进程端口
     * @return
     */
    @Override
    public ThriftClientChannel getChannel(String ip, int port) throws ChannelOpenFailException {
        int signature = getSignature(ip, port);
        ChannelWrapper channelWrapper = channels.get(signature);
//        ChannelWrapper channelWrapper = channels.get(signature);
        if (channelWrapper == null) {
            synchronized (this) {
                channelWrapper = channels.get(signature);
                if (channelWrapper == null) {
                    channelWrapper = buildChannel(ip, port);
                    channels.put(signature, channelWrapper);
                }
            }
        }
        return channelWrapper.getChannel();
    }

    /**
     * 获取channel变更通知者
     * 可以通过通知者注册事件监听
     *
     * @return
     */
    @Override
    public Postman<ChannelConnectEvent> getPostman() {
        return postman;
    }

    /**
     * 建立channel
     *
     * @param ip   远程主机ip
     * @param port 远程进程端口
     * @return
     */
    @Override
    public ThriftClientChannel build(String ip, int port) throws ChannelOpenFailException {
        SocketAddress socketAddress = new InetSocketAddress(ip, port);
        ChannelFuture connect = bootstrap.connect(socketAddress);
        try {
            return (ThriftClientChannel) connect.sync().channel();
        } catch (Exception e) {
            throw new ChannelOpenFailException(e);
        }
    }


    /**
     * 创建签名
     *
     * @param ip   远程主机ip
     * @param port 远程进程端口
     * @return
     */
    private int getSignature(String ip, int port) {
        return Objects.hash(ip, port);
    }


    /**
     * 构造channel
     *
     * @param ip   远程主机ip
     * @param port 远程进程端口
     * @return
     */
    private ChannelWrapper buildChannel(String ip, int port) throws ChannelOpenFailException {
        return new ChannelWrapper(build(ip, port), ip, port);
    }

    /**
     * 读邮件
     *
     * @param mail 邮件
     */
    @Override
    public void readMail(ChannelConnectEvent mail) {
        if (mail instanceof ChannelConnectionCloseEvent) {
            ChannelConnectionCloseEvent tmp = (ChannelConnectionCloseEvent) mail;
            int signature = getSignature(tmp.getIp(), tmp.getPort());
            this.channels.remove(signature);
        }
    }
}
