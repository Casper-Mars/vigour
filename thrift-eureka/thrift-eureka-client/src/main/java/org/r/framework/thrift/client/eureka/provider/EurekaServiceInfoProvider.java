package org.r.framework.thrift.client.eureka.provider;

import com.alibaba.fastjson.JSONObject;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import com.netflix.discovery.shared.Application;
import org.r.framework.thrift.client.core.observer.ServiceObserver;
import org.r.framework.thrift.client.core.provider.ServiceInfoProvider;
import org.r.framework.thrift.client.core.wrapper.ServerWrapper;
import org.r.framework.thrift.common.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * date 20-5-9 下午4:23
 *
 * @author casper
 **/
public class EurekaServiceInfoProvider implements ServiceInfoProvider {

    private final Logger log = LoggerFactory.getLogger(EurekaServiceInfoProvider.class);

    /**
     * 观察者
     */
    private final List<ServiceObserver> observers;

    /**
     * 服务信息
     */
    private final List<ServerWrapper> serverWrappers;

    /**
     * 用服务名称作为key的服务信息索引，提高单个服务信息的查询速度
     */
    private final Map<String, ServerWrapper> index;
    /**
     * eureka的客户端，用来获取注册的服务
     */
    private final EurekaClient eurekaClient;


    public EurekaServiceInfoProvider(EurekaClient eurekaClient) {
        this.eurekaClient = eurekaClient;
        observers = new LinkedList<>();
        serverWrappers = new LinkedList<>();
        index = new HashMap<>();
        rebuild();
    }

    /**
     * 获取全部的服务
     *
     * @return
     */
    @Override
    public List<ServerWrapper> getAllServer() {
        if (serverWrappers == null) {
            return new ArrayList<>();
        }
        return serverWrappers;
    }

    @Override
    public ServerWrapper getServer(String serverName) {
        return index.get(serverName);
    }


    /**
     * 添加观察者
     *
     * @param observer 观察者
     */
    @Override
    public void addObserver(ServiceObserver observer) {
        this.observers.add(observer);
    }

    public void refresh() {
        rebuild();
        for (ServiceObserver observer : observers) {
            observer.updateClientList();
        }
    }


    /**
     * 此方法用于启动时初始化和启动后心跳处理
     * <p>
     * 每次心跳事件需要检查刷新的服务列表中的服务是否存在，不存在则新建服务。
     * 一个服务对应到eureka的一个application，因此一个服务有多个提供者（instance）
     */
    private void rebuild() {
        serverWrappers.clear();
        index.clear();
        List<Application> registeredApplications = eurekaClient.getApplications().getRegisteredApplications();
        for (Application application : registeredApplications) {
            List<InstanceInfo> instances = application.getInstances();
            for (InstanceInfo instance : instances) {
                String ipAddr = instance.getIPAddr();
                InstanceInfo.InstanceStatus status = instance.getStatus();
                boolean isAvailable = status.equals(InstanceInfo.InstanceStatus.UP);
                String serverInfoJson = instance.getMetadata().get(Constants.SERVERINFO);
                if (serverInfoJson == null) {
                    continue;
                }
                try {
                    List<String> serverNames = JSONObject.parseArray(serverInfoJson, String.class);

                    for (String serverName : serverNames) {
                        String[] split = serverName.split(":");
                        ServerWrapper serverWrapper = new ServerWrapper(ipAddr, Integer.parseInt(split[1]), split[0], isAvailable);
                        serverWrappers.add(serverWrapper);
                        index.put(serverWrapper.getName(), serverWrapper);
                    }
                } catch (Exception e) {
                    log.error("can not init server:" + serverInfoJson);
                }
            }
        }
    }

}
