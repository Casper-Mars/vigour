package org.r.framework.thrift.client.core.manager;

import org.r.framework.thrift.client.core.channel.ThriftNettyChannel;
import org.r.framework.thrift.client.core.event.ChannelCloseEvent;
import org.r.framework.thrift.client.core.exception.ChannelOpenFailException;
import org.r.framework.thrift.client.core.factory.ChannelFactory;
import org.r.framework.thrift.client.core.observer.Subscriber;
import org.r.framework.thrift.client.core.wrapper.ChannelWrapper;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * date 2020/6/23 下午1:54
 *
 * @author casper
 **/
public class DefaultChannelManager implements ChannelManager, Subscriber<ChannelCloseEvent> {


    private final Map<Integer, ChannelWrapper> channels;

    private final ChannelFactory channelFactory;

    public DefaultChannelManager(ChannelFactory channelFactory) {
        this(channelFactory, new ConcurrentHashMap<>());
    }

    public DefaultChannelManager(ChannelFactory channelFactory, Map<Integer, ChannelWrapper> channels) {
        this.channelFactory = channelFactory;
        this.channels = channels;
    }

    /**
     * 获取和目标主机的链接，如果没有则新建
     *
     * @param ip   远程服务器ip
     * @param port 远程进程端口
     * @return
     */
    @Override
    public ThriftNettyChannel getChannel(String ip, int port) throws ChannelOpenFailException {
        int signature = getSignature(ip, port);
        ChannelWrapper channelWrapper = channels.get(signature);
        if (channelWrapper == null) {
            channelWrapper = buildChannel(ip, port);
            channels.put(signature, channelWrapper);
        }
        return channelWrapper.getChannel();
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
        return new ChannelWrapper(channelFactory.build(ip, port), ip, port);
    }

    /**
     * 读邮件
     *
     * @param mail 邮件
     */
    @Override
    public void readMail(ChannelCloseEvent mail) {
        int signature = getSignature(mail.getIp(), mail.getPort());
        this.channels.remove(signature);
    }
}
