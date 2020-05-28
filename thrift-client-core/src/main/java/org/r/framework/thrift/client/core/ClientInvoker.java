package org.r.framework.thrift.client.core;

/**
 * date 2020/5/10 21:44
 *
 * @author casper
 */
public interface ClientInvoker {

    /**
     * 封装请求的逻辑
     *
     * @param client 客户端
     * @return
     */
    Object apply(Object client) throws Exception;

}
