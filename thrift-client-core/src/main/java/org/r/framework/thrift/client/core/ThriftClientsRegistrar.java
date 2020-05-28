package org.r.framework.thrift.client.core;

import org.r.framework.thrift.client.core.annotation.EnableThriftClient;
import org.r.framework.thrift.client.core.annotation.ThriftClient;
import org.r.framework.thrift.client.core.factory.ProxyClientBeanFactory;
import org.r.framework.thrift.client.core.provider.ClientWrapperProvider;
import org.r.framework.thrift.client.core.wrapper.ClientWrapper;
import org.r.framework.thrift.common.util.ClassTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * date 20-5-8 上午9:01
 *
 * @author casper
 **/
public class ThriftClientsRegistrar implements ImportBeanDefinitionRegistrar, ResourceLoaderAware, EnvironmentAware {

    private final Logger log = LoggerFactory.getLogger(ThriftClientsRegistrar.class);

    private Environment env;
    private ResourceLoader resourceLoader;

    @Override
    public void setEnvironment(Environment environment) {
        this.env = environment;
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }


    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {

        Map<String, Object> annotationAttributes = importingClassMetadata.getAnnotationAttributes(EnableThriftClient.class.getCanonicalName());
        if (annotationAttributes == null) {
            throw new RuntimeException("missing annotation info!!!!");
        }
        String basePackage = (String) annotationAttributes.get("basePackage");
        registerThriftClient(basePackage, importingClassMetadata, registry);
    }


    public void registerThriftClient(String ifaceBasePackage, AnnotationMetadata metadata, BeanDefinitionRegistry registry) {

        /*获取全部的thrift原生服务客户端接口*/
        ClientWrapperProvider clientWrapperProvider = new ClientWrapperProvider(ifaceBasePackage, this.getClass().getClassLoader());
        List<ClientWrapper> allClients = clientWrapperProvider.getAllClients();
        if (CollectionUtils.isEmpty(allClients)) {
            log.warn("0 client found in the project");
            return;
        }
        Map<String, String> ifaceImplClass = getIfaceImplClass(metadata);
        for (ClientWrapper client : allClients) {
            String fallbackClassName = ifaceImplClass.get(client.getServerName());
            registryClientProxy(registry, client.getClientClass().getName(), fallbackClassName);
        }
    }

    private Map<String, String> getIfaceImplClass(AnnotationMetadata metadata) {
        ClassPathScanningCandidateComponentProvider scanner = getScanner();
        scanner.setResourceLoader(this.resourceLoader);
        Set<String> basePackages;
        AnnotationTypeFilter annotationTypeFilter = new AnnotationTypeFilter(ThriftClient.class);
        scanner.addIncludeFilter(annotationTypeFilter);
        basePackages = getBasePackages(metadata);
        Map<String, String> result = new HashMap<>();
        for (String basePackage : basePackages) {
            Set<BeanDefinition> candidateComponents = scanner.findCandidateComponents(basePackage);
            for (BeanDefinition component : candidateComponents) {
                if (component instanceof AnnotatedBeanDefinition) {
                    AnnotatedBeanDefinition beanDefinition = (AnnotatedBeanDefinition) component;
                    AnnotationMetadata annotationMetadata = beanDefinition.getMetadata();
                    String className = annotationMetadata.getClassName();
                    String serviceName = getThriftServerNameByImplClassName(this.getClass().getClassLoader(), className);
                    if (StringUtils.isEmpty(serviceName)) {
                        continue;
                    }
                    result.put(serviceName, className);
                }
            }
        }
        return result;
    }

    private void registryClientProxy(BeanDefinitionRegistry registry, String clientClassName, String fallbackClassName) {

        /*
         * className作为key绑定的熔断的处理类组装比较复杂，为了偷懒，让spring负责组装注入
         * 因此className为key的beanDefinition要重新命名
         * */
//        String proxyBeanName = fallbackServiceName + "hystrix";
        BeanDefinitionBuilder definition = BeanDefinitionBuilder
                .genericBeanDefinition(ProxyClientBeanFactory.class);
        definition.addPropertyValue("ifaceType", clientClassName);
        if (!StringUtils.isEmpty(fallbackClassName)) {
            String fallbackBeanName = getServiceName(fallbackClassName);
            definition.addPropertyValue("fallbackBeanName", fallbackBeanName);
        }
        definition.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE);

        AbstractBeanDefinition beanDefinition = definition.getBeanDefinition();
        beanDefinition.setPrimary(true);
        BeanDefinitionHolder holder = new BeanDefinitionHolder(beanDefinition, clientClassName+"-proxy");
        BeanDefinitionReaderUtils.registerBeanDefinition(holder, registry);
    }


    protected ClassPathScanningCandidateComponentProvider getScanner() {
        return new ClassPathScanningCandidateComponentProvider(false, this.env) {
            @Override
            protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
                boolean isCandidate = false;
                if (beanDefinition.getMetadata().isIndependent()) {
                    if (!beanDefinition.getMetadata().isAnnotation()) {
                        isCandidate = true;
                    }
                }
                return isCandidate;
            }
        };
    }

    protected Set<String> getBasePackages(AnnotationMetadata importingClassMetadata) {
        Set<String> basePackages = new HashSet<>();
        basePackages.add(ClassUtils.getPackageName(importingClassMetadata.getClassName()));
        return basePackages;
    }

    private String getServiceName(String className) {

        String substring = className.substring(className.lastIndexOf('.') + 1);
        char[] chars = substring.toCharArray();
        chars[0] += 32;
        return new String(chars);
    }

    private String getThriftServerNameByImplClassName(ClassLoader loader, String className) {
        String result = null;
        try {
            Class<?> aClass = loader.loadClass(className);
            Class<?> iface = ClassTool.filterClass(aClass.getInterfaces(), "$Iface");
            if (iface != null) {
                result = iface.getDeclaringClass().getSimpleName();
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return result;
    }

}