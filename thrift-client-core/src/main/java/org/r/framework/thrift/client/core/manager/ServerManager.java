package org.r.framework.thrift.client.core.manager;

/**
 * @author casper
 * @date 2020/6/23 下午1:25
 **/
public interface ServerManager {


    /**
     * 获取服务代理对象
     *
     * @param serverName  服务名称
     * @param serverClass 服务的类
     * @return
     */
    Object getServer(String serverName, Class<?> serverClass);

    /**
     * 添加目标服务
     *
     * @param serverName 服务名称
     */
    void addTargetServer(String serverName);

}
