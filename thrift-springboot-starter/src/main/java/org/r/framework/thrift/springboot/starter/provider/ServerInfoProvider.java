package org.r.framework.thrift.springboot.starter.provider;


import org.r.framework.thrift.springboot.starter.wrapper.ServiceBeanWrapper;

import java.util.ArrayList;
import java.util.List;

/**
 * date 20-5-9 下午3:33
 *
 * @author casper
 **/
public class ServerInfoProvider {


    /**
     * 服务信息
     */
    private final List<ServiceBeanWrapper> list;
    /**
     * 服务绑定的端口
     */
    private final int port;

    public ServerInfoProvider(int port, List<ServiceBeanWrapper> list) {
        this.list = list;
        this.port = port;
    }

    public int getPort() {
        return port;
    }

    /**
     * 获取全部的服务
     *
     * @return
     */
    public List<ServiceBeanWrapper> getAllService() {
        if (list == null) {
            return new ArrayList<>();
        }
        return list;
    }

}
