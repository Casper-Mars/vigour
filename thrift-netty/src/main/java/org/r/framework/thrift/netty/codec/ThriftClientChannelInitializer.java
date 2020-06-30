package org.r.framework.thrift.netty.codec;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldPrepender;
import org.r.framework.thrift.netty.ThriftMessageConstants;
import org.r.framework.thrift.netty.events.ChannelConnectEvent;
import org.r.framework.thrift.netty.events.Postman;

/**
 * date 2020/6/30 17:51
 *
 * @author casper
 */
public class ThriftClientChannelInitializer extends ChannelInitializer<NioSocketChannel> {

    private final int maxFrameSize;
    private final Postman<ChannelConnectEvent> postman;

    public ThriftClientChannelInitializer(int maxFrameSize, Postman<ChannelConnectEvent> postman) {
        this.maxFrameSize = maxFrameSize;
        this.postman = postman;
    }

    @Override
    protected void initChannel(NioSocketChannel ch) throws Exception {
        ChannelPipeline pi = ch.pipeline();
        pi.addLast(ConnectionHandler.class.getSimpleName(),new ConnectionHandler(postman.getMailBox()));
        pi.addLast(TcpPackFrameDecoder.class.getSimpleName(),new TcpPackFrameDecoder(maxFrameSize));
        pi.addLast("frameEncode",new LengthFieldPrepender(ThriftMessageConstants.LENGTH_FIELD_LENGTH));
        pi.addLast("clientHandler",new ThriftClientChannelHandler());
    }
}
