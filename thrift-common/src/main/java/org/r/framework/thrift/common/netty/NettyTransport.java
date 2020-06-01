package org.r.framework.thrift.common.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import org.apache.thrift.TApplicationException;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

/**
 * date 2020/5/30 21:27
 *
 * @author casper
 */
public class NettyTransport extends TTransport {
    private final Channel channel;
    private final ByteBuf in;
    private final ThriftTransportType thriftTransportType;
    private ByteBuf out;
    private static final int DEFAULT_OUTPUT_BUFFER_SIZE = 1024;
    private final int initialReaderIndex;
    private final int initialBufferPosition;
    private int bufferPosition;
    private int bufferEnd;
    private final byte[] buffer;
    private TApplicationException tApplicationException;

    public NettyTransport(Channel channel,
                            ByteBuf in,
                            ThriftTransportType thriftTransportType)
    {
        this.channel = channel;
        this.in = in;
        this.thriftTransportType = thriftTransportType;
        this.out = PooledByteBufAllocator.DEFAULT.buffer(DEFAULT_OUTPUT_BUFFER_SIZE);
        this.initialReaderIndex = in.readerIndex();

        if (!in.hasArray()) {
            buffer = null;
            bufferPosition = 0;
            initialBufferPosition = bufferEnd = -1;
        }
        else {
            buffer = in.array();
            initialBufferPosition = bufferPosition = in.arrayOffset() + in.readerIndex();
            bufferEnd = bufferPosition + in.readableBytes();
            // Without this, reading from a !in.hasArray() buffer will advance the readerIndex
            // of the buffer, while reading from a in.hasArray() buffer will not advance the
            // readerIndex, and this has led to subtle bugs. This should help to identify
            // those problems by making things more consistent.
            in.readerIndex(in.readerIndex() + in.readableBytes());
        }
    }

    public NettyTransport(Channel channel, ThriftMessage message)
    {
        this(channel, message.getBuffer(), message.getTransportType());
    }

    @Override
    public boolean isOpen()
    {
        return channel.isOpen();
    }

    @Override
    public void open()
            throws TTransportException
    {
        // no-op
    }

    @Override
    public void close()
    {
        // no-op
        channel.close();
    }

    @Override
    public int read(byte[] bytes, int offset, int length)
            throws TTransportException
    {
        if (getBytesRemainingInBuffer() >= 0) {
            int _read = Math.min(getBytesRemainingInBuffer(), length);
            System.arraycopy(getBuffer(), getBufferPosition(), bytes, offset, _read);
            consumeBuffer(_read);
            return _read;
        }
        else {
            int _read = Math.min(in.readableBytes(), length);
            in.readBytes(bytes, offset, _read);
            return _read;
        }
    }

    @Override
    public int readAll(byte[] bytes, int offset, int length) throws TTransportException {
        if (read(bytes, offset, length) < length) {
            throw new TTransportException("Buffer doesn't have enough bytes to read");
        }
        return length;
    }

    @Override
    public void write(byte[] bytes, int offset, int length)
            throws TTransportException
    {
        out.writeBytes(bytes, offset, length);
    }

    public ByteBuf getOutputBuffer()
    {
        return out;
    }

    public void setOutputBuffer(ByteBuf buf) {
        out = buf;
    }

    public ThriftTransportType getTransportType() {
        return thriftTransportType;
    }

    @Override
    public void flush()
            throws TTransportException
    {
        // Flush is a no-op: MessageDispatcher will write the response to the Channel, in order to
        // guarantee ordering of responses when required.
    }

    @Override
    public void consumeBuffer(int len)
    {
        bufferPosition += len;
    }

    @Override
//    @edu.umd.cs.findbugs.annotations.SuppressWarnings("EI_EXPOSE_REP")
    public byte[] getBuffer()
    {
        return buffer;
    }

    @Override
    public int getBufferPosition()
    {
        return bufferPosition;
    }

    @Override
    public int getBytesRemainingInBuffer()
    {
        return bufferEnd - bufferPosition;
    }

    public int getReadByteCount()
    {
        if (getBytesRemainingInBuffer() >= 0) {
            return getBufferPosition() - initialBufferPosition;
        }
        else {
            return in.readerIndex() - initialReaderIndex;
        }
    }

    public int getWrittenByteCount()
    {
        return getOutputBuffer().writerIndex();
    }

    public void setTApplicationException(TApplicationException e) {
        tApplicationException = e;
    }

    public TApplicationException getTApplicationException() {
        return tApplicationException;
    }
}
