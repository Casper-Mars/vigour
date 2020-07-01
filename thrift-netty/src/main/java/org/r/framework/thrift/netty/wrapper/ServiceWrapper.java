package org.r.framework.thrift.netty.wrapper;

/**
 * date 2020/5/7 21:16
 *
 * @author casper
 */
public class ServiceWrapper {

    private String host;

    private int port;

    private String name;

    private boolean isAvailable;

    public ServiceWrapper(String host, int port, String name, boolean isAvailable) {
        this.host = host;
        this.port = port;
        this.name = name;
        this.isAvailable = isAvailable;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }
}
