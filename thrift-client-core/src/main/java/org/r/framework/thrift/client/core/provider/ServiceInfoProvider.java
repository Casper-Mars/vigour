package org.r.framework.thrift.client.core.provider;

import org.r.framework.thrift.client.core.observer.ServiceObserver;
import org.r.framework.thrift.client.core.wrapper.ServerWrapper;

import java.util.List;

/**
 * date 20-5-7 下午5:50
 *
 * @author casper
 **/
public interface ServiceInfoProvider {


    /**
     * 获取全部的服务
     *
     * @return
     */
    List<ServerWrapper> getAllServer();

    /**
     * 通过服务名称获得服务信息
     *
     * @param serverName 服务名称
     * @return
     */
    ServerWrapper getServer(String serverName);

    /**
     * 添加观察者
     *
     * @param observer 观察者
     */
    void addObserver(ServiceObserver observer);


}
