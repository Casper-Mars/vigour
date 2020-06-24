package test.client;

import org.apache.thrift.TException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import test.iface.Request;
import test.iface.service.AuthService;
import test.iface.service.LoginService;

import javax.xml.ws.RequestWrapper;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * date 20-5-8 下午1:28
 *
 * @author casper
 **/
@RestController
@RequestMapping("/test")
public class TestController {

    @Autowired
    private LoginService.Iface loginService;
    @Autowired
    private AuthService.Iface authService;

    private AtomicInteger index = new AtomicInteger(1);

    @RequestMapping("/test")
    public String test(String echo) {
        String result = echo;
        Request request = new Request();
        request.setUsername(String.valueOf(index.getAndIncrement()));
        request.setPassword(echo);
        try {
            result = loginService.doAction(request);
        } catch (TException e) {
            e.printStackTrace();
        }
        System.out.println(result);
//        int size = 10;
//        Thread[] threads = new Thread[size];
//        for (int i = 0; i < size; i++) {
//            Thread tmp = new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    String result = "null";
//                    Request request = new Request();
//                    request.setUsername(String.valueOf(index.getAndIncrement()));
//                    request.setPassword(echo);
//                    try {
//                        result = loginService.doAction(request);
//                    } catch (TException e) {
//                        e.printStackTrace();
//                    }
//                    System.out.println(result);
//
//                }
//            });
//            threads[i] = tmp;
//            tmp.start();
//        }
//        for (Thread thread : threads) {
//            try {
//                thread.join();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
        return result;
    }

    @GetMapping("/auth")
    public String auth(String username, String password) {
        String result = null;
        try {
            result = authService.auth(username, password);
        } catch (TException e) {
            e.printStackTrace();
        }
        return result;
    }

}
