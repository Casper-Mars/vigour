package org.r.framework.thrift.client.core;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TMessage;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;
import org.r.framework.thrift.client.core.bridge.ThriftRequest;

/**
 * 实际上认为netty的channel对应thrift的transport
 * <p>
 * <p>
 * <p>
 * date 20-6-1 下午3:08
 *
 * @author casper
 **/
public class NettyTransportAdapter extends TTransport {


    private final Channel channel;
    private ByteBuf responedBuf;
    private final ThriftRequest bridge;

    public NettyTransportAdapter(Channel channel) {
        this.channel = channel;
        bridge = new ThriftRequest();
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
        try {
            channel.closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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

        if (!responedBuf.isReadable()) {
            responedBuf = bridge.get();
        }
        /*读取前获取读的指针位置*/
        int before = responedBuf.readerIndex();
        ByteBuf tmp = responedBuf.readBytes(buf, off, len);
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
        ByteBuf byteBuf = Unpooled.copiedBuffer(buf, off, len);
        channel.write(byteBuf);
    }

    /**
     * Flush any pending data out of a transport buffer.
     *
     * @throws TTransportException if there was an error writing out data.
     */
    @Override
    public void flush() throws TTransportException {

        int seqId = getSeqId(this.responedBuf);



        channel.flush();
    }


    private int getSeqId(ByteBuf byteBuf) {
        try {
            byteBuf.markReaderIndex();
            TTransport tmpTransport = new ByteBufTransport(byteBuf);
            TProtocol inputProtocol = new TBinaryProtocol(tmpTransport);
            TMessage message = inputProtocol.readMessageBegin();
            byteBuf.resetReaderIndex();
            return message.seqid;
        } catch (TException e) {
            e.printStackTrace();
            throw new RuntimeException("Could not find sequenceId in Thrift message", e);
        }
    }


}
