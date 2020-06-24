package test.client;

import org.apache.thrift.TException;
import org.r.framework.thrift.client.core.annotation.ThriftClient;
import test.iface.service.AuthService;

/**
 * date 2020/6/24 下午6:04
 *
 * @author casper
 **/
@ThriftClient
public class AuthClient implements AuthService.Iface {
    @Override
    public String auth(String username, String password) throws TException {
        return "本地熔断_auth";
    }
}
