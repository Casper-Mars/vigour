package org.r.framework.thrift.springboot.starter.config;

/**
 * date 2020/4/30 22:14
 *
 * @author casper
 */
public class NettyConfig {


    /**
     * 是否可用
     */
    private boolean enable;

    /**
     * 工作线程池大小
     */
    private int workPoolSize;

    /**
     * 调度线程池大小
     */
    private int bossPoolSize;

    public int getWorkPoolSize() {
        return workPoolSize;
    }

    public void setWorkPoolSize(int workPoolSize) {
        this.workPoolSize = workPoolSize;
    }

    public int getBossPoolSize() {
        return bossPoolSize;
    }

    public void setBossPoolSize(int bossPoolSize) {
        this.bossPoolSize = bossPoolSize;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }
}
