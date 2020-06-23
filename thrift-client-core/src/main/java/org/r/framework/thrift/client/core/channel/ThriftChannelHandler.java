package org.r.framework.thrift.client.core.channel;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * date 2020/6/22 22:33
 *
 * @author casper
 */
public class ThriftChannelHandler extends SimpleChannelInboundHandler<ByteBuf> {


    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf) throws Exception {
        Channel channel = channelHandlerContext.channel();
        if (channel instanceof ThriftNettyChannel) {
            ThriftNettyChannel nettyChannel = (ThriftNettyChannel) channel;
            nettyChannel.onMsgRec(byteBuf);
        }
    }
}
