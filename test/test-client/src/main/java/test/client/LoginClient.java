package test.client;


import org.apache.thrift.TException;
import org.r.framework.thrift.springboot.starter.annotation.ThriftClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import test.iface.Request;
import test.iface.service.LoginService;

/**
 * date 20-5-7 下午4:49
 *
 * @author casper
 **/
@ThriftClient
@Component
public class LoginClient implements LoginService.Iface {

    @Autowired
    private UserService service;


    @Override
    public String doAction(Request request) throws TException {
        return "本地熔断";
    }
}
