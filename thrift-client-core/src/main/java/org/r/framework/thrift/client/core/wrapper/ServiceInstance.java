package org.r.framework.thrift.client.core.wrapper;

import org.r.framework.thrift.client.core.exception.ChannelOpenFailException;
import org.r.framework.thrift.client.core.factory.ThriftClientFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * date 2020/5/24 23:41
 *
 * @author casper
 */
public class ServiceInstance {

    private final Logger log = LoggerFactory.getLogger(ServiceInstance.class);

    private final ServiceWrapper serviceWrapper;
    private final ThriftClientFactory thriftClientFactory;

    public ServiceInstance(ServiceWrapper serviceWrapper, ThriftClientFactory thriftClientFactory) {
        this.serviceWrapper = serviceWrapper;
        this.thriftClientFactory = thriftClientFactory;
    }

    /**
     * 构建服务的代理类
     *
     * @param clazz 服务实现类
     * @return
     */
    public Object build(Class<?> clazz) throws ChannelOpenFailException {
        return thriftClientFactory.buildClient(clazz, serviceWrapper.getName(), serviceWrapper.getHost(), serviceWrapper.getPort());
    }

    public String getIp() {
        return serviceWrapper.getHost();
    }

    public int getPort() {
        return serviceWrapper.getPort();
    }


}
