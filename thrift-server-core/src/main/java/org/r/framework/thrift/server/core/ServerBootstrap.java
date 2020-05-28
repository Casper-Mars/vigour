package org.r.framework.thrift.server.core;

import org.r.framework.thrift.server.core.builder.ServerDefBuilder;
import org.r.framework.thrift.server.core.config.ConfigProperties;
import org.r.framework.thrift.server.core.server.ServerDelegate;
import org.r.framework.thrift.server.core.wrapper.ServerDef;
import org.r.framework.thrift.server.core.wrapper.ServerWrapper;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;

/**
 * date 2020/4/30 22:23
 *
 * @author casper
 */
public class ServerBootstrap implements ApplicationListener<ApplicationStartedEvent> {



    private final ServerDelegate delegate;
    private final ConfigProperties configProperties;
    private final ServerDef serverDef;

    public ServerBootstrap(ServerWrapper serverWrapper,ServerDelegate delegate, ConfigProperties configProperties) {
        this.delegate = delegate;
        this.configProperties = configProperties;
        serverDef = ServerDefBuilder.create()
                .serverPort(configProperties.getPort())
                .processor(serverWrapper.getProcessor())
                .name(configProperties.getName())
                .build();
    }

    private void start() {
        if (configProperties.isEnable()) {
            delegate.start(serverDef);
        }
    }


    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        start();
    }
}
