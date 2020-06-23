package test.client;

import org.apache.thrift.TException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import test.iface.Request;
import test.iface.service.LoginService;

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
//        ExecutorService service = Executors.newCachedThreadPool();
//        for (int i = 0; i < size; i++) {
//            service.submit(new Runnable() {
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
//        }
//        try {
//            service.awaitTermination(10, TimeUnit.SECONDS);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        return result;
    }


}
