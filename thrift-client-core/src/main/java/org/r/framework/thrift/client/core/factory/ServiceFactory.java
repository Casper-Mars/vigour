package org.r.framework.thrift.client.core.factory;

import java.lang.reflect.InvocationTargetException;

/**
 * date 2020/6/23 下午1:16
 *
 * @author casper
 **/
public interface ServiceFactory {

    /**
     * 构建服务的代理类
     *
     * @param clazz 服务实现类
     * @return
     */
    Object buildServerProxy(Class<?> clazz) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException;


}
