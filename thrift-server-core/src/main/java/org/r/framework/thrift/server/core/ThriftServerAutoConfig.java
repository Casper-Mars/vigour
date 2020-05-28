package org.r.framework.thrift.server.core;

import org.r.framework.thrift.server.core.annotation.ThriftService;
import org.r.framework.thrift.server.core.builder.DefaultProcessorBuilderFactory;
import org.r.framework.thrift.server.core.builder.ProcessorBuilderFactory;
import org.r.framework.thrift.server.core.config.ConfigProperties;
import org.r.framework.thrift.server.core.provider.ServerInfoProvider;
import org.r.framework.thrift.server.core.server.NettyServer;
import org.r.framework.thrift.server.core.server.ServerDelegate;
import org.r.framework.thrift.server.core.server.ThriftServer;
import org.r.framework.thrift.server.core.wrapper.ServerWrapper;
import org.r.framework.thrift.server.core.wrapper.ServiceBeanWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
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
    public ServerInfoProvider serverInfoProvider(ApplicationContext applicationContext) {
        String[] servicesName = applicationContext.getBeanNamesForAnnotation(ThriftService.class);
        if (servicesName.length <= 0 || servicesName[0] == null) {
            throw new RuntimeException("missing service bean while starting the thrift server");
        }

        int port = configProperties.getPort() == 0 ? getBindingPort() : configProperties.getPort();
        List<ServiceBeanWrapper> wrappers = new ArrayList<>(servicesName.length);
        for (String s : servicesName) {
            Object bean = applicationContext.getBean(s);
            wrappers.add(new ServiceBeanWrapper(bean));
        }
        return new ServerInfoProvider(port, wrappers);
    }


    @Bean
    @ConditionalOnMissingBean(ServerBootstrap.class)
    public ServerBootstrap buildServer(ApplicationContext applicationContext, ServerInfoProvider serverInfoProvider) {
        String name = applicationContext.getEnvironment().getProperty("spring.application.name");
        String version = "0.13.0";
        ProcessorBuilderFactory factory = new DefaultProcessorBuilderFactory(version);
        ServerWrapper serverWrapper = new ServerWrapper(factory, serverInfoProvider.getAllService());
        ServerDelegate delegate;
        if (configProperties.getNetty().isEnable()) {
            delegate = new NettyServer();
        } else {
            delegate = new ThriftServer();
        }
        configProperties.setPort(serverInfoProvider.getPort());
        configProperties.setName(StringUtils.isEmpty(name) ? "thrift-server" : name);
        return new ServerBootstrap(serverWrapper, delegate, configProperties);
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

}
