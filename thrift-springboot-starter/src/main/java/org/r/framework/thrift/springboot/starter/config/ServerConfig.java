package org.r.framework.thrift.springboot.starter.config;

import org.springframework.boot.context.properties.NestedConfigurationProperty;

/**
 * date 2020/4/30 22:24
 *
 * @author casper
 */
public class ServerConfig {

    /**
     * 是否启用服务端
     */
    private boolean enable = false;
    /**
     * 服务端监听的端口
     */
    private int port;
    /**
     * 服务端名称
     */
    private String name;
    /**
     * thrift版本号
     */
    private String thriftVersion;
    /**
     * thrift协议帧大小的最大值
     */
    private int maxFrameSize = 67108864;
    /**
     * 服务端的最大连接数
     */
    private int maxConnections = 10;
    /**
     * 服务端的netty配置
     */
    @NestedConfigurationProperty
    private NettyConfig netty;

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public NettyConfig getNetty() {
        return netty;
    }

    public void setNetty(NettyConfig netty) {
        this.netty = netty;
    }

    public String getThriftVersion() {
        return thriftVersion;
    }

    public void setThriftVersion(String thriftVersion) {
        this.thriftVersion = thriftVersion;
    }

    public int getMaxFrameSize() {
        return maxFrameSize;
    }

    public void setMaxFrameSize(int maxFrameSize) {
        this.maxFrameSize = maxFrameSize;
    }

    public int getMaxConnections() {
        return maxConnections;
    }

    public void setMaxConnections(int maxConnections) {
        this.maxConnections = maxConnections;
    }
}
