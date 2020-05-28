package org.r.framework.thrift.client.core;

import org.r.framework.thrift.client.core.config.ConfigProperties;
import org.r.framework.thrift.client.core.manager.ClientManager;
import org.r.framework.thrift.client.core.provider.DefaultServiceInfoProvider;
import org.r.framework.thrift.client.core.provider.ServiceInfoProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * date 20-4-30 下午3:19
 *
 * @author casper
 **/
@Configuration
@EnableConfigurationProperties(ConfigProperties.class)
public class ThriftClientAutoConfig {

    @Autowired
    private ConfigProperties configProperties;

    @Bean
    @ConditionalOnExpression("#{'' != ${thrift.client.servers}}")
    @ConditionalOnMissingBean
    public ServiceInfoProvider serviceInfoProvider() {
        return new DefaultServiceInfoProvider(configProperties.getServers());
    }

    @Bean
    @ConditionalOnMissingBean(ClientManager.class)
    @ConditionalOnBean
    public ClientManager transportProvider(ServiceInfoProvider serviceInfoProvider) {
        return new ClientManager(serviceInfoProvider);
    }


}
