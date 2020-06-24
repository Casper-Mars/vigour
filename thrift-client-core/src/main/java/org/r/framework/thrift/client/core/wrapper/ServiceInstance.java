package org.r.framework.thrift.client.core.wrapper;

import org.r.framework.thrift.client.core.factory.ThriftClientFactory;

/**
 * date 2020/5/24 23:41
 *
 * @author casper
 */
public class ServiceInstance {

    private final ServiceWrapper serviceWrapper;
    private final ThriftClientFactory thriftClientFactory;
    private final ThreadLocal<Object> serviceProxy;

    public ServiceInstance(ServiceWrapper serviceWrapper, ThriftClientFactory thriftClientFactory) {
        this.serviceWrapper = serviceWrapper;
        this.thriftClientFactor y = thriftClientFactory;
        this.serviceProxy = new ThreadLocal<>();
    }

    /**
     * 构建服务的代理类
     *
     * @param clazz 服务实现类
     * @return
     */
    public Object build(Class<?> clazz) {
        Object proxy = this.serviceProxy.get();
        if (proxy == null) {
            proxy = thriftClientFactory.buildClient(clazz, serviceWrapper.getName(), serviceWrapper.getHost(), serviceWrapper.getPort());
            this.serviceProxy.set(proxy);
        }
        return proxy;
    }







}
