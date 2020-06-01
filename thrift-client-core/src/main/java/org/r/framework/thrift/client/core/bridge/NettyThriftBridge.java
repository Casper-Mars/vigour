package org.r.framework.thrift.client.core.bridge;

/**
 * date 20-6-1 下午3:44
 *
 * @author casper
 **/
public interface NettyThriftBridge<T> {


    /**
     * 从桥中获取
     *
     * @return
     */
    T get();


    /**
     * 放入桥中
     *
     * @param data 数据
     */
    void put(T data);


}
