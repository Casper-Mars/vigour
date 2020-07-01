package org.r.framework.thrift.springboot.starter.annotation;

import org.r.framework.thrift.springboot.starter.ThriftClientsRegistrar;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * date 2020/5/7 23:39
 *
 * @author casper
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import(ThriftClientsRegistrar.class)
public @interface EnableThriftClient {

}
