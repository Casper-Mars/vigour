package org.r.framework.thrift.netty.manager;

import org.r.framework.thrift.netty.core.events.ChannelConnectEvent;
import org.r.framework.thrift.netty.core.events.Subscriber;

/**
 * @author casper
 * @date 2020/6/23 下午12:57
 **/
public interface ServiceManager extends Subscriber<ChannelConnectEvent> {


    /**
     * 获取服务具体的实例对象
     *
     * @param serviceClass 服务的class
     * @return
     */
    Object getService(Class<?> serviceClass);

    /**
     * 注册服务
     *
     * @param host 远程主机地址
     * @param port 远程服务进程端口
     */
    void registryServiceIfAbsence(String host, int port);


}
