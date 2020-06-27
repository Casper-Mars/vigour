package org.r.framework.thrift.client.core.channel;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.r.framework.thrift.client.core.thrift.ThriftRequestListener;
import org.r.framework.thrift.netty.ThriftMessage;
import org.r.framework.thrift.netty.ThriftTransportType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * date 2020/6/23 上午10:24
 *
 * @author casper
 **/
public class ThriftNettyChannel extends NioSocketChannel {

    private final Logger log = LoggerFactory.getLogger(ThriftNettyChannel.class);

    private final Map<Integer, ThriftRequestListener> requestMap;

    private final AtomicInteger requestId;

    /**
     * 1.使用信号量为了保证底层的tcp数据包能够按照正确的顺序执行，即发-收-确认的三个包完成一次rpc调用。如果没有此信号量控制数据的写入，发送的tcp包就会乱了，导致有些rpc请求完成不了
     * 2.每写入一个请求的数据，就要等待这个数据返回才能释放信号量。因此，如果服务端无法响应tcp数据包，则信号量无法释放，会导致整个channel挂起
     * 3.此处应该添加一个计时器
     */
    private final Semaphore semaphore;

    /**
     * Create a new instance
     */
    public ThriftNettyChannel() {
        this.requestMap = new ConcurrentHashMap<>();
        this.requestId = new AtomicInteger(1);
        this.semaphore = new Semaphore(1);
    }

    /**
     * 接收到消息事件
     *
     * @param msg 消息
     */
    public void onMsgRec(ThriftMessage msg) {
        this.semaphore.release();
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
        semaphore.acquire();
        int requestId = getRequestId();
        this.requestMap.put(requestId, thriftRequestListener);
        ThriftMessage thriftMessage = new ThriftMessage(msg, ThriftTransportType.FRAMED, requestId);
        writeAndFlush(thriftMessage.getContent());
    }

    private int getRequestId() {
        return this.requestId.getAndIncrement();
    }

}
