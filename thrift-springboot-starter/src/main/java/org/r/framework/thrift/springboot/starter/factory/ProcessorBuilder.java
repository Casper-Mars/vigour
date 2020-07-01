package org.r.framework.thrift.springboot.starter.factory;

import org.apache.thrift.TProcessor;
import org.r.framework.thrift.springboot.starter.wrapper.ServiceBeanWrapper;

import java.util.List;

/**
 * date 20-4-30 下午3:53
 *
 * @author casper
 **/
public interface ProcessorBuilder {


    /**
     * 构建出thrift的处理类
     *
     * @param wrapper 服务bean的封装信息
     * @return
     */
    TProcessor build(ServiceBeanWrapper wrapper);

    /**
     * 批量构建出thrift的处理类
     *
     * @param wrappers 服务bean的封装信息
     * @return
     */
    TProcessor build(List<ServiceBeanWrapper> wrappers);


}
