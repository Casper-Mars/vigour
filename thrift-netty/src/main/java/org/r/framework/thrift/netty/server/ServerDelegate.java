package org.r.framework.thrift.netty.server;


import org.r.framework.thrift.netty.wrapper.ServerDefinition;

/**
 * date 2020/4/30 22:29
 *
 * @author casper
 */
public interface ServerDelegate {


    /**
     * 启动服务
     * @param serverDefinition 服务信息
     */
    void start(ServerDefinition serverDefinition);

    /**
     * 停止服务
     */
    void stop();


}
