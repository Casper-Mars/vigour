package org.r.framework.thrift.server.core.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * date 20-4-30 下午3:15
 *
 * @author casper
 **/
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Component
public @interface ThriftService {
    String name() default "";
}
