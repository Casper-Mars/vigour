package org.r.framework.thrift.client.core;

import org.r.framework.thrift.client.core.manager.ClientManager;
import org.r.framework.thrift.client.core.provider.ServiceInfoProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * date 20-4-30 下午3:19
 *
 * @author casper
 **/
@Configuration
public class ThriftClientAutoConfig {

    @Bean
    @ConditionalOnBean(ServiceInfoProvider.class)
    public ClientManager clientManager(ServiceInfoProvider serviceInfoProvider) {
        return new ClientManager(serviceInfoProvider);
    }


}
