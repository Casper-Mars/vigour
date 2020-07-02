package org.r.framework.thrift.springboot.starter;

import org.r.framework.thrift.netty.manager.ChannelManager;
import org.r.framework.thrift.netty.manager.DefaultChannelManager;
import org.r.framework.thrift.netty.manager.DefaultServerManager;
import org.r.framework.thrift.netty.manager.ServerManager;
import org.r.framework.thrift.netty.provider.DefaultServiceInfoProvider;
import org.r.framework.thrift.netty.provider.ServiceInfoProvider;
import org.r.framework.thrift.netty.wrapper.ServiceWrapper;
import org.r.framework.thrift.springboot.starter.annotation.EnableThriftClient;
import org.r.framework.thrift.springboot.starter.config.ClientConfig;
import org.r.framework.thrift.springboot.starter.config.ConfigProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.CollectionUtils;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * date 20-4-30 下午3:19
 *
 * @author casper
 **/
@EnableConfigurationProperties(ConfigProperties.class)
@Configuration
@EnableThriftClient()
@ConditionalOnProperty(name = "thrift.client.enable", havingValue = "true")
public class ThriftClientAutoConfig {

    private final Logger log = LoggerFactory.getLogger(ThriftClientAutoConfig.class);

    @Bean
    @ConditionalOnMissingBean(ChannelManager.class)
    public ChannelManager channelManager(ConfigProperties configProperties) {
        ClientConfig client = configProperties.getClient();
        return new DefaultChannelManager(client.getNetty().getWorkPoolSize(), client.getMaxFrameSize());
    }

    @Bean
    public ServiceInfoProvider serviceInfoProvider(ConfigProperties configProperties) {
        ClientConfig client = configProperties.getClient();
        Map<String, String> serverInfos = client.getServerInfos();
        List<ServiceWrapper> serviceList = new LinkedList<>();
        if (!CollectionUtils.isEmpty(serverInfos)) {
            Set<Map.Entry<String, String>> entries = serverInfos.entrySet();
            for (Map.Entry<String, String> entry : entries) {
                /*
                 * key的格式为：ip:port
                 * value的格式为：serverName,serverName,......
                 * */
                String[] ipAndPort = entry.getKey().split("-");
                if (ipAndPort.length != 2) {
                    log.error("{} server address format incorrect", entry.getKey());
                    continue;
                }
                String value = entry.getValue();
                if (value.isEmpty()) {
                    log.warn("{} has an empty server list", entry.getKey());
                    continue;
                }
                String[] serverNames = value.split(",");
                for (String serverName : serverNames) {
                    ServiceWrapper serviceWrapper = new ServiceWrapper(ipAndPort[0], Integer.parseInt(ipAndPort[1]), serverName.trim(), true);
                    serviceList.add(serviceWrapper);
                }
            }
        }
        return new DefaultServiceInfoProvider(serviceList);
    }


    @Bean
    @ConditionalOnBean(ServiceInfoProvider.class)
    public ServerManager serverManager(ServiceInfoProvider serviceInfoProvider, ChannelManager channelManager) {
        return new DefaultServerManager(serviceInfoProvider, channelManager);
    }


}
