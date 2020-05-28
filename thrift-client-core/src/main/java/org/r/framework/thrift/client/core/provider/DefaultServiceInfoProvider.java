package org.r.framework.thrift.client.core.provider;

import org.r.framework.thrift.client.core.observer.ServiceObserver;
import org.r.framework.thrift.client.core.wrapper.ServerWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * date 2020/5/7 21:18
 *
 * @author casper
 */
public class DefaultServiceInfoProvider implements ServiceInfoProvider {

    private final Logger log = LoggerFactory.getLogger(DefaultProtocolProvider.class);


    private final List<ServerWrapper> serverWrappers;
    /**
     * 用服务名称作为key的服务信息索引，提高单个服务信息的查询速度
     */
    private final Map<String, ServerWrapper> index;


    public DefaultServiceInfoProvider(List<String> serverInfos) {
        this.serverWrappers = new LinkedList<>();
        this.index = new HashMap<>();
        if (!CollectionUtils.isEmpty(serverInfos)) {
            for (String serverInfo : serverInfos) {
                ServerWrapper serverWrapper = buildWrapper(serverInfo);
                if (serverWrapper != null) {
                    serverWrappers.add(serverWrapper);
                    index.put(serverWrapper.getName(), serverWrapper);
                }
            }
        }
    }

    @Override
    public List<ServerWrapper> getAllServer() {
        return serverWrappers;
    }

    @Override
    public ServerWrapper getServer(String serverName) {
        return index.get(serverName);
    }

    @Override
    public void addObserver(ServiceObserver observer) {

    }

    private ServerWrapper buildWrapper(String ipAndPort) {
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
        return new ServerWrapper(ip, port, strings[1],true);
    }


}
