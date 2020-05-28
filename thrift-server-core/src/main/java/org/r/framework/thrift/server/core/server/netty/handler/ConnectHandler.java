package org.r.framework.thrift.server.core.server.netty.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

/**
 * date 20-5-7 下午2:52
 *
 * @author casper
 **/
public class ConnectHandler extends ChannelInboundHandlerAdapter {

    private final Logger log = LoggerFactory.getLogger(ConnectHandler.class);


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        InetSocketAddress address = (InetSocketAddress) ctx.channel().remoteAddress();
        log.info("get connection:" + address.getAddress().getHostAddress());
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        InetSocketAddress address = (InetSocketAddress) ctx.channel().remoteAddress();
        log.info("connection " + address.getAddress().getHostAddress() + " disconnected");
        super.channelInactive(ctx);
    }
}
