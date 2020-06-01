package org.r.framework.thrift.server.core.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.r.framework.thrift.netty.ThriftChannelInitializer;
import org.r.framework.thrift.server.core.server.netty.handler.ConnectHandler;
import org.r.framework.thrift.server.core.server.netty.handler.MessageDispatcher;
import org.r.framework.thrift.server.core.wrapper.ServerDef;

/**
 * date 2020/4/30 22:18
 *
 * @author casper
 */
public class NettyServer implements ServerDelegate {


    @Override
    public void start(ServerDef serverDef) {
        EventLoopGroup workGroup = new NioEventLoopGroup();
        EventLoopGroup bossGroup = new NioEventLoopGroup(2);
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup, workGroup).channel(NioServerSocketChannel.class);
        bootstrap.localAddress(serverDef.getServerPort());
        ThriftChannelInitializer channelInitializer  = new ThriftChannelInitializer();
        channelInitializer.addLast(ConnectHandler.class.getSimpleName(),new ConnectHandler());
        channelInitializer.addLast(MessageDispatcher.class.getSimpleName(),new MessageDispatcher(serverDef));
        bootstrap.childHandler(channelInitializer);

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
