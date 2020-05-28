package org.r.framework.thrift.client.core.wrapper;

/**
 * date 2020/5/19 11:31
 *
 * @author casper
 */
public class InstanceWrapper {

    private final String host;

    private final int port;




    public InstanceWrapper(String host, int port) {
        this.host = host;
        this.port = port;
    }


    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }


    @Override
    public boolean equals(Object obj) {
        if (obj instanceof InstanceWrapper) {
            InstanceWrapper tmp = (InstanceWrapper) obj;
            return host.equals(tmp.getHost()) && port == tmp.getPort();
        }
        return false;
    }
}
