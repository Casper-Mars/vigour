package org.r.framework.thrift.springboot.starter.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.stereotype.Component;

/**
 * date 2020/4/30 22:09
 *
 * @author casper
 */
@Component
@ConfigurationProperties(prefix = "thrift")
public class ConfigProperties {


    /**
     * 服务端的配置
     */
    @NestedConfigurationProperty
    private ServerConfig server;

    /**
     * 客户端配置
     */
    @NestedConfigurationProperty
    private ClientConfig client;

    public ClientConfig getClient() {
        return client;
    }

    public void setClient(ClientConfig client) {
        this.client = client;
    }

    public ServerConfig getServer() {
        return server;
    }

    public void setServer(ServerConfig server) {
        this.server = server;
    }

}
