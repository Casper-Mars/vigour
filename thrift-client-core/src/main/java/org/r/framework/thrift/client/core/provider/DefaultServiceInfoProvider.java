package org.r.framework.thrift.client.core.provider;

import org.r.framework.thrift.client.core.observer.ServiceObserver;
import org.r.framework.thrift.client.core.wrapper.ServiceWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * date 2020/5/7 21:18
 *
 * @author casper
 */
public class DefaultServiceInfoProvider implements ServiceInfoProvider {

    private final Logger log = LoggerFactory.getLogger(DefaultProtocolProvider.class);


    private final List<ServiceWrapper> serviceWrappers;
    /**
     * 用服务名称作为key的服务信息索引，提高单个服务信息的查询速度
     */
    private final Map<String, ServiceWrapper> index;


    public DefaultServiceInfoProvider(List<String> serverInfos) {
        this.serviceWrappers = new LinkedList<>();
        this.index = new HashMap<>();
        if (!CollectionUtils.isEmpty(serverInfos)) {
            for (String serverInfo : serverInfos) {
                ServiceWrapper serviceWrapper = buildWrapper(serverInfo);
                if (serviceWrapper != null) {
                    serviceWrappers.add(serviceWrapper);
                    index.put(serviceWrapper.getName(), serviceWrapper);
                }
            }
        }
    }

    @Override
    public List<ServiceWrapper> getAllServer() {
        return serviceWrappers;
    }

    /**
     * 获取列表指定的服务的信息
     *
     * @param targetServerList 指定的服务列表
     * @return
     */
    @Override
    public List<ServiceWrapper> getTargetServer(Set<String> targetServerList) {
        return serviceWrappers;
    }

    @Override
    public ServiceWrapper getServer(String serverName) {
        return index.get(serverName);
    }

    @Override
    public void addObserver(ServiceObserver observer) {

    }

    private ServiceWrapper buildWrapper(String ipAndPort) {
        if (ipAndPort.indexOf(':') == -1 || ipAndPort.indexOf('/') == -1) {
            log.warn("unrecognized server info " + ipAndPort);
            return null;
        }
        String[] split = ipAndPort.split(":");
        String ip = split[0];
        String[] strings = split[1].split("/");
        int port;
        try {
            port = Integer.parseInt(strings[0]);
        } catch (NumberFormatException e) {
            log.warn("incorrect port for server info " + ipAndPort);
            return null;
        }
        return new ServiceWrapper(ip, port, strings[1],true);
    }


}
