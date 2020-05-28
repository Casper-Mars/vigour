package org.r.framework.thrift.server.core.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.stereotype.Component;

/**
 * date 2020/4/30 22:09
 *
 * @author casper
 */
@Component
@ConfigurationProperties(prefix = "thrift.server")
public class ConfigProperties {

    private boolean enable;
    @NestedConfigurationProperty
    private NettyConfig netty;

    private String name;

    private int port;


    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public NettyConfig getNetty() {
        return netty;
    }

    public void setNetty(NettyConfig netty) {
        this.netty = netty;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
