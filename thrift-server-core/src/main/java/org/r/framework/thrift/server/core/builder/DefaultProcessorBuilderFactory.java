package org.r.framework.thrift.server.core.builder;

import org.r.framework.thrift.server.core.builder.version.ThirteenBuilder;

/**
 * date 20-4-30 下午4:05
 *
 * @author casper
 **/

public class DefaultProcessorBuilderFactory implements ProcessorBuilderFactory {


    /**
     * thrift 的版本
     */
    private String thriftVersion;


    public DefaultProcessorBuilderFactory(String thriftVersion) {
        this.thriftVersion = thriftVersion;
    }

    @Override
    public ProcessorBuilder getBuilder() {



        return new ThirteenBuilder();
    }
}
