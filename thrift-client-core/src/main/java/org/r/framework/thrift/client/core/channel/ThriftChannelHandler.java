package org.r.framework.thrift.client.core.channel;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.r.framework.thrift.netty.ThriftMessage;
import org.r.framework.thrift.netty.ThriftMessageConstants;
import org.r.framework.thrift.netty.ThriftTransportType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * date 2020/6/22 22:33
 *
 * @author casper
 */
public class ThriftChannelHandler extends ChannelInboundHandlerAdapter {

    private final Logger log = LoggerFactory.getLogger(ThriftChannelHandler.class);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Channel channel = ctx.channel();
        if (channel instanceof ThriftNettyChannel && msg instanceof ByteBuf) {
            ThriftNettyChannel nettyChannel = (ThriftNettyChannel) channel;
            ByteBuf buf = (ByteBuf) msg;
            int requestId = buf.getInt(buf.readerIndex());
            int msgOffSet = buf.readerIndex() + ThriftMessageConstants.MESSAGE_REQUEST_ID_SIZE;
            int msgLength = buf.readableBytes() - ThriftMessageConstants.MESSAGE_REQUEST_ID_SIZE;
            log.info("get response for request[id:{}]", requestId);
            nettyChannel.onMsgRec(new ThriftMessage(buf.slice(msgOffSet, msgLength), ThriftTransportType.FRAMED, requestId));
        } else {
            super.channelRead(ctx, msg);
        }
    }

}
