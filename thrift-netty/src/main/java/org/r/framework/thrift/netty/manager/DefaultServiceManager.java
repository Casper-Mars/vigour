package org.r.framework.thrift.netty.manager;

import org.r.framework.thrift.netty.core.events.ChannelConnectEvent;
import org.r.framework.thrift.netty.core.events.ChannelConnectionCloseEvent;
import org.r.framework.thrift.netty.core.events.Subscriber;
import org.r.framework.thrift.netty.exception.ChannelOpenFailException;
import org.r.framework.thrift.netty.factory.ThriftClientFactory;
import org.r.framework.thrift.netty.wrapper.ServiceInstance;
import org.r.framework.thrift.netty.wrapper.ServiceWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * date 2020/5/7 21:16
 *
 * @author casper
 */
public class DefaultServiceManager implements ServiceManager, Subscriber<ChannelConnectEvent> {

    private final Logger log = LoggerFactory.getLogger(DefaultServiceManager.class);


    /**
     * 服务名称
     */
    private final String name;

    /**
     * 服务实例列表
     */
    private final List<ServiceInstance> serviceList;

    /**
     * thrift客户端工厂
     */
    private final ThriftClientFactory thriftClientFactory;

    /**
     * 记录实例的地址hash值，为了判断实例是否存在，避免重复构建
     */
    private final Set<Integer> instanceHashValue;

    /**
     * 计数器，用于负载均衡
     */
    private final AtomicInteger counter;

    public DefaultServiceManager(String name, ThriftClientFactory thriftClientFactory) {
        this.name = name;
        this.thriftClientFactory = thriftClientFactory;
        this.serviceList = new LinkedList<>();
        this.counter = new AtomicInteger(0);
        this.instanceHashValue = new HashSet<>();
    }

    public String getName() {
        return name;
    }

    /**
     * 注册服务
     *
     * @param host 远程主机地址
     * @param port 远程服务进程端口
     */
    public void registryServiceIfAbsence(String host, int port) {
        int hash = getInstanceHashValue(host, port);
        if (instanceHashValue.contains(hash)) {
            return;
        }
        instanceHashValue.add(hash);
        ServiceWrapper serviceWrapper = new ServiceWrapper(host, port, this.name, true);
        ServiceInstance factory = new ServiceInstance(serviceWrapper, this.thriftClientFactory);
        serviceList.add(factory);

    }

    /**
     * 具体的负载均衡算法实现，要在集合中选取最佳的
     * 目前的算法只是按顺序分配任务。并且存在多线程的问题，获取到的factory未必是有效的服务实例，存在情况有可能在获取后该实例就down了
     *
     * @return
     */
    private ServiceInstance getServiceFactory() {
        if (serviceList.isEmpty()) {
            return null;
        }
        int i = counter.incrementAndGet();
        int index = i % serviceList.size();
        ServiceInstance target = serviceList.get(index);
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
        ServiceInstance serviceInstance = getServiceFactory();
        if (serviceInstance == null) {
            return null;
        }
        try {
            return serviceInstance.build(serviceClass);
        } catch (ChannelOpenFailException e) {
            log.error(e.getMessage(), e);
            this.serviceList.remove(serviceInstance);
        }
        return null;
    }

    /**
     * 获取实例的hash值
     *
     * @param ip   ip地址
     * @param port 端口
     * @return
     */
    private int getInstanceHashValue(String ip, int port) {
        return Objects.hash(ip, port);
    }


    /**
     * 读邮件
     *
     * @param mail 邮件
     */
    @Override
    public void readMail(ChannelConnectEvent mail) {
        if(mail instanceof ChannelConnectionCloseEvent){
            ChannelConnectionCloseEvent tmp = (ChannelConnectionCloseEvent)mail;
            this.serviceList.removeIf(t -> t.getPort() == tmp.getPort() && t.getIp().equals(tmp.getIp()));
            this.instanceHashValue.remove(getInstanceHashValue(tmp.getIp(), tmp.getPort()));

        }
    }
}
