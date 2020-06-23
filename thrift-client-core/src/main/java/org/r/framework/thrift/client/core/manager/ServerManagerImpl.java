package org.r.framework.thrift.client.core.manager;

import org.r.framework.thrift.client.core.factory.DefaultThriftClientFactory;
import org.r.framework.thrift.client.core.factory.ThriftClientFactory;
import org.r.framework.thrift.client.core.observer.ServiceObserver;
import org.r.framework.thrift.client.core.provider.ServiceInfoProvider;
import org.r.framework.thrift.client.core.wrapper.ServiceWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * date 2020/5/7 21:51
 *
 * @author casper
 */
public class ServerManagerImpl implements ServiceObserver, ServerManager {

    private final Logger log = LoggerFactory.getLogger(ServerManagerImpl.class);

    /**
     * 服务信息提供者
     */
    private final ServiceInfoProvider serviceInfoProvider;
    /**
     * 服务列表
     */
    private final Map<String, ServiceManager> services;
    /**
     * 底层socket管理器，用于多个service复用同一个socket，降低系统的开销
     */
    private final TransportManager transportManager;
    /**
     * 目标服务列表，会使用此列表进行过滤，列表上有的服务才会进行处理和维护
     */
    private final Set<String> targetServiceList;

    /**
     * thrift客户端工厂
     */
    private final ThriftClientFactory thriftClientFactory;

    public ServerManagerImpl(ServiceInfoProvider serviceInfoProvider, ChannelManager channelManager) {
        this.serviceInfoProvider = serviceInfoProvider;
        serviceInfoProvider.addObserver(this);
        this.services = new HashMap<>();
        this.transportManager = new TransportManager();
        this.targetServiceList = new HashSet<>();
        this.thriftClientFactory = new DefaultThriftClientFactory(channelManager);
        updateClientList();
    }

    /**
     * 1、先更新transport，移除已经不存在的transport，同时会通知对应的服务管理器移除对应的实例，添加新增的transport
     * 2、再更新服务管理器列表，移除不存在的服务
     * 3、根据新增的transport过滤出新增的服务实例，并添加到对应的服务管理器中
     */
    @Override
    public synchronized void updateClientList() {

        log.info("server list refresh");
        /*获取全部的服务实例*/
        List<ServiceWrapper> allServer = serviceInfoProvider.getTargetServer(this.targetServiceList);
        /*提取最新列表的服务名称列表*/
        Set<String> serverName = allServer.stream().map(ServiceWrapper::getName).collect(Collectors.toSet());
        /*更新transport*/
        transportManager.updateTransportList(allServer);
        /*移除不存在的服务的服务管理器*/
        Collection<String> deletes = new HashSet<>(services.keySet());
        deletes.removeAll(serverName);
        if (!CollectionUtils.isEmpty(deletes)) {
            for (String delete : deletes) {
                services.remove(delete);
            }
        }

        for (ServiceWrapper serviceWrapper : allServer) {
            /*如果服务实例不可用，则移除服务和transport*/
            if (!serviceWrapper.isAvailable()) {
                log.info("server:{}[{}:{}] is unavailable", serviceWrapper.getName(), serviceWrapper.getHost(), serviceWrapper.getPort());
                transportManager.deleteTransport(serviceWrapper.getHost(), serviceWrapper.getPort());
            } else {
                log.info("server:{}[{}:{}] is up", serviceWrapper.getName(), serviceWrapper.getHost(), serviceWrapper.getPort());
                ServiceManager ServiceManager = services.get(serviceWrapper.getName());
                if (ServiceManager == null) {
                    ServiceManager = new DefaultServiceManager(serviceWrapper.getName(), this.thriftClientFactory);
                    services.put(serviceWrapper.getName(), ServiceManager);
                }
                ServiceManager.registryService(serviceWrapper.getHost(), serviceWrapper.getPort());
            }
        }
    }

    /**
     * 获取服务代理对象
     *
     * @param serverName  服务名称
     * @param serverClass 服务的类
     * @return
     */
    @Override
    public Object getServer(String serverName, Class<?> serverClass) {
        ServiceManager serviceManager = services.get(serverName);
        if (serviceManager != null) {
            return serviceManager.getService(serverClass);
        }
        return null;
    }

    /**
     * 添加目标服务
     *
     * @param serverName 服务名称
     */
    @Override
    public void addTargetServer(String serverName) {
        this.targetServiceList.add(serverName);
    }
}
