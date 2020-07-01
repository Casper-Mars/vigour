package org.r.framework.thrift.netty.core.client;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;
import org.r.framework.thrift.netty.core.codec.ThriftClientChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeoutException;

/**
 * 实际上认为netty的channel对应thrift的transport
 * <p>
 * date 20-6-1 下午3:08
 *
 * @author casper
 **/
public class ClientNettyTransport extends TTransport {

    private final Logger log = LoggerFactory.getLogger(ClientNettyTransport.class);

    private final ThriftClientChannel channel;
    private ByteBuf respondBuf;
    private final LinkedBlockingQueue<ThriftRequestListener> requestQueue;
    private final ByteBuf innerBuffer;

    public ClientNettyTransport(ThriftClientChannel channel) {
        this.channel = channel;
        requestQueue = new LinkedBlockingQueue<>();
        innerBuffer = Unpooled.buffer();
    }

    /**
     * Queries whether the transport is open.
     *
     * @return True if the transport is open.
     */
    @Override
    public boolean isOpen() {
        return channel.isOpen();
    }

    /**
     * Opens the transport for reading/writing.
     *
     * @throws TTransportException if the transport could not be opened
     */
    @Override
    public void open() throws TTransportException {

    }

    /**
     * Closes the transport.
     */
    @Override
    public void close() {
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

        if (respondBuf == null || !respondBuf.isReadable()) {
            try {
                ThriftRequestListener poll = requestQueue.take();
                respondBuf = poll.get();
                if (respondBuf == null) {
                    throw new TTransportException();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                throw new TTransportException();
            } catch (TimeoutException e) {
                e.printStackTrace();
            }
        }
        /*读取前获取读的指针位置*/
        int before = respondBuf.readerIndex();
        ByteBuf tmp = respondBuf.readBytes(buf, off, len);
        /*读取后获取读的指针位置*/
        int after = tmp.readerIndex();
        /*返回读取的字节数*/
        return after - before;
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
        innerBuffer.writeBytes(buf, off, len);
    }

    /**
     * Flush any pending data out of a transport buffer.
     *
     * @throws TTransportException if there was an error writing out data.
     */
    @Override
    public void flush() throws TTransportException {
        try {
            ThriftRequestListener thriftRequestListener = new ThriftRequestListener();
            requestQueue.add(thriftRequestListener);
            channel.sendMsg(innerBuffer.copy(), thriftRequestListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
        innerBuffer.clear();
    }

}
