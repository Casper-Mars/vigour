package org.r.framework.thrift.client.core.thrift;

import com.google.common.util.concurrent.SettableFuture;
import io.netty.buffer.ByteBuf;

import java.util.concurrent.ExecutionException;

/**
 * date 20-6-1 下午5:17
 *
 * @author casper
 **/
public class ThriftRequest {

    /**
     * 内部同步对象
     */
    private final SettableFuture<ByteBuf> innerObject;

    public ThriftRequest() {
        innerObject = SettableFuture.create();
    }


    /**
     * 从桥中获取
     *
     * @return
     */
    public ByteBuf get() {
        try {
            return innerObject.get();
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
