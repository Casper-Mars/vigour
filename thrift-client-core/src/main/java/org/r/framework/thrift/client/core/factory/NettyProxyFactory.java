package org.r.framework.thrift.client.core.factory;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import org.r.framework.thrift.netty.ThriftChannelInitializer;

/**
 *
 * date 20-6-1 上午11:09
 *
 * @author casper
 **/
public class NettyProxyFactory {


    public Bootstrap buildProxy(EventLoopGroup workThreads){
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(workThreads).channel(SocketChannel.class);
        ThriftChannelInitializer channelInitializer = new ThriftChannelInitializer();
        bootstrap.handler(channelInitializer);
        return bootstrap;
    }




}
