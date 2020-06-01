package org.r.framework.thrift.client.core;

import io.netty.buffer.ByteBuf;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

/**
 * date 20-6-1 下午5:41
 *
 * @author casper
 **/
public class ByteBufTransport extends TTransport {


    private final ByteBuf byteBuf;

    public ByteBufTransport(ByteBuf byteBuf) {
        this.byteBuf = byteBuf;
    }

    /**
     * Queries whether the transport is open.
     *
     * @return True if the transport is open.
     */
    @Override
    public boolean isOpen() {
        throw new UnsupportedOperationException();
    }

    /**
     * Opens the transport for reading/writing.
     *
     * @throws TTransportException if the transport could not be opened
     */
    @Override
    public void open() throws TTransportException {
        throw new UnsupportedOperationException();
    }

    /**
     * Closes the transport.
     */
    @Override
    public void close() {
        throw new UnsupportedOperationException();
    }

    /**
     * Reads up to len bytes into buffer buf, starting at offset off.
     *
     * @param buf Array to read into
     * @param off Index to start reading at
     * @param len Maximum number of bytes to read
     * @return The number of bytes actually read
     * @throws TTransportException if there was an error reading data
     */
    @Override
    public int read(byte[] buf, int off, int len) throws TTransportException {
        byteBuf.readBytes(buf, off, len);
        return len;
    }

    /**
     * Writes up to len bytes from the buffer.
     *
     * @param buf The output data buffer
     * @param off The offset to start writing from
     * @param len The number of bytes to write
     * @throws TTransportException if there was an error writing data
     */
    @Override
    public void write(byte[] buf, int off, int len) throws TTransportException {
        byteBuf.writeBytes(buf, off, len);
    }
}
