package org.r.framework.thrift.client.core.factory;

import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TTransport;
import org.r.framework.thrift.client.core.exception.ChannelOpenFailException;

/**
 * @author casper
 * @date 2020/6/23 下午1:46
 **/
public interface ThriftClientFactory {

    /**
     * 构建协议
     *
     * @param serverName 服务名称
     * @param ip         远程服务主机ip
     * @param port       远程进程端口
     * @return
     */
    TProtocol buildProtocol(String serverName, String ip, int port) throws ChannelOpenFailException;

    /**
     * 构建通讯载体
     *
     * @param ip   远程服务主机ip
     * @param port 远程进程端口
     * @return
     */
    TTransport buildTransport(String ip, int port) throws ChannelOpenFailException;

    /**
     * 构建thrift客户端代理类
     *
     * @param clazz      thrift客户端类
     * @param serverName 代理的远程服务名称
     * @param ip         远程服务主机ip
     * @param port       远程进程端口
     * @return
     */
    Object buildClient(Class<?> clazz, String serverName, String ip, int port) throws ChannelOpenFailException;

}
