package org.r.framework.thrift.netty.core.codec;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.handler.codec.TooLongFrameException;
import org.r.framework.thrift.netty.core.ThriftMessage;

/**
 * date 20-5-6 下午4:38
 *
 * @author casper
 **/
public class ThriftProtocolEncoder extends MessageToByteEncoder<ThriftMessage> {


    private final long maxFrameSize;

    public ThriftProtocolEncoder() {
        this.maxFrameSize = 64 * 1024 * 1024L;
    }


    public ThriftProtocolEncoder(long maxFrameSize) {
        this.maxFrameSize = maxFrameSize;
    }

    /**
     * Encode a message into a {@link ByteBuf}. This method will be called for each written message that can be handled
     * by this encoder.
     *
     * @param ctx     the {@link ChannelHandlerContext} which this {@link MessageToByteEncoder} belongs to
     * @param message the message to encode
     * @param out     the {@link ByteBuf} into which the encoded message will be written
     * @throws Exception is thrown if an error occurs
     */
    @Override
    protected void encode(ChannelHandlerContext ctx, ThriftMessage message, ByteBuf out) throws Exception {
        int frameSize = message.getOriginBuf().readableBytes();

        if (message.getOriginBuf().readableBytes() > maxFrameSize) {
            ctx.fireExceptionCaught(new TooLongFrameException(
                    String.format(
                            "Frame size exceeded on encode: frame was %d bytes, maximum allowed is %d bytes",
                            frameSize,
                            maxFrameSize)));
            return;
        }

        switch (message.getTransportType()) {
            case UNFRAMED:
                out.writeBytes(message.getOriginBuf());
                break;
            case FRAMED:
                /*写入有效消息的长度*/
                ByteBuf frameSizeBuffer = PooledByteBufAllocator.DEFAULT.buffer(4);
                ByteBuf content = message.getContent();
                frameSizeBuffer.writeInt(content.readableBytes());
                out.writeBytes(frameSizeBuffer);
                /*写入有效消息*/
                out.writeBytes(content);
                break;
            default:
                throw new UnsupportedOperationException("Unrecognized transport type");
        }


    }
}
