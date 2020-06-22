package org.r.framework.thrift.client.core.manager;

import org.r.framework.thrift.client.core.factory.ProtocolFactory;
import org.r.framework.thrift.client.core.observer.ServiceObserver;
import org.r.framework.thrift.client.core.provider.ServiceInfoProvider;
import org.r.framework.thrift.client.core.thread.ServerExecutor;
import org.r.framework.thrift.client.core.wrapper.ServerWrapper;
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
public class ClientManager implements ServiceObserver {

    private final Logger log = LoggerFactory.getLogger(ClientManager.class);

    /**
     * 服务信息提供者
     */
    private final ServiceInfoProvider serviceInfoProvider;
    /**
     * 服务列表
     */
    private final Map<String, ServerManager> serverManagerMap;
    /**
     * 底层socket管理器，用于多个service复用同一个socket，降低系统的开销
     */
    private final TransportManager transportManager;
    /**
     * thrift 的协议工厂
     */
    private final ProtocolFactory protocolFactory;
    /**
     * 目标服务列表，会使用此列表进行过滤，列表上有的服务才会进行处理和维护
     */
    private final Set<String> targetServiceList;


    public ClientManager(ServiceInfoProvider serviceInfoProvider) {
        this.serviceInfoProvider = serviceInfoProvider;
        serviceInfoProvider.addObserver(this);
        this.serverManagerMap = new HashMap<>();
        this.transportManager = new TransportManager();
        this.protocolFactory = new ProtocolFactory();
        this.targetServiceList = new HashSet<>();
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
        List<ServerWrapper> allServer = serviceInfoProvider.getTargetServer(this.targetServiceList);
        /*提取最新列表的服务名称列表*/
        Set<String> serverName = allServer.stream().map(ServerWrapper::getName).collect(Collectors.toSet());
        /*更新transport*/
        transportManager.updateTransportList(allServer);
        /*移除不存在的服务的服务管理器*/
        Collection<String> deletes = new HashSet<>(serverManagerMap.keySet());
        deletes.removeAll(serverName);
        if (!CollectionUtils.isEmpty(deletes)) {
            for (String delete : deletes) {
                serverManagerMap.remove(delete);
            }
        }

        for (ServerWrapper serverWrapper : allServer) {
            /*如果服务实例不可用，则移除服务和transport*/
            if (!serverWrapper.isAvailable()) {
                log.info("server:{}[{}:{}] is unavailable", serverWrapper.getName(), serverWrapper.getHost(), serverWrapper.getPort());
                transportManager.deleteTransport(serverWrapper.getHost(), serverWrapper.getPort());
            } else {
                log.info("server:{}[{}:{}] is up", serverWrapper.getName(), serverWrapper.getHost(), serverWrapper.getPort());
                ServerManager serverManager = serverManagerMap.get(serverWrapper.getName());
                if (serverManager == null) {
                    serverManager = new ServerManager(serverWrapper.getName(), this.transportManager, this.protocolFactory);
                    serverManagerMap.put(serverWrapper.getName(), serverManager);
                }
                serverManager.registryClient(serverWrapper.getHost(), serverWrapper.getPort());
            }
        }
    }

    public ServerExecutor buildClient(String serverName, Class<?> serverClass) {
        ServerManager manager = serverManagerMap.get(serverName);
        ServerExecutor client = null;
        if (manager != null) {
            client = manager.getClient(serverClass);
        }
        return client;
    }

    /**
     * 添加目标服务
     *
     * @param serviceName 目标服务名称
     */
    public void addTargetService(String serviceName) {
        this.targetServiceList.add(serviceName);
    }


}
