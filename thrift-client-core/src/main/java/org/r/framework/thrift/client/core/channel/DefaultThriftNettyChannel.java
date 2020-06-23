package org.r.framework.thrift.client.core.channel;

import io.netty.buffer.ByteBuf;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TMessage;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TTransport;
import org.r.framework.thrift.client.core.ByteBufTransport;
import org.r.framework.thrift.client.core.thrift.ThriftRequest;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * date 2020/6/23 上午10:24
 *
 * @author casper
 **/
public class DefaultThriftNettyChannel extends NioSocketChannel implements ThriftNettyChannel {


    private final Map<Integer, ThriftRequest> requestMap;

    /**
     * Create a new instance
     */
    public DefaultThriftNettyChannel() {
        this.requestMap = new ConcurrentHashMap<>();
    }

    /**
     * 接收到消息事件
     *
     * @param msg 消息
     */
    @Override
    public void onMsgRec(ByteBuf msg) {
        int seqId = getSeqId(msg);
        ThriftRequest thriftRequest = requestMap.get(seqId);
        if (thriftRequest != null) {
            thriftRequest.put(msg);
        }
    }

    /**
     * 发送消息
     *
     * @param msg 消息
     */
    @Override
    public ThriftRequest sendMsg(ByteBuf msg) throws Exception {
        ThriftRequest thriftRequest = new ThriftRequest();
        int seqId = getSeqId(msg);
        this.requestMap.put(seqId, thriftRequest);
        doWriteBytes(msg);
        return thriftRequest;
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
