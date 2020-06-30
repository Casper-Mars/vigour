package org.r.framework.thrift.client.core.factory;

import org.r.framework.thrift.client.core.exception.ChannelOpenFailException;
import org.r.framework.thrift.netty.codec.ThriftClientChannel;

/**
 * @author casper
 * @date 2020/6/23 下午2:06
 **/
public interface ChannelFactory {


    /**
     * 建立channel
     *
     * @param ip   远程主机ip
     * @param port 远程进程端口
     * @return
     */
    ThriftClientChannel build(String ip, int port) throws ChannelOpenFailException;


}
