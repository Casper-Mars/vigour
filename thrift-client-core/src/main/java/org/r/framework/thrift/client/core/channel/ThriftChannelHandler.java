package org.r.framework.thrift.client.core.channel;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * date 2020/6/22 22:33
 *
 * @author casper
 */
public class ThriftChannelHandler extends ChannelInboundHandlerAdapter {


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Channel channel = ctx.channel();
        if (channel instanceof ThriftNettyChannel) {
            ThriftNettyChannel nettyChannel = (ThriftNettyChannel) channel;
            nettyChannel.onMsgRec((ByteBuf) msg);
        } else {
            super.channelRead(ctx, msg);
        }
    }

}
