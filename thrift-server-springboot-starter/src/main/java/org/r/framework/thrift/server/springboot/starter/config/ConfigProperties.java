package org.r.framework.thrift.server.springboot.starter.config;

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
     * netty配置
     */
    @NestedConfigurationProperty
    private NettyConfig netty;

    /**
     * 服务端的配置
     */
    private ServerConfig server;

    public ServerConfig getServer() {
        return server;
    }

    public void setServer(ServerConfig server) {
        this.server = server;
    }


    public NettyConfig getNetty() {
        return netty;
    }

    public void setNetty(NettyConfig netty) {
        this.netty = netty;
    }

}
