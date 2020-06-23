package org.r.framework.thrift.client.core.channel;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import org.r.framework.thrift.client.core.thrift.ThriftRequest;

/**
 * @author casper
 * @date 2020/6/23 上午9:15
 **/
public interface ThriftNettyChannel extends Channel {


    /**
     * 接收到消息事件
     *
     * @param msg 消息
     */
    void onMsgRec(ByteBuf msg);


    /**
     * 发送消息
     *
     * @param msg 消息
     */
    ThriftRequest sendMsg(ByteBuf msg) throws Exception;


}
