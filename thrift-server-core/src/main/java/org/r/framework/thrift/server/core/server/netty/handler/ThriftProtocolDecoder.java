package org.r.framework.thrift.server.core.server.netty.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.TooLongFrameException;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.protocol.TProtocolFactory;
import org.apache.thrift.protocol.TProtocolUtil;
import org.apache.thrift.protocol.TType;
import org.apache.thrift.transport.TTransportException;
import org.r.framework.thrift.server.core.server.netty.core.DefaultTransport;
import org.r.framework.thrift.server.core.server.netty.core.ThriftMessage;
import org.r.framework.thrift.server.core.server.netty.core.ThriftTransportType;

/**
 * date 20-5-6 下午4:15
 *
 * @author casper
 **/
// TODO: 20-5-7 检查ByteBuf的创建，防止内存泄露
public class ThriftProtocolDecoder extends ChannelInboundHandlerAdapter {

    public static final int MESSAGE_FRAME_SIZE = 4;


    private long maxFrameSize;
    private TProtocolFactory inputProtocolFactory;

    public ThriftProtocolDecoder(long maxFrameSize, TProtocolFactory inputProtocolFactory) {
        this.maxFrameSize = maxFrameSize;
        this.inputProtocolFactory = inputProtocolFactory;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ThriftMessage message = null;
        if(msg instanceof ByteBuf){
            ByteBuf buffer = (ByteBuf)msg;

//            /*test begin*/
//
//            int len = buffer.readableBytes();
//            byte[] buf = new byte[len];
//            buffer.readBytes(buf,0,len);
//
//
//
//            /*test end*/
            if (!buffer.isReadable() || buffer.readableBytes() < MESSAGE_FRAME_SIZE) {
                return;
            }
            short firstByte = buffer.getUnsignedByte(0);
            if (firstByte >= 0x80) {
                ByteBuf messageBuffer = tryDecodeUnframedMessage(ctx, ctx.channel(), buffer, inputProtocolFactory);
                if (messageBuffer != null) {
                    // A non-zero MSB for the first byte of the message implies the message starts with a
                    // protocol id (and thus it is unframed).
                    message = new ThriftMessage(messageBuffer, ThriftTransportType.UNFRAMED);
                }
            }else {
                ByteBuf messageBuffer = tryDecodeFramedMessage(ctx, ctx.channel(), buffer, true);
                if (messageBuffer != null) {
                    // Messages with a zero MSB in the first byte are framed messages
                    message = new ThriftMessage(messageBuffer, ThriftTransportType.FRAMED);
                }
            }

        }
        if(message == null){
            super.channelRead(ctx, msg);
        }else {
            super.channelRead(ctx,message);
        }
    }

    protected ByteBuf tryDecodeFramedMessage(ChannelHandlerContext ctx,
                                             Channel channel,
                                             ByteBuf buffer,
                                             boolean stripFraming)
    {
        // Framed messages are prefixed by the size of the frame (which doesn't include the
        // framing itself).

        int messageStartReaderIndex = buffer.readerIndex();
        int messageContentsOffset;

        if (stripFraming) {
            messageContentsOffset = messageStartReaderIndex + MESSAGE_FRAME_SIZE;
        }
        else {
            messageContentsOffset = messageStartReaderIndex;
        }

        // The full message is larger by the size of the frame size prefix
        int messageLength = buffer.getInt(messageStartReaderIndex) + MESSAGE_FRAME_SIZE;
        int messageContentsLength = messageStartReaderIndex + messageLength - messageContentsOffset;

        if (messageContentsLength > maxFrameSize) {
            ctx.fireExceptionCaught(
                    new TooLongFrameException("Maximum frame size of " + maxFrameSize +
                            " exceeded")
            );
        }

        if (messageLength == 0) {
            // Zero-sized frame: just ignore it and return nothing
            buffer.readerIndex(messageContentsOffset);
            return null;
        } else if (buffer.readableBytes() < messageLength) {
            // Full message isn't available yet, return nothing for now
            return null;
        } else {
            // Full message is available, return it
            ByteBuf messageBuffer = extractFrame(buffer,
                    messageContentsOffset,
                    messageContentsLength);
            buffer.readerIndex(messageStartReaderIndex + messageLength);
            return messageBuffer;
        }
    }

    protected ByteBuf tryDecodeUnframedMessage(ChannelHandlerContext ctx,
                                               Channel channel,
                                               ByteBuf buffer,
                                               TProtocolFactory inputProtocolFactory)
            throws TException
    {
        // Perform a trial decode, skipping through
        // the fields, to see whether we have an entire message available.

        int messageLength = 0;
        int messageStartReaderIndex = buffer.readerIndex();

        try {
            DefaultTransport decodeAttemptTransport =
                    new DefaultTransport(channel, buffer, ThriftTransportType.UNFRAMED);
            int initialReadBytes = decodeAttemptTransport.getReadByteCount();
            TProtocol inputProtocol =
                    inputProtocolFactory.getProtocol(decodeAttemptTransport);

            // Skip through the message
            inputProtocol.readMessageBegin();
            TProtocolUtil.skip(inputProtocol, TType.STRUCT);
            inputProtocol.readMessageEnd();

            messageLength = decodeAttemptTransport.getReadByteCount() - initialReadBytes;
        } catch (TTransportException | IndexOutOfBoundsException e) {
            // No complete message was decoded: ran out of bytes
            return null;
        } finally {
            if (buffer.readerIndex() - messageStartReaderIndex > maxFrameSize) {
                ctx.fireExceptionCaught(
                        new TooLongFrameException("Maximum frame size of " + maxFrameSize + " exceeded")
                );
            }

            buffer.readerIndex(messageStartReaderIndex);
        }

        if (messageLength <= 0) {
            return null;
        }

        // We have a full message in the read buffer, slice it off
        ByteBuf messageBuffer =
                extractFrame(buffer, messageStartReaderIndex, messageLength);
        buffer.readerIndex(messageStartReaderIndex + messageLength);
        return messageBuffer;
    }

    protected ByteBuf extractFrame(ByteBuf buffer, int index, int length)
    {
        // Slice should be sufficient here (and avoids the copy in LengthFieldBasedFrameDecoder)
        // because we know no one is going to modify the contents in the read buffers.
        return buffer.slice(index, length);
    }


}
