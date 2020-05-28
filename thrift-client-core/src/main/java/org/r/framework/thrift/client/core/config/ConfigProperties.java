package org.r.framework.thrift.client.core.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * date 2020/4/30 22:09
 *
 * @author casper
 */
@Component
@ConfigurationProperties(prefix = "thrift.client")
public class ConfigProperties {


    private boolean enableEureka;

    private List<String> servers;

    public List<String> getServers() {
        return servers;
    }

    public void setServers(List<String> servers) {
        this.servers = servers;
    }

    public boolean isEnableEureka() {
        return enableEureka;
    }

    public void setEnableEureka(boolean enableEureka) {
        this.enableEureka = enableEureka;
    }
}
