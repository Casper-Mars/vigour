package org.r.framework.thrift.server.core.wrapper;

import org.apache.thrift.TProcessor;
import org.r.framework.thrift.server.core.builder.ProcessorBuilderFactory;

import java.util.List;

/**
 * date 2020/4/30 22:39
 *
 * @author casper
 */
public class ServerWrapper {

    private final ProcessorBuilderFactory factory;
    private final List<ServiceBeanWrapper> wrappers;

    public ServerWrapper(ProcessorBuilderFactory factory, List<ServiceBeanWrapper> wrappers) {
        this.factory = factory;
        this.wrappers = wrappers;
    }


    public ProcessorBuilderFactory getFactory() {
        return factory;
    }

    public List<ServiceBeanWrapper> getWrappers() {
        return wrappers;
    }


    public TProcessor getProcessor() {

        return factory.getBuilder().build(wrappers);
    }


}
