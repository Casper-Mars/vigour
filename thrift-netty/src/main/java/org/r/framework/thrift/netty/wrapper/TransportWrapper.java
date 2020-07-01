package org.r.framework.thrift.netty.wrapper;

import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;

/**
 * date 2020/5/21 18:47
 *
 * @author casper
 */
public class TransportWrapper {

    private final String host;
    private final int port;
    private final TTransport transport;

    public TransportWrapper(String host, int port) {
        this.host = host;
        this.port = port;
        this.transport = new TSocket(host, port);
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public TTransport getTransport() {
        return transport;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof TransportWrapper){
            TransportWrapper tmp = (TransportWrapper) obj;
            return host.equals(tmp.getHost()) && port == tmp.getPort();
        }
        return false;
    }

    @Override
    public int hashCode() {
        return host.hashCode()+port;
    }
}
