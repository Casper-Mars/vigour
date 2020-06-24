package org.r.framework.thrift.client.core.manager;

/**
 * @author casper
 * @date 2020/6/23 下午12:57
 **/
public interface ServiceManager {


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
    void registryService(String host, int port);


}
