package org.r.framework.thrift.server.core.server;

import org.apache.thrift.TProcessor;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TSimpleServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TTransportException;
import org.r.framework.thrift.server.core.wrapper.ServerDef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * date 20-4-30 下午3:30
 *
 * @author casper
 **/

public class ThriftServer implements ServerDelegate {
    private Logger log = LoggerFactory.getLogger(ThriftServer.class);


    @Override
    public void start(ServerDef serverDef) {
        try {
            TProcessor processor = serverDef.getProcessor();
            TServerSocket tServerSocket = new TServerSocket(serverDef.getServerPort());
            TServer.Args args1 = new TServer.Args(tServerSocket);
            args1.processor(processor);
            TServer server = new TSimpleServer(args1);
            log.info("server start at " + serverDef.getServerPort());
            server.serve();
        } catch (TTransportException e) {
            e.printStackTrace();
        }
    }
}
