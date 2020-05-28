package org.r.framework.thrift.client.core.provider;

import org.apache.thrift.protocol.TProtocol;
import org.r.framework.thrift.client.core.observer.ServiceObserver;

/**
 * date 20-5-7 下午5:49
 *
 * @author casper
 **/
public interface ProtocolProvider {

    /**
     * 根据服务名称获取协议
     *
     * @param serverName 服务名称
     * @return
     */
    TProtocol getProtocol(String serverName);

    /**
     * 添加观察者
     *
     * @param observer 观察者
     */
    void addObserver(ServiceObserver observer);


}
