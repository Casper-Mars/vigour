package org.r.framework.thrift.netty.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.r.framework.thrift.netty.ThriftMessage;
import org.r.framework.thrift.netty.ThriftTransportType;
import org.r.framework.thrift.netty.client.ThriftRequestListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * date 2020/6/23 上午10:24
 *
 * @author casper
 **/
public class ThriftClientChannel extends NioSocketChannel {

    private final Logger log = LoggerFactory.getLogger(ThriftClientChannel.class);

    private final Map<Integer, ThriftRequestListener> requestMap;

    private final AtomicInteger requestId;

    /**
     * Create a new instance
     */
    public ThriftClientChannel() {
        this.requestMap = new ConcurrentHashMap<>();
        this.requestId = new AtomicInteger(1);
    }

    /**
     * 接收到消息事件
     *
     * @param msg 消息
     */
    public void onMsgRec(ThriftMessage msg) {
        ThriftRequestListener thriftRequestListener = requestMap.get(msg.getRequestId());
        if (thriftRequestListener != null) {
            thriftRequestListener.put(msg.getOriginBuf());
            requestMap.remove(msg.getRequestId());
        } else {
            log.error("can not find request[id:{}]", msg.getRequestId());
        }
    }

    /**
     * 发送消息
     *
     * @param msg 消息
     */
    public void sendMsg(ByteBuf msg, ThriftRequestListener thriftRequestListener) throws Exception {
        int requestId = getRequestId();
        this.requestMap.put(requestId, thriftRequestListener);
        ThriftMessage thriftMessage = new ThriftMessage(msg, ThriftTransportType.FRAMED, requestId);
        writeAndFlush(thriftMessage.getContent()).syncUninterruptibly();
    }

    private int getRequestId() {
        return this.requestId.getAndIncrement();
    }

}
