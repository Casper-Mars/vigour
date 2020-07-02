package org.r.framework.thrift.netty.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.r.framework.thrift.netty.core.codec.ThriftServerChannelInitializer;
import org.r.framework.thrift.netty.wrapper.ServerDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * date 2020/4/30 22:18
 *
 * @author casper
 */
public class NettyServer implements ServerDelegate {

    private final Logger log = LoggerFactory.getLogger(NettyServer.class);

    private Channel channel;
    private EventLoopGroup bossGroup;
    private EventLoopGroup workGroup;

    /**
     * 启动服务
     *
     * @param serverDefinition 服务信息
     */
    @Override
    public void start(ServerDefinition serverDefinition) {
        workGroup = new NioEventLoopGroup(serverDefinition.getNettyWorkPoolSize());
        bossGroup = new NioEventLoopGroup(serverDefinition.getNettyBossPoolSize());
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup, workGroup).channel(NioServerSocketChannel.class);
        bootstrap.localAddress(serverDefinition.getPort());
        bootstrap.childHandler(new ThriftServerChannelInitializer(serverDefinition.getMaxFrameSize(), serverDefinition.getProcessor()));
        try {
            channel = bootstrap.bind().sync().channel();
            log.info("Server start with netty at {}", serverDefinition.getPort());
            channel.closeFuture().sync();
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
        } finally {
            workGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    /**
     * 停止服务
     */
    @Override
    public void stop() {
        try {
            channel.closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            if (workGroup != null) {
                workGroup.shutdownGracefully();
            }
            if (bossGroup != null) {
                bossGroup.shutdownGracefully();
            }
        }
    }
}
