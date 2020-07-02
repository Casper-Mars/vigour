package org.r.framework.thrift.eureka.client;

import com.netflix.discovery.EurekaClient;
import org.r.framework.thrift.eureka.client.provider.EurekaServiceInfoProvider;
import org.r.framework.thrift.netty.provider.ServiceInfoProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
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
@ConditionalOnProperty(name = "thrift.client.enable", havingValue = "true")
public class EurekaThriftClientAutoConfig {

    private final Logger log = LoggerFactory.getLogger(EurekaThriftClientAutoConfig.class);

    @Bean
    @Primary
    public ServiceInfoProvider serviceInfoProvider(ConfigurableApplicationContext applicationContext) {
        log.info("Create eureka service info provider");
        EurekaClient eurekaClient = applicationContext.getBean(EurekaClient.class);
        return new EurekaServiceInfoProvider(eurekaClient);
    }

    @Bean
    public ServerRefreshListener serverRefreshListener(ServiceInfoProvider provider) {

        return new ServerRefreshListener(provider);
    }

}
