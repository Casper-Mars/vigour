package org.r.framework.thrift.netty.manager;

import org.r.framework.thrift.netty.core.codec.ThriftClientChannel;
import org.r.framework.thrift.netty.core.events.ChannelConnectEvent;
import org.r.framework.thrift.netty.core.events.Postman;
import org.r.framework.thrift.netty.core.events.Subscriber;
import org.r.framework.thrift.netty.exception.ChannelOpenFailException;

/**
 * channel的管理器，包括channel的创建和删除，以及channel掉线剔除和剔除通知
 *
 * @author casper
 * @date 2020/6/23 下午1:39
 **/
public interface ChannelManager extends Subscriber<ChannelConnectEvent> {


    /**
     * 获取和目标主机的链接，如果没有则新建
     *
     * @param ip   远程服务器ip
     * @param port 远程进程端口
     * @return
     */
    ThriftClientChannel getChannel(String ip, int port) throws ChannelOpenFailException;

    /**
     * 获取channel变更通知者
     * 可以通过通知者注册事件监听
     *
     * @return
     */
    Postman<ChannelConnectEvent> getPostman();

    /**
     * 建立channel
     *
     * @param ip   远程主机ip
     * @param port 远程进程端口
     * @return
     */
    ThriftClientChannel build(String ip, int port) throws ChannelOpenFailException;


}
