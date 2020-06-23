package test.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

/**
 * date 20-4-30 下午4:12
 *
 * @author casper
 **/
@SpringBootApplication
@EnableEurekaClient
public class TestApplication {


    public static void main(String[] args) {
        SpringApplication.run(TestApplication.class);
    }


}
