package test.client;

import org.r.framework.thrift.client.core.annotation.EnableThriftClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * date 20-4-30 下午4:12
 *
 * @author casper
 **/
@SpringBootApplication
@EnableThriftClient(basePackage = "test.iface")
@EnableEurekaClient
public class TestApplication {


    public static void main(String[] args) {
        ConfigurableApplicationContext run = SpringApplication.run(TestApplication.class);
    }


}
