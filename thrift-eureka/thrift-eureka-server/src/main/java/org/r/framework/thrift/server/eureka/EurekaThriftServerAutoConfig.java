package org.r.framework.thrift.server.eureka;

import org.r.framework.thrift.server.eureka.provider.ServerMetaDataProvider;
import org.r.framework.thrift.springboot.starter.provider.ServerInfoProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * date 20-4-30 下午3:19
 *
 * @author casper
 **/
@Configuration
@ConditionalOnProperty(name = "thrift.server.enable", havingValue = "true")
public class EurekaThriftServerAutoConfig {

    @Bean
//    @ConditionalOnBean(ServerInfoProvider.class)
    public ServerMetaDataProvider serverMetaDataProvider(ServerInfoProvider serverInfoProvider) {
        return new ServerMetaDataProvider(serverInfoProvider);
    }


}
