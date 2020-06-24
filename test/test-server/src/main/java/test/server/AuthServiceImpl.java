package test.server;

import org.apache.thrift.TException;
import org.r.framework.thrift.server.core.annotation.ThriftService;
import test.iface.service.AuthService;

/**
 * date 2020/6/24 下午6:02
 *
 * @author casper
 **/
@ThriftService
public class AuthServiceImpl implements AuthService.Iface {
    @Override
    public String auth(String username, String password) throws TException {
        return username + "_" + password;
    }
}
