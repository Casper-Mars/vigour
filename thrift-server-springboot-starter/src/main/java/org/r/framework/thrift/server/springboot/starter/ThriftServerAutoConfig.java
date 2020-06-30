package org.r.framework.thrift.server.springboot.starter;

import org.apache.thrift.TProcessor;
import org.r.framework.thrift.server.core.server.NettyServer;
import org.r.framework.thrift.server.core.server.ServerDelegate;
import org.r.framework.thrift.server.core.server.ThriftServer;
import org.r.framework.thrift.server.core.wrapper.ServerDefinition;
import org.r.framework.thrift.server.springboot.starter.annotation.ThriftService;
import org.r.framework.thrift.server.springboot.starter.config.ConfigProperties;
import org.r.framework.thrift.server.springboot.starter.config.ServerConfig;
import org.r.framework.thrift.server.springboot.starter.factory.DefaultProcessorBuilderFactory;
import org.r.framework.thrift.server.springboot.starter.factory.ProcessorBuilderFactory;
import org.r.framework.thrift.server.springboot.starter.provider.ServerInfoProvider;
import org.r.framework.thrift.server.springboot.starter.wrapper.ServerWrapper;
import org.r.framework.thrift.server.springboot.starter.wrapper.ServiceBeanWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;

/**
 * date 20-4-30 下午3:19
 *
 * @author casper
 **/
@Configuration
@EnableConfigurationProperties(ConfigProperties.class)
public class ThriftServerAutoConfig {

    private final Logger log = LoggerFactory.getLogger(ThriftServerAutoConfig.class);

    @Resource
    private ConfigProperties configProperties;

    @Bean
    @ConditionalOnMissingBean(ServerInfoProvider.class)
    @ConditionalOnProperty(name = "${thrift.server.enable}", havingValue = "true")
    public ServerInfoProvider serverInfoProvider(ApplicationContext applicationContext) {
        String[] servicesName = applicationContext.getBeanNamesForAnnotation(ThriftService.class);
        if (servicesName.length <= 0 || servicesName[0] == null) {
            throw new RuntimeException("missing service bean while starting the thrift server");
        }

        int port = configProperties.getServer().getPort() == 0 ? getBindingPort() : configProperties.getServer().getPort();
        List<ServiceBeanWrapper> wrappers = new ArrayList<>(servicesName.length);
        for (String s : servicesName) {
            Object bean = applicationContext.getBean(s);
            wrappers.add(new ServiceBeanWrapper(bean));
        }
        return new ServerInfoProvider(port, wrappers);
    }


    @Bean
    @ConditionalOnMissingBean(ServerBootstrap.class)
    @ConditionalOnProperty(name = "${thrift.server.enable}", havingValue = "true")
    public ServerBootstrap buildServer(ApplicationContext applicationContext, ServerInfoProvider serverInfoProvider) {
        ServerConfig serverConfig = configProperties.getServer();
        /*处理thrift的服务，并生成TProcessor实例*/
        String name = applicationContext.getEnvironment().getProperty("spring.application.name");
        String version = serverConfig.getThriftVersion();
        ProcessorBuilderFactory factory = new DefaultProcessorBuilderFactory(version);
        ServerWrapper serverWrapper = new ServerWrapper(factory, serverInfoProvider.getAllService());
        /*服务实例*/
        ServerDelegate delegate;
        if (serverConfig.getNetty().isEnable()) {
            delegate = new NettyServer();
        } else {
            delegate = new ThriftServer();
        }
        /*构建服务定义对象*/
        serverConfig.setPort(serverInfoProvider.getPort());
        serverConfig.setName(StringUtils.isEmpty(name) ? "thrift-server" : name);
        ServerDefinition serverDefinition = getServerDefinition(serverConfig, serverWrapper.getProcessor());
        return new ServerBootstrap(delegate, serverDefinition);
    }


    private int getBindingPort() {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(0);
        } catch (IOException e) {
            log.error("can not get a random port!!!!!!");
            System.exit(-1);
        }
        int localPort = serverSocket.getLocalPort();
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return localPort;
    }


    private ServerDefinition getServerDefinition(ServerConfig serverConfig, TProcessor processor) {
        ServerDefinition.ServerDefinitionBuilder builder = ServerDefinition.createBuilder();
        builder.port(serverConfig.getPort())
                .name(serverConfig.getName())
                .maxConnections(serverConfig.getMaxConnections())
                .maxFrameSize(serverConfig.getMaxFrameSize())
                .processor(processor);
        if (serverConfig.getNetty().isEnable()) {
            builder.nettyBossPoolSize(serverConfig.getNetty().getBossPoolSize())
                    .nettyWorkPoolSize(serverConfig.getNetty().getWorkPoolSize());
        }
        return builder.build();
    }


}
