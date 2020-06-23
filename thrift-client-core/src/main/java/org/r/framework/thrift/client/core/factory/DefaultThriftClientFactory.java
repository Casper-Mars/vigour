package org.r.framework.thrift.client.core.factory;

import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TMultiplexedProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TTransport;
import org.r.framework.thrift.client.core.exception.ChannelOpenFailException;
import org.r.framework.thrift.client.core.manager.ChannelManager;
import org.r.framework.thrift.client.core.thrift.NettyTransport;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * date 2020/6/23 下午1:51
 *
 * @author casper
 **/
public class DefaultThriftClientFactory implements ThriftClientFactory {


    /**
     *
     */
    private final ChannelManager channelManager;

    public DefaultThriftClientFactory(ChannelManager channelManager) {
        this.channelManager = channelManager;
    }

    /**
     * 构建协议
     *
     * @param serverName 服务名称
     * @param ip         远程服务主机ip
     * @param port       远程进程端口
     * @return
     */
    @Override
    public TProtocol buildProtocol(String serverName, String ip, int port) throws ChannelOpenFailException {
        return new TMultiplexedProtocol(new TBinaryProtocol(buildTransport(ip, port)), serverName);
    }

    /**
     * 构建通讯载体
     *
     * @param ip   远程服务主机ip
     * @param port 远程进程端口
     * @return
     */
    @Override
    public TTransport buildTransport(String ip, int port) throws ChannelOpenFailException {
        return new NettyTransport(channelManager.getChannel(ip,port));
    }

    /**
     * 构建thrift客户端代理类
     *
     * @param clazz      thrift客户端类
     * @param serverName 代理的远程服务名称
     * @param ip         远程服务主机ip
     * @param port       远程进程端口
     * @return
     */
    @Override
    public Object buildClient(Class<?> clazz, String serverName, String ip, int port) {

        Object instance = null;
        try {
            TProtocol protocol = buildProtocol(serverName, ip, port);
            Constructor<?> constructor = clazz.getConstructor(TProtocol.class);
            instance = constructor.newInstance(protocol);
        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException | ChannelOpenFailException e) {
            e.printStackTrace();
        }
        return instance;
    }
}
