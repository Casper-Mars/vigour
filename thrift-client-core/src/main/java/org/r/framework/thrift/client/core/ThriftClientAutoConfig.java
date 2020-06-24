package org.r.framework.thrift.client.core;

import org.r.framework.thrift.client.core.config.ConfigProperties;
import org.r.framework.thrift.client.core.event.ChannelCloseEvent;
import org.r.framework.thrift.client.core.factory.ChannelFactory;
import org.r.framework.thrift.client.core.factory.DefaultChannelFactory;
import org.r.framework.thrift.client.core.manager.ChannelManager;
import org.r.framework.thrift.client.core.manager.DefaultChannelManager;
import org.r.framework.thrift.client.core.manager.DefaultServerManager;
import org.r.framework.thrift.client.core.manager.ServerManager;
import org.r.framework.thrift.client.core.observer.Postman;
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
    public Postman<ChannelCloseEvent> postman() {
        Postman<ChannelCloseEvent> postman = new Postman<>();
        postman.start();
        return postman;
    }


    @Bean
    @ConditionalOnMissingBean(ChannelFactory.class)
    public ChannelFactory channelFactory(ConfigProperties configProperties, Postman<ChannelCloseEvent> postman) {
        return new DefaultChannelFactory(configProperties, postman);
    }

    @Bean
    @ConditionalOnMissingBean(ChannelManager.class)
    public ChannelManager channelManager(ChannelFactory channelFactory, Postman<ChannelCloseEvent> postman) {
        DefaultChannelManager channelManager = new DefaultChannelManager(channelFactory);
        postman.subscript(channelManager);
        return channelManager;
    }


    @Bean
    @ConditionalOnBean(ServiceInfoProvider.class)
    public ServerManager clientManager(ServiceInfoProvider serviceInfoProvider, ChannelManager channelManager,Postman<ChannelCloseEvent> postman) {
        return new DefaultServerManager(serviceInfoProvider, channelManager,postman);
    }


}
