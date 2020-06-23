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

    /**
     * eventLoop 的工作线程数
     */
    private int workThreads = 10;

    /**
     * thrift数据帧的最大帧长度
     */
    private int maxFrameSize = 64 * 1024 * 1024;

    public int getWorkThreads() {
        return workThreads;
    }

    public void setWorkThreads(int workThreads) {
        this.workThreads = workThreads;
    }

    public int getMaxFrameSize() {
        return maxFrameSize;
    }

    public void setMaxFrameSize(int maxFrameSize) {
        this.maxFrameSize = maxFrameSize;
    }
}
