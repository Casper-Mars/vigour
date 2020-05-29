package org.r.framework.thrift.client.eureka;

import com.netflix.discovery.EurekaClient;
import org.r.framework.thrift.client.core.ThriftClientAutoConfig;
import org.r.framework.thrift.client.core.provider.ServiceInfoProvider;
import org.r.framework.thrift.client.eureka.provider.EurekaServiceInfoProvider;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * date 20-4-30 下午3:19
 *
 * @author casper
 **/
@Configuration
public class EurekaThriftClientAutoConfig extends ThriftClientAutoConfig {


    @Bean
    public ServiceInfoProvider serviceInfoProvider(ConfigurableApplicationContext applicationContext) {
        EurekaClient eurekaClient = applicationContext.getBean(EurekaClient.class);
        return new EurekaServiceInfoProvider(eurekaClient);
    }

    @Bean
    public ServerRefreshListener serverRefreshListener(ServiceInfoProvider provider){

        return new ServerRefreshListener(provider);
    }

}
