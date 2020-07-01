package org.r.framework.thrift.netty.server;

import org.apache.thrift.TProcessor;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TSimpleServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TTransportException;
import org.r.framework.thrift.netty.wrapper.ServerDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * date 20-4-30 下午3:30
 *
 * @author casper
 **/

public class ThriftServer implements ServerDelegate {
    private final Logger log = LoggerFactory.getLogger(ThriftServer.class);


    /**
     * 启动服务
     *
     * @param serverDefinition 服务信息
     */
    @Override
    public void start(ServerDefinition serverDefinition) {
        try {
            TProcessor processor = serverDefinition.getProcessor();
            TServerSocket tServerSocket = new TServerSocket(serverDefinition.getPort());
            TServer.Args args1 = new TServer.Args(tServerSocket);
            args1.processor(processor);
            TServer server = new TSimpleServer(args1);
            log.info("Server start at {}", serverDefinition.getPort());
            server.serve();
        } catch (TTransportException e) {
            e.printStackTrace();
        }

    }

    /**
     * 停止服务
     */
    @Override
    public void stop() {

    }
}
