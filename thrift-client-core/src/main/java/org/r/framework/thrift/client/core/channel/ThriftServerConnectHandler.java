package org.r.framework.thrift.client.core.channel;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.r.framework.thrift.client.core.event.ChannelCloseEvent;
import org.r.framework.thrift.client.core.observer.MailBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

/**
 * date 2020/6/24 上午10:36
 *
 * @author casper
 **/
public class ThriftServerConnectHandler extends ChannelInboundHandlerAdapter {

    private final Logger log = LoggerFactory.getLogger(ThriftServerConnectHandler.class);

    private final MailBox<ChannelCloseEvent> mailBox;

    public ThriftServerConnectHandler(MailBox<ChannelCloseEvent> mailBox) {
        this.mailBox = mailBox;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        if (channel instanceof ThriftNettyChannel) {
            ThriftNettyChannel tmp = (ThriftNettyChannel) channel;
            InetSocketAddress serverAddress = tmp.remoteAddress();
            log.info("[netty-thrift]Server:{}:{} is connected", serverAddress.getHostString(), serverAddress.getPort());
        }
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        if (channel instanceof ThriftNettyChannel) {
            ThriftNettyChannel tmp = (ThriftNettyChannel) channel;
            InetSocketAddress serverAddress = tmp.remoteAddress();
            log.info("[netty-thrift]Server:{}:{} is down", serverAddress.getAddress().getHostAddress(), serverAddress.getPort());
            ChannelCloseEvent channelCloseEvent = new ChannelCloseEvent(serverAddress.getAddress().getHostAddress(), serverAddress.getPort());
            mailBox.putMail(channelCloseEvent);
        }
        super.channelInactive(ctx);
    }

}
