package org.r.framework.thrift.springboot.starter.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * @author casper
 * @date 20-5-7 下午4:47
 **/
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface ThriftClient {


}
