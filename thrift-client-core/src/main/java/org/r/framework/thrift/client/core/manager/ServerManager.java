package org.r.framework.thrift.client.core.manager;

import org.r.framework.thrift.client.core.exception.TransportFailException;
import org.r.framework.thrift.client.core.factory.ServerFactory;
import org.r.framework.thrift.client.core.factory.ProtocolFactory;
import org.r.framework.thrift.client.core.thread.ServerExecutor;
import org.r.framework.thrift.client.core.wrapper.TransportWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

/**
 * date 2020/5/7 21:16
 *
 * @author casper
 */
public class ServerManager implements Function<TransportWrapper, Boolean> {

    private final Logger log = LoggerFactory.getLogger(ServerManager.class);


    /**
     * 服务名称
     */
    private final String name;

    /**
     * 底层socket管理器
     */
    private final TransportManager transportManager;

    /**
     * 协议工厂
     */
    private final ProtocolFactory protocolFactory;

    /**
     * 客户端列表
     */
    private final List<ServerFactory> clientList;

    /**
     * 用于指示服务使用的底层socket，避免重复添加
     */
    private final Set<TransportWrapper> transportsSet;


    /**
     * 计数器，用于负载均衡
     */
    private final AtomicInteger counter;

    public ServerManager(String name, TransportManager transportManager, ProtocolFactory protocolFactory) {
        this.name = name;
        this.transportsSet = new HashSet<>();
        this.transportManager = transportManager;
        this.protocolFactory = protocolFactory;
        this.transportManager.observedBy(this);
        this.clientList = new LinkedList<>();
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
        log.info("server[{}] instance[{}:{}] removed",this.name,transportWrapper.getHost(),transportWrapper.getPort());
        this.clientList.removeIf(i -> i.getTransportWrapper().equals(transportWrapper));
        transportsSet.remove(transportWrapper);
        return this.clientList.isEmpty();
    }

    /**
     * 注册客户端
     *
     * @param host 地址
     * @param port 端口
     */
    public void registryClient(String host, int port) {
        TransportWrapper transport = null;
        try {
            transport = transportManager.getTransportWrapper(host, port);
        } catch (TransportFailException e) {
            log.error("无法注册服务实例：{}:{}", host, port, e);
        }
        if (transportsSet.contains(transport)) {
            return;
        }
        transportsSet.add(transport);
        ServerFactory factory = new ServerFactory(this.name, transport, protocolFactory);
        clientList.add(factory);
    }


    /**
     * 获取客户端请求执行器
     *
     * @param thriftClientClass thrift原生实现的客户端类的class
     * @return
     */
    public ServerExecutor getClient(Class<?> thriftClientClass) {
        ServerFactory serverFactory = getClientFactory();
        if(serverFactory ==null){
            return null;
        }
        ServerExecutor target = null;
        try {
            target = serverFactory.getClient(thriftClientClass);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
            e.printStackTrace();
        }
        return target;
    }

    /**
     * 具体的负载均衡算法实现，要在集合中选取最佳的
     * 目前的算法只是按顺序分配任务。并且存在多线程的问题，获取到的factory未必是有效的服务实例，存在情况有可能在获取后该实例就down了
     *
     * @return
     */
    private ServerFactory getClientFactory() {
        if(clientList.isEmpty()){
            return null;
        }
        int i = counter.incrementAndGet();
        int index = i % clientList.size();
        ServerFactory target = clientList.get(index);
        if (i >= clientList.size()) {
            synchronized (this) {
                i = counter.get();
                if (i >= clientList.size()) {
                    counter.set(0);
                }
            }
        }
        return target;
    }

}
