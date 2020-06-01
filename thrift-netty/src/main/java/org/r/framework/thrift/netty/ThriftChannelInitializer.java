package org.r.framework.thrift.netty;

import io.netty.channel.*;
import org.r.framework.thrift.netty.codec.ThriftProtocolDecoder;
import org.r.framework.thrift.netty.codec.ThriftProtocolEncoder;
import org.springframework.util.CollectionUtils;

import java.util.LinkedList;
import java.util.List;

/**
 * date 20-6-1 上午11:33
 *
 * @author casper
 **/
public class ThriftChannelInitializer extends ChannelInitializer {


    private Long maxFrameSize;

    private List<ChannelHandlerWrapper> handlerList;


    public ThriftChannelInitializer() {
        this(64 * 1024 * 1024L);
    }

    public ThriftChannelInitializer(long maxFrameSize) {
        this.maxFrameSize = maxFrameSize;
        this.handlerList = new LinkedList<>();
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
    protected void initChannel(Channel ch) throws Exception {
        ch.pipeline()
                .addLast("thriftDecoder", new ThriftProtocolDecoder(maxFrameSize))
                .addLast("thriftEncoder", new ThriftProtocolEncoder(maxFrameSize));
        if (!CollectionUtils.isEmpty(handlerList)) {
            for (ChannelHandlerWrapper wrapper : handlerList) {
                ch.pipeline().addLast(wrapper.getName(), wrapper.getHandler());
            }
        }
    }

    /**
     * 在尾部插入处理器
     *
     * @param name    处理器名称
     * @param handler 处理器
     * @return
     */
    public ThriftChannelInitializer addLast(String name, ChannelHandler handler) {
        handlerList.add(new ChannelHandlerWrapper(name, handler));
        return this;
    }


    private class ChannelHandlerWrapper {

        private final String name;
        private final ChannelHandler handler;

        public ChannelHandlerWrapper(String name, ChannelHandler handler) {
            this.name = name;
            this.handler = handler;
        }

        public String getName() {
            return name;
        }

        public ChannelHandler getHandler() {
            return handler;
        }
    }


}
