package org.r.framework.thrift.client.core;

import org.r.framework.thrift.client.core.config.ConfigProperties;
import org.r.framework.thrift.client.core.factory.ChannelFactory;
import org.r.framework.thrift.client.core.factory.DefaultChannelFactory;
import org.r.framework.thrift.client.core.manager.ChannelManager;
import org.r.framework.thrift.client.core.manager.DefaultChannelManager;
import org.r.framework.thrift.client.core.manager.ServerManagerImpl;
import org.r.framework.thrift.client.core.provider.ServiceInfoProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * date 20-4-30 下午3:19
 *
 * @author casper
 **/
@EnableConfigurationProperties(ConfigProperties.class)
@Configuration
public class ThriftClientAutoConfig {


    @Bean
    @ConditionalOnMissingBean(ChannelFactory.class)
    public ChannelFactory channelFactory(ConfigProperties configProperties) {
        return new DefaultChannelFactory(configProperties);
    }

    @Bean
    @ConditionalOnMissingBean(ChannelManager.class)
    public ChannelManager channelManager(ChannelFactory channelFactory) {
        return new DefaultChannelManager(channelFactory);
    }


    @Bean
    @ConditionalOnBean(ServiceInfoProvider.class)
    public ServerManagerImpl clientManager(ServiceInfoProvider serviceInfoProvider, ChannelManager channelManager) {
        return new ServerManagerImpl(serviceInfoProvider, channelManager);
    }


}
