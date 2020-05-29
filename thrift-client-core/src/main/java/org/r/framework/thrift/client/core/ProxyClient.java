package org.r.framework.thrift.client.core;

import org.r.framework.thrift.client.core.manager.ClientManager;
import org.r.framework.thrift.client.core.thread.ClientExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.Future;

/**
 * date 20-5-8 下午2:44
 *
 * @author casper
 **/
public class ProxyClient implements MethodInterceptor {

    private final Logger log = LoggerFactory.getLogger(ProxyClient.class);

    private final String serverName;
    private final Class<?> serviceClass;
    private final ClientManager manager;
    private final Object fallback;

    public ProxyClient(String serverName, ClientManager manager, Object fallback, Class<?> serviceClass) {
        this.serverName = serverName;
        this.manager = manager;
        this.fallback = fallback;
        this.serviceClass = serviceClass;
        manager.addTargetService(serverName);
    }

    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        Object result = null;
        ClientExecutor client = manager.buildClient(serverName, serviceClass);
        if (client == null && fallback == null) {
            throw new RuntimeException("no client for this server:" + serverName);
        }
        boolean invokeSuccess = false;
        try {
            if (client != null) {
                Future<Object> submitTask = client.submitTask(t -> method.invoke(t, objects));
                result = submitTask.get();
                invokeSuccess = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error(String.format("can not finish the rpc request for method:%s with server:%s", method.getName(), serverName));
        }
        if (invokeSuccess) {
            return result;
        }
        if (fallback == null) {
            throw new RuntimeException("can not finish the rpc request and the fallback is null!!!!");
        }
        /*如果远程调用不成功，则转本地调用*/
        try {
            result = invokeFallback(method, objects);

        } catch (Exception e) {
            throw new RuntimeException("can not process the request!!!!even the fallback had failed");
        }
        return result;
    }


    private Object invokeFallback(Method method, Object[] objects) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        log.warn("circuit broken for server:" + serverName);
        Method fallbackMethod = fallback.getClass().getMethod(method.getName(), method.getParameterTypes());
        return fallbackMethod.invoke(fallback, objects);
    }


}
