package test.client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * date 20-4-30 下午4:12
 *
 * @author casper
 **/
@SpringBootApplication
public class TestClientApplication {


    public static void main(String[] args) {
        ConfigurableApplicationContext run = SpringApplication.run(TestClientApplication.class);
    }


}
