package org.r.framework.thrift.netty.core.client;

import com.google.common.util.concurrent.SettableFuture;
import io.netty.buffer.ByteBuf;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * date 20-6-1 下午5:17
 *
 * @author casper
 **/
public class ThriftRequestListener {

    /**
     * 内部同步对象
     */
    private final SettableFuture<ByteBuf> innerObject;
    private final int timeOut;

    public ThriftRequestListener() {
        this(SettableFuture.create(), -1);
    }

    public ThriftRequestListener(SettableFuture<ByteBuf> innerObject, int timeOut) {
        this.innerObject = innerObject;
        this.timeOut = timeOut;
    }

    /**
     * 从桥中获取
     *
     * @return
     */
    public ByteBuf get() throws TimeoutException {
        try {
            if (timeOut == -1) {
                return innerObject.get();
            } else {
                return innerObject.get(10, TimeUnit.SECONDS);
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 放入桥中
     *
     * @param data 数据
     */
    public void put(ByteBuf data) {
        innerObject.set(data);
    }


}
