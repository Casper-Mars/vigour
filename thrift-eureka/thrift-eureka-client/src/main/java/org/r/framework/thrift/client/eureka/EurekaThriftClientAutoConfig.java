package org.r.framework.thrift.client.eureka;

import com.netflix.discovery.EurekaClient;
import org.r.framework.thrift.client.eureka.provider.EurekaServiceInfoProvider;
import org.r.framework.thrift.netty.provider.ServiceInfoProvider;
import org.r.framework.thrift.springboot.starter.ThriftClientAutoConfig;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * date 20-4-30 下午3:19
 *
 * @author casper
 **/
@Configuration
public class EurekaThriftClientAutoConfig extends ThriftClientAutoConfig {


    @Bean
    @Primary
    public ServiceInfoProvider serviceInfoProvider(ConfigurableApplicationContext applicationContext) {
        EurekaClient eurekaClient = applicationContext.getBean(EurekaClient.class);
        return new EurekaServiceInfoProvider(eurekaClient);
    }

    @Bean
    public ServerRefreshListener serverRefreshListener(ServiceInfoProvider provider) {

        return new ServerRefreshListener(provider);
    }

}
