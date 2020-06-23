package org.r.framework.thrift.client.core.provider;

import org.r.framework.thrift.client.core.observer.ServiceObserver;
import org.r.framework.thrift.client.core.wrapper.ServiceWrapper;

import java.util.List;
import java.util.Set;

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
    List<ServiceWrapper> getAllServer();

    /**
     * 获取列表指定的服务的信息
     *
     * @param targetServerList 指定的服务列表
     * @return
     */
    List<ServiceWrapper> getTargetServer(Set<String> targetServerList);

    /**
     * 通过服务名称获得服务信息
     *
     * @param serverName 服务名称
     * @return
     */
    ServiceWrapper getServer(String serverName);

    /**
     * 添加观察者
     *
     * @param observer 观察者
     */
    void addObserver(ServiceObserver observer);


}
