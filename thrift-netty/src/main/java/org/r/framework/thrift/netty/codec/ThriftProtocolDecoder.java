package org.r.framework.thrift.netty.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.TooLongFrameException;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.protocol.TProtocolUtil;
import org.apache.thrift.protocol.TType;
import org.apache.thrift.transport.TTransportException;
import org.r.framework.thrift.netty.NettyTransport;
import org.r.framework.thrift.netty.ThriftMessage;
import org.r.framework.thrift.netty.ThriftTransportType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 这里的原生的thrift消息类型分了了两种，一种是frame，一种是unframe。
 * frame是指一个完整的消息是有固定大小的，少了就填充。应用这个机制是为了解决粘包和拆包的问题，一般是由于用了netty做客户端的底层通讯框架
 * unframe是指每个消息没有大小固定的说明，就是普通的数据流。这种数据是会产生粘包和拆包的问题的，依然保留是为了兼容原生的thrift客户端
 * <p>
 * <p>
 * <p>
 * <p>
 * date 20-5-6 下午4:15
 *
 * @author casper
 **/
public class ThriftProtocolDecoder extends ChannelInboundHandlerAdapter {

    private final Logger log = LoggerFactory.getLogger(ThriftProtocolDecoder.class);

    public static final int MESSAGE_FRAME_SIZE = 4;
    public static final int REQUEST_ID_SIZE = 4;


    private final long maxFrameSize;


    public ThriftProtocolDecoder(long maxFrameSize) {
        this.maxFrameSize = maxFrameSize;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ThriftMessage message = null;
        log.info("get a request");
        if (msg instanceof ByteBuf) {
            ByteBuf buffer = (ByteBuf) msg;

            /*buffer不可读或者buffer的大小还不够一个int（用来存放数据长度的）*/
            if (!buffer.isReadable()) {
                return;
            }
            /*获取buffer的第一个8位无符号整形数作为判断的标志位*/
            short firstByte = buffer.getUnsignedByte(0);
            /*如果标志位大于等于128，则buffer的数据是原生的thrift请求。否则，是经过netty封装的数据*/

            if (firstByte >= 0x80) {
                ByteBuf messageBuffer = tryDecodeUnframedMessage(ctx, ctx.channel(), buffer);
                if (messageBuffer != null) {
                    // A non-zero MSB for the first byte of the message implies the message starts with a
                    // protocol id (and thus it is unframed).
                    message = new ThriftMessage(messageBuffer, ThriftTransportType.UNFRAMED);
                }
            } else if (buffer.readableBytes() < MESSAGE_FRAME_SIZE) {
                return;
            } else {
                message = tryDecodeFramedMessage(ctx, buffer);
            }

        }
        if (message == null) {
            super.channelRead(ctx, msg);
        } else {
            super.channelRead(ctx, message);
        }
    }

    protected ThriftMessage tryDecodeFramedMessage(ChannelHandlerContext ctx,
                                                   ByteBuf buffer) {
        // Framed messages are prefixed by the size of the frame (which doesn't include the
        // framing itself).

        int messageStartReaderIndex = buffer.readerIndex();
        int messageContentsOffset;

        /*
         * 第一个可读位置的4个字节代表的整形数是指这个frame的大小，这个4个字节不应该计算在数据中，应该排除出来。
         * 第二个整形数是请求的id，也是应该排除出来的
         * 所以消息数据的开始位置是第一个可读位置偏移8个字节
         *
         *
         *
         * */
        messageContentsOffset = messageStartReaderIndex + REQUEST_ID_SIZE;

        /*这个消息的大小是有效消息大小和有效消息大小指示量的大小之和，单位值字节*/
        int messageLength = buffer.readableBytes();
        /*真正的消息的大小*/
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
            int requestId = buffer.getInt(messageStartReaderIndex);
            ByteBuf messageBuffer = extractFrame(buffer,
                    messageContentsOffset,
                    messageContentsLength);
            buffer.readerIndex(messageStartReaderIndex + messageLength);
            if (messageBuffer == null) {
                return null;
            }
            log.info("get request[id:{} size:{}]", requestId, messageBuffer.readableBytes());
            return new ThriftMessage(messageBuffer, ThriftTransportType.FRAMED, requestId);
        }
    }

    protected ByteBuf tryDecodeUnframedMessage(ChannelHandlerContext ctx,
                                               Channel channel,
                                               ByteBuf buffer
    )
            throws TException {
        // Perform a trial decode, skipping through
        // the fields, to see whether we have an entire message available.

        int messageLength = 0;
        int messageStartReaderIndex = buffer.readerIndex();

        try {
            NettyTransport decodeAttemptTransport =
                    new NettyTransport(channel, buffer, ThriftTransportType.UNFRAMED);
            int initialReadBytes = decodeAttemptTransport.getReadByteCount();
            TProtocol inputProtocol = new TBinaryProtocol(decodeAttemptTransport);

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

    protected ByteBuf extractFrame(ByteBuf buffer, int index, int length) {
        // Slice should be sufficient here (and avoids the copy in LengthFieldBasedFrameDecoder)
        // because we know no one is going to modify the contents in the read buffers.
        return buffer.slice(index, length);
    }


}
