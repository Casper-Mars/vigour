package org.r.framework.thrift.client.core.manager;

import org.r.framework.thrift.client.core.thread.ServerProxy;

/**
 * @author casper
 * @date 2020/6/23 下午12:57
 **/
public interface ServerManager {


    /**
     * 获取服务代理对象
     *
     * @param serverName   服务名称
     * @param serviceClass 服务的class
     * @return
     */
    Object getServer(String serverName, Class<?> serviceClass);


}
