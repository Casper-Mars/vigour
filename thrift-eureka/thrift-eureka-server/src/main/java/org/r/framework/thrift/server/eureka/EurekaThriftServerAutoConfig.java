package org.r.framework.thrift.server.eureka;

import org.r.framework.thrift.server.core.provider.ServerInfoProvider;
import org.r.framework.thrift.server.eureka.provider.ServerMetaDataProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * date 20-4-30 下午3:19
 *
 * @author casper
 **/
@Configuration
public class EurekaThriftServerAutoConfig {

    @Bean
    @ConditionalOnBean
    public ServerMetaDataProvider serverMetaDataProvider(ServerInfoProvider serverInfoProvider) {
        return new ServerMetaDataProvider(serverInfoProvider);
    }


}
