package org.r.framework.thrift.client.core.bridge;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * date 20-6-1 下午5:17
 *
 * @author casper
 **/
public class BlockingNettyThriftBridge<T> implements NettyThriftBridge<T> {


    private final BlockingQueue<T> queue;

    public BlockingNettyThriftBridge() {
        this(new LinkedBlockingQueue<>());
    }

    public BlockingNettyThriftBridge(BlockingQueue<T> queue) {
        this.queue = queue;
    }

    /**
     * 从桥中获取
     *
     * @return
     */
    @Override
    public T get() {
        try {
            return queue.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 放入桥中
     *
     * @param data 数据
     */
    @Override
    public void put(T data) {
        try {
            queue.put(data);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


}
