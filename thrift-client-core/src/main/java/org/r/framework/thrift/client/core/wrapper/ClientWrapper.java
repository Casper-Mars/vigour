package org.r.framework.thrift.client.core.wrapper;

/**
 * date 20-5-7 下午5:38
 *
 * @author casper
 **/
public class ClientWrapper {

    /**
     * 服务名称
     */
    private String serverName;

    /**
     * 服务的接口实现类
     */
    private Class<?> clientClass;



    public ClientWrapper(String serverName, Class<?> clientClass) {
        this.serverName = serverName;
        this.clientClass = clientClass;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public Class<?> getClientClass() {
        return clientClass;
    }

    public void setClientClass(Class<?> clientClass) {
        this.clientClass = clientClass;
    }
}
