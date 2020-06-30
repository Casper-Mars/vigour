package test.server;

import org.apache.thrift.TException;
import org.r.framework.thrift.server.springboot.starter.annotation.ThriftService;
import org.springframework.beans.factory.annotation.Value;
import test.iface.Request;
import test.iface.service.LoginService;

/**
 * date 20-4-30 下午4:15
 *
 * @author casper
 **/
@ThriftService
public class TestServcie implements LoginService.Iface{

    @Value("${server.port}")
    private int port;

    @Override
    public String doAction(Request request) throws TException {
        return String.valueOf(port);
    }
}
