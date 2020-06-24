package org.r.framework.thrift.client.core;

import org.r.framework.thrift.client.core.manager.DefaultServerManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * date 20-5-8 下午2:44
 *
 * @author casper
 **/
public class ProxyClient implements MethodInterceptor {

    private final Logger log = LoggerFactory.getLogger(ProxyClient.class);

    private final String serverName;
    private final Class<?> serviceClass;
    private final DefaultServerManager manager;
    private final Object fallback;

    public ProxyClient(String serverName, DefaultServerManager manager, Object fallback, Class<?> serviceClass) {
        this.serverName = serverName;
        this.manager = manager;
        this.fallback = fallback;
        this.serviceClass = serviceClass;
        manager.addTargetServer(serverName);
    }

    /**
     * 拦截方法执行
     *
     * @param o
     * @param method
     * @param objects
     * @param methodProxy
     * @return
     * @throws Throwable
     */
    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        Object result = null;
        Object client = manager.getServer(serverName, serviceClass);
        if (client == null && fallback == null) {
            throw new RuntimeException("no client for this server:" + serverName);
        }
        boolean invokeSuccess = false;
        try {
            if (client != null) {
                result = method.invoke(client, objects);
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


    /**
     * 调用本地的熔断器
     *
     * @param method  方法名称
     * @param objects 参数
     * @return
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    private Object invokeFallback(Method method, Object[] objects) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        log.warn("circuit broken for server:" + serverName);
        Method fallbackMethod = fallback.getClass().getMethod(method.getName(), method.getParameterTypes());
        return fallbackMethod.invoke(fallback, objects);
    }


}
