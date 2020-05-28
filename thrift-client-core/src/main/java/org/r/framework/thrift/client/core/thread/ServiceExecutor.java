package org.r.framework.thrift.client.core.thread;

import org.r.framework.thrift.client.core.ClientInvoker;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.*;

/**
 * date 2020/5/10 21:17
 *
 * @author casper
 */
public class ServiceExecutor implements Closeable {


    private final ExecutorService executorService;


    public ServiceExecutor(String serverName) {
        this.executorService = new ThreadPoolExecutor(
                1,
                1,
                1,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(),
                r -> new Thread(r, "serverExecutor-" + serverName),
                new ThreadPoolExecutor.DiscardOldestPolicy()
        );
    }

    /**
     * 提交接口调用的任务
     *
     * @param invoker 调用的逻辑
     * @return
     */
    public Future<Object> submitTask(Object client, ClientInvoker invoker) {
        return this.executorService.submit(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                return invoker.apply(client);
            }
        });
    }


    @Override
    public void close() throws IOException {
        try {
            this.executorService.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
            this.executorService.shutdown();
        }
    }
}
