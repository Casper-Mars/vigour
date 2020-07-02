package org.r.framework.thrift.springboot.starter.config;

import org.springframework.boot.context.properties.NestedConfigurationProperty;

import java.util.Map;

/**
 * date 2020/7/1 11:20
 *
 * @author casper
 */
public class ClientConfig {

    /**
     * 是否启用
     */
    private boolean enable;

    /**
     * netty配置
     */
    @NestedConfigurationProperty
    private NettyConfig netty;

    /**
     * thrift数据帧大小最大值
     */
    private int maxFrameSize = 67108864;

    /**
     * 扫描的基本包路径
     */
    private String basePackage;

    /**
     * 服务信息
     */
    private Map<String, String> serverInfos;

    public String getBasePackage() {
        return basePackage;
    }

    public void setBasePackage(String basePackage) {
        this.basePackage = basePackage;
    }

    public Map<String, String> getServerInfos() {

        return serverInfos;
    }

    public void setServerInfos(Map<String, String> serverInfos) {
        this.serverInfos = serverInfos;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public int getMaxFrameSize() {
        return maxFrameSize;
    }

    public void setMaxFrameSize(int maxFrameSize) {
        this.maxFrameSize = maxFrameSize;
    }

    public NettyConfig getNetty() {
        return netty;
    }

    public void setNetty(NettyConfig netty) {
        this.netty = netty;
    }
}
