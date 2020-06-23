package org.r.framework.thrift.client.core.factory;

import org.r.framework.thrift.client.core.channel.ThriftNettyChannel;
import org.r.framework.thrift.client.core.exception.ChannelOpenFailException;

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
    ThriftNettyChannel build(String ip, int port) throws ChannelOpenFailException;


}
