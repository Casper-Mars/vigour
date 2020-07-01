package org.r.framework.thrift.netty.wrapper;

import org.apache.thrift.TProcessor;

import java.util.concurrent.ExecutorService;

/**
 * 描述服务的信息
 * 1.端口
 * 2.服务名称
 * 3.thrift协议处理器
 * 4.thrift数据帧大小最大值
 * 5.服务最大连接数
 * 6.业务处理线程池配置
 * <p>
 * date 2020/6/30 16:01
 *
 * @author casper
 */
public class ServerDefinition {

    /**
     * 服务绑定的端口
     */
    private int port;

    /**
     * 服务名称
     */
    private String name;

    /**
     * thrift处理器
     */
    private TProcessor processor;

    /**
     * thrift协议数据帧大小最大值
     */
    private int maxFrameSize;

    /**
     * 最大连接数
     */
    private int maxConnections;

    /**
     * 业务处理线程池
     */
    private ExecutorService businessThreadPool;

    /**
     * netty的工作线程池大小
     */
    private int nettyWorkPoolSize;

    /**
     * netty的调度线程池大小
     */
    private int nettyBossPoolSize;

    private ServerDefinition() {

    }

    public static ServerDefinitionBuilder createBuilder() {
        return new ServerDefinitionBuilder();
    }

    public static class ServerDefinitionBuilder {

        ServerDefinition serverDefinition;

        public ServerDefinition build() {
            return serverDefinition;
        }

        public ServerDefinitionBuilder port(int port) {
            serverDefinition.port = port;
            return this;
        }

        public ServerDefinitionBuilder name(String name) {
            serverDefinition.name = name;
            return this;
        }

        public ServerDefinitionBuilder processor(TProcessor processor) {
            serverDefinition.processor = processor;
            return this;
        }

        public ServerDefinitionBuilder maxFrameSize(int maxFrameSize) {
            serverDefinition.maxFrameSize = maxFrameSize;
            return this;
        }

        public ServerDefinitionBuilder maxConnections(int maxConnections) {
            serverDefinition.maxConnections = maxConnections;
            return this;
        }

        public ServerDefinitionBuilder businessThreadPool(ExecutorService businessThreadPool) {
            serverDefinition.businessThreadPool = businessThreadPool;
            return this;
        }
        public ServerDefinitionBuilder nettyWorkPoolSize(int nettyWorkPoolSize){
            serverDefinition.nettyWorkPoolSize = nettyWorkPoolSize;
            return this;
        }
        public ServerDefinitionBuilder nettyBossPoolSize(int nettyBossPoolSize){
            serverDefinition.nettyBossPoolSize = nettyBossPoolSize;
            return this;
        }
    }

    public int getPort() {
        return port;
    }

    public String getName() {
        return name;
    }

    public TProcessor getProcessor() {
        return processor;
    }

    public int getMaxFrameSize() {
        return maxFrameSize;
    }

    public int getMaxConnections() {
        return maxConnections;
    }

    public ExecutorService getBusinessThreadPool() {
        return businessThreadPool;
    }

    public int getNettyWorkPoolSize() {
        return nettyWorkPoolSize;
    }

    public int getNettyBossPoolSize() {
        return nettyBossPoolSize;
    }
}
