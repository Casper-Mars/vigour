package org.r.framework.thrift.springboot.starter.wrapper;

import org.r.framework.thrift.common.util.ClassTool;

/**
 * date 20-4-30 下午3:57
 *
 * @author casper
 **/
public class ServiceBeanWrapper {

    /**
     * 服务名称，默认是bean名称
     */
    private final String name;

    /**
     * 注入到ioc的bean对象
     */
    private final Object bean;


    public ServiceBeanWrapper(Object bean) {
        this.bean = bean;
        Class<?> interfaceClass = getInterfaceClass("$Iface");
        this.name = interfaceClass.getDeclaringClass().getSimpleName();
    }

    public ServiceBeanWrapper(String name, Object bean) {
        this.name = name;
        this.bean = bean;
    }

    public String getName() {
        return name;
    }

    public Object getBean() {
        return bean;
    }

    /**
     * 获取对象的类的实现接口
     *
     * @return
     */
    public Class<?>[] getInterfaceClasss() {

        return bean.getClass().getInterfaces();
    }

    /**
     * 后缀匹配查询出接口的类
     *
     * @param name 后缀
     * @return
     */
    public Class<?> getInterfaceClass(String name) {

        Class<?>[] interfaceClass = getInterfaceClasss();
        return ClassTool.filterClass(interfaceClass, name);
    }


}
