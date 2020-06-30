package org.r.framework.thrift.client.core.factory;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import org.r.framework.thrift.client.core.config.ConfigProperties;
import org.r.framework.thrift.client.core.exception.ChannelOpenFailException;
import org.r.framework.thrift.netty.codec.ThriftClientChannel;
import org.r.framework.thrift.netty.codec.ThriftClientChannelInitializer;
import org.r.framework.thrift.netty.events.ChannelConnectEvent;
import org.r.framework.thrift.netty.events.Postman;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

/**
 * date 20-6-1 上午11:09
 *
 * @author casper
 **/
public class DefaultChannelFactory implements ChannelFactory {

    private final Bootstrap bootstrap;

    public DefaultChannelFactory(ConfigProperties configProperties, Postman<ChannelConnectEvent> postman) {
        EventLoopGroup workThreads = new NioEventLoopGroup(configProperties.getWorkThreads());
        bootstrap = new Bootstrap();
        bootstrap.group(workThreads).channel(ThriftClientChannel.class);
        bootstrap.handler(new ThriftClientChannelInitializer(configProperties.getMaxFrameSize(),postman));
    }

    /**
     * 建立channel
     *
     * @param ip   远程主机ip
     * @param port 远程进程端口
     * @return
     */
    public ThriftClientChannel build(String ip, int port) throws ChannelOpenFailException {
        SocketAddress socketAddress = new InetSocketAddress(ip, port);
        ChannelFuture connect = bootstrap.connect(socketAddress);
        try {
            return (ThriftClientChannel) connect.sync().channel();
        } catch (Exception e) {
            throw new ChannelOpenFailException(e);
        }
    }


}
