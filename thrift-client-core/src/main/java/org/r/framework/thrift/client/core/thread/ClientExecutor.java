package org.r.framework.thrift.client.core.thread;

import org.r.framework.thrift.client.core.ClientInvoker;

import java.util.concurrent.Future;

/**
 * date 20-5-27 上午9:46
 *
 * @author casper
 **/
public class ClientExecutor {


    /**
     * 实例化出来的服务实现类
     */
    private final Object client;

    /**
     * 对应一个transport的单线程处理线程池
     */
    private final ServiceExecutor serviceExecutor;

    public ClientExecutor(Object client, ServiceExecutor serviceExecutor) {
        this.client = client;
        this.serviceExecutor = serviceExecutor;
    }

    /**
     * 提交接口调用的任务
     *
     * @param invoker 调用的逻辑
     * @return
     */
    public Future<Object> submitTask(ClientInvoker invoker) {
        return this.serviceExecutor.submitTask(client, invoker);
    }


}
