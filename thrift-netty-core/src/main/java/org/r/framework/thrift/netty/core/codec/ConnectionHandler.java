package org.r.framework.thrift.netty.core.codec;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.r.framework.thrift.netty.core.events.ChannelConnectEvent;
import org.r.framework.thrift.netty.core.events.ChannelConnectionCloseEvent;
import org.r.framework.thrift.netty.core.events.MailBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

/**
 * date 20-5-7 下午2:52
 *
 * @author casper
 **/
public class ConnectionHandler extends ChannelInboundHandlerAdapter {

    private final Logger log = LoggerFactory.getLogger(ConnectionHandler.class);

    private final MailBox<ChannelConnectEvent> mailBox;

    public ConnectionHandler() {
        this(null);
    }

    public ConnectionHandler(MailBox<ChannelConnectEvent> mailBox) {
        this.mailBox = mailBox;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        InetSocketAddress address = (InetSocketAddress) ctx.channel().remoteAddress();
        log.info("get connection:" + address.getAddress().getHostAddress());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        InetSocketAddress address = (InetSocketAddress) ctx.channel().remoteAddress();
        log.info("connection " + address.getAddress().getHostAddress() + " disconnected");
        if (mailBox != null) {
            ChannelConnectionCloseEvent channelConnectionCloseEvent = new ChannelConnectionCloseEvent(address.getAddress().getHostAddress(), address.getPort());
            mailBox.putMail(channelConnectionCloseEvent);
        }
    }
}
