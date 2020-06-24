package org.r.framework.thrift.client.core.manager;

import org.r.framework.thrift.client.core.event.ChannelCloseEvent;
import org.r.framework.thrift.client.core.factory.DefaultThriftClientFactory;
import org.r.framework.thrift.client.core.factory.ThriftClientFactory;
import org.r.framework.thrift.client.core.observer.Postman;
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
public class DefaultServerManager implements ServiceObserver, ServerManager {

    private final Logger log = LoggerFactory.getLogger(DefaultServerManager.class);

    /**
     * 服务信息提供者
     */
    private final ServiceInfoProvider serviceInfoProvider;
    /**
     * 服务列表
     */
    private final Map<String, ServiceManager> services;
    /**
     * 目标服务列表，会使用此列表进行过滤，列表上有的服务才会进行处理和维护
     */
    private final Set<String> targetServiceList;
    /**
     * thrift客户端工厂
     */
    private final ThriftClientFactory thriftClientFactory;

    private final Postman<ChannelCloseEvent> postman;

    public DefaultServerManager(ServiceInfoProvider serviceInfoProvider, ChannelManager channelManager, Postman<ChannelCloseEvent> postman) {
        this.serviceInfoProvider = serviceInfoProvider;
        serviceInfoProvider.addObserver(this);
        this.services = new HashMap<>();
        this.targetServiceList = new HashSet<>();
        this.thriftClientFactory = new DefaultThriftClientFactory(channelManager);
        this.postman = postman;
        updateClientList();
    }

    /**
     * 只对新增的服务做新增处理，已经存在的服务的可用性由netty维护
     */
    @Override
    public synchronized void updateClientList() {

        log.info("server list refresh");
        /*获取全部的服务实例*/
        List<ServiceWrapper> allServer = serviceInfoProvider.getTargetServer(this.targetServiceList);
        /*提取最新列表的服务名称列表*/
        Set<String> serverName = allServer.stream().map(ServiceWrapper::getName).collect(Collectors.toSet());
        /*移除不存在的服务的服务管理器*/
        Collection<String> deletes = new HashSet<>(services.keySet());
        deletes.removeAll(serverName);
        if (!CollectionUtils.isEmpty(deletes)) {
            for (String delete : deletes) {
                services.remove(delete);
            }
        }

        for (ServiceWrapper serviceWrapper : allServer) {
            log.info("server:{}[{}:{}] is up", serviceWrapper.getName(), serviceWrapper.getHost(), serviceWrapper.getPort());
            ServiceManager ServiceManager = services.get(serviceWrapper.getName());
            if (ServiceManager == null) {
                ServiceManager = new DefaultServiceManager(serviceWrapper.getName(), this.thriftClientFactory);
                postman.subscript((DefaultServiceManager)ServiceManager);
                services.put(serviceWrapper.getName(), ServiceManager);
            }
            ServiceManager.registryServiceIfAbsence(serviceWrapper.getHost(), serviceWrapper.getPort());
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
