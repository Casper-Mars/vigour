package org.r.framework.thrift.server.core.builder;

import org.apache.thrift.TProcessor;
import org.r.framework.thrift.server.core.wrapper.ServerDef;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * date 20-5-7 上午11:33
 *
 * @author casper
 **/
public class ServerDefBuilder {


    public static InnerBuilder create() {
        return new InnerBuilder();
    }


    public static class InnerBuilder {

        private int serverPort = 8080;
        private int maxFrameSize = 64 * 1024 * 1024;
        private int maxConnections = 0;
        private int queuedResponseLimit = 16;
        private TProcessor processor;
        private Executor executor;
        private String name = "default server";


        public InnerBuilder serverPort(int serverPort) {
            this.serverPort = serverPort;
            return this;
        }

        public InnerBuilder maxFrameSize(int maxFrameSize) {
            this.maxFrameSize = maxFrameSize;
            return this;
        }

        public InnerBuilder maxConnections(int maxConnections) {
            this.maxConnections = maxConnections;
            return this;
        }

        public InnerBuilder queuedResponseLimit(int queuedResponseLimit) {
            this.queuedResponseLimit = queuedResponseLimit;
            return this;
        }

        public InnerBuilder processor(TProcessor processor) {
            this.processor = processor;
            return this;
        }


        public InnerBuilder executor(ExecutorService executorService) {
            this.executor = executorService;
            return this;
        }

        public InnerBuilder name(String name) {
            this.name = name;
            return this;
        }

        public ServerDef build(){

            if(executor == null){
                executor = Executors.newCachedThreadPool();
            }
            return new ServerDef(
                    name,
                    serverPort,
                    maxFrameSize,
                    queuedResponseLimit,
                    maxConnections,
                    processor,
                    executor
            );
        }


    }


}
