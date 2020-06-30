package org.r.framework.thrift.netty.codec;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.apache.thrift.TProcessor;

/**
 * date 2020/6/30 15:31
 *
 * @author casper
 */
public class ThriftServerChannelInitializer extends ChannelInitializer<NioSocketChannel> {

    private final int maxFrameSize;
    private final TProcessor processor;

    public ThriftServerChannelInitializer(int maxFrameSize, TProcessor processor) {
        this.maxFrameSize = maxFrameSize;
        this.processor = processor;
    }

    /**
     * This method will be called once the {@link Channel} was registered. After the method returns this instance
     * will be removed from the {@link ChannelPipeline} of the {@link Channel}.
     *
     * @param ch the {@link Channel} which was registered.
     * @throws Exception is thrown if an error occurs. In that case it will be handled by
     *                   {@link #exceptionCaught(ChannelHandlerContext, Throwable)} which will by default close
     *                   the {@link Channel}.
     */
    @Override
    protected void initChannel(NioSocketChannel ch) throws Exception {
        ChannelPipeline pi = ch.pipeline();
        pi.addLast(TcpPackFrameDecoder.class.getSimpleName(), new TcpPackFrameDecoder(maxFrameSize))
                .addLast("thriftDecoder", new ThriftProtocolDecoder(maxFrameSize))
                .addLast("thriftEncoder", new ThriftProtocolEncoder(maxFrameSize))
                .addLast(ConnectionHandler.class.getSimpleName(), new ConnectionHandler())
                .addLast(ThriftRPCHandler.class.getSimpleName(), new ThriftRPCHandler(processor));
    }
}
