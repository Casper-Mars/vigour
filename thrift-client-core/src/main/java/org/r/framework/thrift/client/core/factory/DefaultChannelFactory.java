package org.r.framework.thrift.client.core.factory;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import org.r.framework.thrift.client.core.channel.ThriftChannelHandler;
import org.r.framework.thrift.client.core.channel.ThriftNettyChannel;
import org.r.framework.thrift.client.core.channel.ThriftServerConnectHandler;
import org.r.framework.thrift.client.core.config.ConfigProperties;
import org.r.framework.thrift.client.core.exception.ChannelOpenFailException;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

/**
 * date 20-6-1 上午11:09
 *
 * @author casper
 **/
public class DefaultChannelFactory implements ChannelFactory {

    // TFramedTransport framing appears at the front of the message
    private static final int LENGTH_FIELD_OFFSET = 0;

    // TFramedTransport framing is four bytes long
    private static final int LENGTH_FIELD_LENGTH = 4;

    // TFramedTransport framing represents message size *not including* framing so no adjustment
    // is necessary
    private static final int LENGTH_ADJUSTMENT = 0;

    // The client expects to see only the message *without* any framing, this strips it off
    private static final int INITIAL_BYTES_TO_STRIP = LENGTH_FIELD_LENGTH;

    private final Bootstrap bootstrap;

    public DefaultChannelFactory(ConfigProperties configProperties) {
        EventLoopGroup workThreads = new NioEventLoopGroup(configProperties.getWorkThreads());
        bootstrap = new Bootstrap();
        bootstrap.group(workThreads).channel(ThriftNettyChannel.class);
        bootstrap.handler(new ChannelInitializer<ThriftNettyChannel>() {
            @Override
            protected void initChannel(ThriftNettyChannel ch) throws Exception {
                ChannelPipeline pipeline = ch.pipeline();
                pipeline
                        .addLast("connectHandler", new ThriftServerConnectHandler())
                        .addLast("frameEncode", new LengthFieldPrepender(LENGTH_FIELD_LENGTH))
                        .addLast("frameDecode", new LengthFieldBasedFrameDecoder(
                                configProperties.getMaxFrameSize(),
                                LENGTH_FIELD_OFFSET,
                                LENGTH_FIELD_LENGTH,
                                LENGTH_ADJUSTMENT,
                                INITIAL_BYTES_TO_STRIP
                        ))
                        .addLast("thriftAdapter", new ThriftChannelHandler());
            }
        });


    }

    /**
     * 建立channel
     *
     * @param ip   远程主机ip
     * @param port 远程进程端口
     * @return
     */
    public ThriftNettyChannel build(String ip, int port) throws ChannelOpenFailException {
        SocketAddress socketAddress = new InetSocketAddress(ip, port);
        ChannelFuture connect = bootstrap.connect(socketAddress);
        try {
            return (ThriftNettyChannel) connect.sync().channel();
        } catch (InterruptedException e) {
            throw new ChannelOpenFailException(e);
        }
    }


}
