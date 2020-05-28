package org.r.framework.thrift.client.core.wrapper;

import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

/**
 * date 2020/5/7 22:45
 *
 * @author casper
 */
public class ProtocolWrapper {

    private TProtocol protocol;

    private TTransport tTransport;


    public ProtocolWrapper(TProtocol protocol, TTransport tTransport) {
        this.protocol = protocol;
        this.tTransport = tTransport;
    }

    public void open() throws TTransportException {
        tTransport.open();
    }

    public void close(){
        tTransport.close();
    }




}
