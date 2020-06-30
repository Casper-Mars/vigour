package org.r.framework.thrift.client.core.manager;

import org.r.framework.thrift.client.core.exception.ChannelOpenFailException;
import org.r.framework.thrift.netty.codec.ThriftClientChannel;

/**
 * @author casper
 * @date 2020/6/23 下午1:39
 **/
public interface ChannelManager {


    /**
     * 获取和目标主机的链接，如果没有则新建
     * @param ip 远程服务器ip
     * @param port 远程进程端口
     * @return
     */
    ThriftClientChannel getChannel(String ip, int port) throws ChannelOpenFailException;




}
