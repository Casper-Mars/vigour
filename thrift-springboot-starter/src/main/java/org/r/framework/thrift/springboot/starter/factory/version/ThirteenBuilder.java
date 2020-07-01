package org.r.framework.thrift.springboot.starter.factory.version;

import org.apache.thrift.TMultiplexedProcessor;
import org.apache.thrift.TProcessor;
import org.r.framework.thrift.springboot.starter.factory.ProcessorBuilder;
import org.r.framework.thrift.springboot.starter.wrapper.ServiceBeanWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.stream.Stream;

/**
 * date 20-4-30 下午4:09
 *
 * @author casper
 **/
public class ThirteenBuilder implements ProcessorBuilder {


    private final Logger log = LoggerFactory.getLogger(ThirteenBuilder.class);


    /**
     * 构建出thrift的处理类
     *
     * @param wrapper 服务bean的封装信息
     * @return
     */
    @Override
    public TProcessor build(ServiceBeanWrapper wrapper) {

        /**
         * 获取thrift生成的类的内部processor类
         * 1-先获取implement的接口类
         * 2-根据接口类获取定义的父类
         * 3-根据父类获取内部实现的processor类
         *
         * */
        Class<?> interfaceClass = wrapper.getInterfaceClass("$Iface");
        if (interfaceClass == null) {
            return null;
        }
        /*根据接口类获取定义的父类*/
        Class<?> declaringClass = interfaceClass.getDeclaringClass();
        /*筛选出processor类*/
        Class<?>[] declaredClasses = declaringClass.getDeclaredClasses();
        Class<TProcessor> processorClass = (Class<TProcessor>) Stream.of(declaredClasses)
                .filter(clazz -> clazz.getName().endsWith("$Processor"))
                .findFirst().orElse(null);

        if (processorClass == null) {
            return null;
        }
        try {
            Constructor<TProcessor> processorConstructor = processorClass.getConstructor(interfaceClass);
            log.info("Build a thrift processor with server name: {}",wrapper.getName());
            return BeanUtils.instantiateClass(processorConstructor, wrapper.getBean());
        } catch (NoSuchMethodException e) {
            e.printStackTrace();

        }
        return null;
    }

    /**
     * 批量构建出thrift的处理类
     *
     * @param wrappers 服务bean的封装信息
     * @return
     */
    @Override
    public TProcessor build(List<ServiceBeanWrapper> wrappers) {

        if(CollectionUtils.isEmpty(wrappers)){
            return null;
        }
        TMultiplexedProcessor processor = new TMultiplexedProcessor();
        for (ServiceBeanWrapper wrapper : wrappers) {
            TProcessor p = build(wrapper);
            if(p  == null){
                continue;
            }
            processor.registerProcessor(wrapper.getName(),p);
        }
        return processor;
    }
}
