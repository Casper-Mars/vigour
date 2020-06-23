package org.r.framework.thrift.client.core.factory;

import org.r.framework.thrift.client.core.wrapper.ServiceWrapper;

/**
 * date 2020/5/24 23:41
 *
 * @author casper
 */
public class DefaultServiceFactory implements ServiceFactory {

    private final ServiceWrapper serviceWrapper;
    private final ThriftClientFactory thriftClientFactory;
    private final ThreadLocal<Object> serviceProxy;

    public DefaultServiceFactory(ServiceWrapper serviceWrapper, ThriftClientFactory thriftClientFactory) {
        this.serviceWrapper = serviceWrapper;
        this.thriftClientFactory = thriftClientFactory;
        this.serviceProxy = new ThreadLocal<>();
    }

    /**
     * 构建服务的代理类
     *
     * @param clazz 服务实现类
     * @return
     */
    @Override
    public Object buildServerProxy(Class<?> clazz) {
        Object proxy = this.serviceProxy.get();
        if (proxy == null) {
            proxy = thriftClientFactory.buildClient(clazz, serviceWrapper.getName(), serviceWrapper.getHost(), serviceWrapper.getPort());
            this.serviceProxy.set(proxy);
        }
        return proxy;
    }
}
