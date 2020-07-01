package org.r.framework.thrift.springboot.starter;

import org.r.framework.thrift.netty.server.ServerDelegate;
import org.r.framework.thrift.netty.wrapper.ServerDefinition;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;

/**
 * date 2020/4/30 22:23
 *
 * @author casper
 */
public class ServerBootstrap implements ApplicationListener<ApplicationStartedEvent> {


    private final ServerDelegate delegate;
    private final ServerDefinition serverDefinition;

    public ServerBootstrap(ServerDelegate delegate, ServerDefinition serverDefinition) {
        this.delegate = delegate;
        this.serverDefinition = serverDefinition;
    }

//    public ServerBootstrap(ServerWrapper serverWrapper, ServerDelegate delegate, ConfigProperties configProperties) {
//        this.delegate = delegate;
//        this.configProperties = configProperties;
//        ServerConfig serverConfig = configProperties.getServer();
//
//        serverDefinition = ServerDefinition.createBuilder()
//                .name(serverConfig.getName())
//                .port(serverConfig.getPort())
//                .maxFrameSize(serverConfig.getMaxFrameSize())
//                .maxConnections(serverConfig.getMaxConnections())
//                .nettyBossPoolSize()
//                .build();
//    }

    private void start() {
        delegate.start(serverDefinition);
    }


    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        start();
    }
}
