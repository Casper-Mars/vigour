package org.r.framework.thrift.client.core;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.r.framework.thrift.client.core.bridge.NettyThriftBridge;

/**
 * date 20-6-1 下午4:59
 *
 * @author casper
 **/
public class NettyResponseHandle extends SimpleChannelInboundHandler<ByteBuf> {


    private final NettyThriftBridge<ByteBuf> bridge;

    public NettyResponseHandle(NettyThriftBridge<ByteBuf> bridge) {
        this.bridge = bridge;
    }

    /**
     * Is called for each message of type {@link I}.
     *
     * @param ctx the {@link ChannelHandlerContext} which this {@link SimpleChannelInboundHandler}
     *            belongs to
     * @param msg the message to handle
     * @throws Exception is thrown if an error occurred
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
        bridge.put(msg);
    }
}
