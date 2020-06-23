package org.r.framework.thrift.client.core.manager;

import org.r.framework.thrift.client.core.factory.DefaultServiceFactory;
import org.r.framework.thrift.client.core.factory.ServiceFactory;
import org.r.framework.thrift.client.core.factory.ThriftClientFactory;
import org.r.framework.thrift.client.core.wrapper.ServiceWrapper;
import org.r.framework.thrift.client.core.wrapper.TransportWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

/**
 * date 2020/5/7 21:16
 *
 * @author casper
 */
public class DefaultServiceManager implements ServiceManager, Function<TransportWrapper, Boolean> {

    private final Logger log = LoggerFactory.getLogger(DefaultServiceManager.class);


    /**
     * 服务名称
     */
    private final String name;

    /**
     * 服务实例列表
     */
    private final List<ServiceFactory> serviceList;

    /**
     * thrift客户端工厂
     */
    private final ThriftClientFactory thriftClientFactory;

    /**
     * 计数器，用于负载均衡
     */
    private final AtomicInteger counter;

    public DefaultServiceManager(String name, ThriftClientFactory thriftClientFactory) {
        this.name = name;
        this.thriftClientFactory = thriftClientFactory;
        this.serviceList = new LinkedList<>();
        this.counter = new AtomicInteger(0);
    }

    public String getName() {
        return name;
    }

    /**
     * transport被删除的时候，会回调此方法
     * 当此服务管理器的实例列表为空时，返回true，表示transport管理器要移除本服务管理器的监听
     *
     * @param transportWrapper transport的信息
     * @return true, 表示transport管理器要移除本服务管理器的监听
     */
    @Override
    public Boolean apply(TransportWrapper transportWrapper) {
        log.info("server[{}] instance[{}:{}] removed", this.name, transportWrapper.getHost(), transportWrapper.getPort());
//        this.serviceList.removeIf(i -> i.getTransportWrapper().equals(transportWrapper));
        return this.serviceList.isEmpty();
    }

    /**
     * 注册服务
     *
     * @param host 远程主机地址
     * @param port 远程服务进程端口
     */
    public void registryService(String host, int port) {
        ServiceWrapper serviceWrapper = new ServiceWrapper(host, port, this.name, true);
        ServiceFactory factory = new DefaultServiceFactory(serviceWrapper, this.thriftClientFactory);
        serviceList.add(factory);
    }

    /**
     * 具体的负载均衡算法实现，要在集合中选取最佳的
     * 目前的算法只是按顺序分配任务。并且存在多线程的问题，获取到的factory未必是有效的服务实例，存在情况有可能在获取后该实例就down了
     *
     * @return
     */
    private ServiceFactory getServiceFactory() {
        if (serviceList.isEmpty()) {
            return null;
        }
        int i = counter.incrementAndGet();
        int index = i % serviceList.size();
        ServiceFactory target = serviceList.get(index);
        if (i >= serviceList.size()) {
            synchronized (this) {
                i = counter.get();
                if (i >= serviceList.size()) {
                    counter.set(0);
                }
            }
        }
        return target;
    }

    /**
     * 获取服务代理对象
     *
     * @param serviceClass 服务的class
     * @return
     */
    @Override
    public Object getService(Class<?> serviceClass) {
        ServiceFactory serviceFactory = getServiceFactory();
        if (serviceFactory == null) {
            return null;
        }
        try {
            return serviceFactory.buildServerProxy(serviceClass);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
            e.printStackTrace();
        }
        return null;
    }
}
