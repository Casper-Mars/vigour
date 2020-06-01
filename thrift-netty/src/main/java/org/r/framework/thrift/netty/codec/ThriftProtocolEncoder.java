package org.r.framework.thrift.netty.codec;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.handler.codec.TooLongFrameException;
import org.r.framework.thrift.netty.ThriftMessage;

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
        int frameSize = message.getBuffer().readableBytes();

        if (message.getBuffer().readableBytes() > maxFrameSize) {
            ctx.fireExceptionCaught(new TooLongFrameException(
                    String.format(
                            "Frame size exceeded on encode: frame was %d bytes, maximum allowed is %d bytes",
                            frameSize,
                            maxFrameSize)));
            return;
        }

        switch (message.getTransportType()) {
            case UNFRAMED:
                out.writeBytes(message.getBuffer());
                break;
            case FRAMED:
                ByteBuf frameSizeBuffer = PooledByteBufAllocator.DEFAULT.buffer(4);
                frameSizeBuffer.writeInt(message.getBuffer().readableBytes());
                out.writeBytes(frameSizeBuffer);
                out.writeBytes(message.getBuffer());

            default:
                throw new UnsupportedOperationException("Unrecognized transport type");
        }


    }
}
