package org.r.framework.thrift.springboot.starter;

import org.r.framework.thrift.common.util.ClassTool;
import org.r.framework.thrift.springboot.starter.annotation.ThriftClient;
import org.r.framework.thrift.springboot.starter.factory.ProxyClientBeanFactory;
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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * date 20-5-8 上午9:01
 *
 * @author casper
 **/
public class ThriftClientsRegistrar implements ImportBeanDefinitionRegistrar, ResourceLoaderAware, EnvironmentAware {

    private final Logger log = LoggerFactory.getLogger(ThriftClientsRegistrar.class);

    private Environment env;
    private ResourceLoader resourceLoader;

    private static boolean init = false;

    /**
     * 配置文件配置的基础包扫描路径
     */
    private String basePackage;

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
        if (init) {
            return;
        }
        /*
         * 只有在配置了启用客户端，才进行bean的注册
         * 客户端的自动装配分开了两个地方，后期再进一步整合一起
         * */
        String property = env.getProperty("thrift.client.enable");
        if (StringUtils.isEmpty(property) || property.equals("false")) {
            return;
        }
        basePackage = env.getProperty("thrift.client.base-package");
        registerThriftClient(importingClassMetadata, registry);
        init = true;
    }


    public void registerThriftClient(AnnotationMetadata metadata, BeanDefinitionRegistry registry) {

        /*获取全部有客户端注解的实现类*/
        Map<String, String> ifaceImplClass = getIfaceImplClass(metadata);
        if (CollectionUtils.isEmpty(ifaceImplClass)) {
            log.warn("Can not find any implement class with annotation ThriftClient");
            return;
        }
        for (Map.Entry<String, String> entry : ifaceImplClass.entrySet()) {
            registryClientProxy(registry, entry.getValue(), entry.getValue());
        }
    }

    /**
     * 获取有客户端注解的实现类
     *
     * @param metadata
     * @return
     */
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
                    log.info("Find implement class {} for server {}", className, serviceName);
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
        BeanDefinitionHolder holder = new BeanDefinitionHolder(beanDefinition, clientClassName + "-proxy");
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

    /**
     * 把被注解的类所在的包加入基础扫描包路径
     *
     * @param importingClassMetadata 被注解的信息
     * @return
     */
    protected Set<String> getBasePackages(AnnotationMetadata importingClassMetadata) {
        Set<String> basePackages = new HashSet<>();
        if (!StringUtils.isEmpty(basePackage)) {
            basePackages.add(basePackage);
        }
        basePackages.add(ClassUtils.getPackageName(importingClassMetadata.getClassName()));
        return basePackages;
    }

    /**
     * 从完整的class名称中提取出简易类名，并首字母小写
     * 例如：从java.lang.String中提取String，
     *
     * @param className 完整的类名称
     * @return
     */
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
