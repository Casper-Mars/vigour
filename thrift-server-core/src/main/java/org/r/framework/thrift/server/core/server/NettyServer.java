package org.r.framework.thrift.server.core.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import org.r.framework.thrift.netty.ThriftChannelInitializer;
import org.r.framework.thrift.netty.codec.ThriftProtocolDecoder;
import org.r.framework.thrift.netty.codec.ThriftProtocolEncoder;
import org.r.framework.thrift.server.core.server.netty.handler.ConnectHandler;
import org.r.framework.thrift.server.core.server.netty.handler.MessageDispatcher;
import org.r.framework.thrift.server.core.wrapper.ServerDef;

/**
 * date 2020/4/30 22:18
 *
 * @author casper
 */
public class NettyServer implements ServerDelegate {

    // TFramedTransport framing appears at the front of the message
    private static final int LENGTH_FIELD_OFFSET = 0;

    // TFramedTransport framing is four bytes long
    private static final int LENGTH_FIELD_LENGTH = 4;

    // TFramedTransport framing represents message size *not including* framing so no adjustment
    // is necessary
    private static final int LENGTH_ADJUSTMENT = 0;

    // The client expects to see only the message *without* any framing, this strips it off
    private static final int INITIAL_BYTES_TO_STRIP = LENGTH_FIELD_LENGTH;


    @Override
    public void start(ServerDef serverDef) {
        EventLoopGroup workGroup = new NioEventLoopGroup();
        EventLoopGroup bossGroup = new NioEventLoopGroup(2);
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup, workGroup).channel(NioServerSocketChannel.class);
        bootstrap.localAddress(serverDef.getServerPort());
        bootstrap.childHandler(new ChannelInitializer<NioSocketChannel>() {
            @Override
            protected void initChannel(NioSocketChannel ch) throws Exception {
                ch.pipeline()
                        .addLast("frameDecode", new LengthFieldBasedFrameDecoder(
                                serverDef.getMaxFrameSize(),
                                LENGTH_FIELD_OFFSET,
                                LENGTH_FIELD_LENGTH,
                                LENGTH_ADJUSTMENT,
                                INITIAL_BYTES_TO_STRIP
                        ))
                        .addLast("thriftDecoder", new ThriftProtocolDecoder(serverDef.getMaxFrameSize()))
                        .addLast("thriftEncoder", new ThriftProtocolEncoder(serverDef.getMaxFrameSize()))
                        .addLast(ConnectHandler.class.getSimpleName(), new ConnectHandler())
                        .addLast(MessageDispatcher.class.getSimpleName(), new MessageDispatcher(serverDef));
            }
        });

        try {
            ChannelFuture sync = bootstrap.bind().sync();
            System.out.println("server start with netty at " + serverDef.getServerPort());
            sync.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            workGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }
}
