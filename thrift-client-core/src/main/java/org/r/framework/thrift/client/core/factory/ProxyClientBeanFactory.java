package org.r.framework.thrift.client.core.factory;

import org.apache.thrift.protocol.TProtocol;
import org.r.framework.thrift.client.core.ProxyClient;
import org.r.framework.thrift.client.core.manager.ClientManager;
import org.r.framework.thrift.common.util.ClassTool;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * date 20-5-8 下午12:44
 *
 * @author casper
 **/
public class ProxyClientBeanFactory implements InitializingBean, FactoryBean<Object>, ApplicationContextAware {


    private Class<?> ifaceType;
    private String fallbackBeanName;
    private ApplicationContext context;


    @Override
    public Object getObject() throws Exception {
        ClientManager manager = context.getBean(ClientManager.class);
        /*
         * 1 通过实现了Service.Iface接口的iface实现类，回溯出 Service
         * 2 通过Service找到Client实现类
         * 3 运用cglib，生成iface实现类的代理类
         * 4 注入代理类
         *
         * */
        Object originBean = null;
        try {
            if (!StringUtils.isEmpty(fallbackBeanName)) {
                originBean = context.getBean(fallbackBeanName);
            }
        } catch (Exception e) {

        }
        Class<?> iface = ClassTool.filterClass(ifaceType.getInterfaces(), "$Iface");
        Class<?> serviceClass = iface.getDeclaringClass();
        Class<?> clientClass = ClassTool.filterClass(serviceClass.getDeclaredClasses(), "$Client");
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(clientClass);
        ProxyClient proxyClient = new ProxyClient(serviceClass.getSimpleName(), manager, originBean, clientClass);
        enhancer.setCallback(proxyClient);
        Class<?>[] arg = new Class[]{TProtocol.class};
        Object[] argv = new Object[]{null};
        return enhancer.create(arg, argv);
    }

    @Override
    public Class<?> getObjectType() {
        return this.ifaceType;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(this.ifaceType, "client class must be set");
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }


    public Class<?> getIfaceType() {
        return ifaceType;
    }

    public void setIfaceType(Class<?> ifaceType) {
        this.ifaceType = ifaceType;
    }

    public String getFallbackBeanName() {
        return fallbackBeanName;
    }

    public void setFallbackBeanName(String fallbackBeanName) {
        this.fallbackBeanName = fallbackBeanName;
    }
}
