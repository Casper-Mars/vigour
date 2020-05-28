package org.r.framework.thrift.client.core.manager;

import org.r.framework.thrift.client.core.factory.ProtocolFactory;
import org.r.framework.thrift.client.core.observer.ServiceObserver;
import org.r.framework.thrift.client.core.provider.ServiceInfoProvider;
import org.r.framework.thrift.client.core.thread.ClientExecutor;
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

    private final ServiceInfoProvider serviceInfoProvider;
    private final Map<String, ServerManager> managerMap;
    private final TransportManager transportManager;
    private final ProtocolFactory protocolFactory;


    public ClientManager(ServiceInfoProvider serviceInfoProvider) {
        this.serviceInfoProvider = serviceInfoProvider;
        serviceInfoProvider.addObserver(this);
        this.managerMap = new HashMap<>();
        this.transportManager = new TransportManager();
        this.protocolFactory = new ProtocolFactory();
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
        List<ServerWrapper> allServer = serviceInfoProvider.getAllServer();
        /*提取最新列表的服务名称列表*/
        Set<String> serverName = allServer.stream().map(ServerWrapper::getName).collect(Collectors.toSet());
        /*更新transport*/
        transportManager.updateTransportList(allServer);
        /*移除不存在的服务的服务管理器*/
        Collection<String> deletes = new HashSet<>(managerMap.keySet());
        deletes.removeAll(serverName);
        if (!CollectionUtils.isEmpty(deletes)) {
            for (String delete : deletes) {
                managerMap.remove(delete);
            }
        }

        for (ServerWrapper serverWrapper : allServer) {
            /*如果服务实例不可用，则移除服务和transport*/
            if (!serverWrapper.isAvailable()) {
                log.info("server:{}[{}:{}] is unavailable", serverWrapper.getName(), serverWrapper.getHost(), serverWrapper.getPort());
                transportManager.deleteTransport(serverWrapper.getHost(), serverWrapper.getPort());
            } else {
                log.info("server:{}[{}:{}] is up", serverWrapper.getName(), serverWrapper.getHost(), serverWrapper.getPort());
                ServerManager serverManager = managerMap.get(serverWrapper.getName());
                if (serverManager == null) {
                    serverManager = new ServerManager(serverWrapper.getName(), this.transportManager, this.protocolFactory);
                    managerMap.put(serverWrapper.getName(), serverManager);
                }
                serverManager.registryClient(serverWrapper.getHost(), serverWrapper.getPort());
            }
        }
    }

    public ClientExecutor buildClient(String serverName, Class<?> serverClass) {
        ServerManager manager = managerMap.get(serverName);
        ClientExecutor client = null;
        if (manager != null) {
            client = manager.getClient(serverClass);
        }
        return client;
    }

}
