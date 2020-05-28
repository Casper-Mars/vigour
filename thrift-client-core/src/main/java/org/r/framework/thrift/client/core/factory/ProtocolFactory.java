package org.r.framework.thrift.client.core.factory;

import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TMultiplexedProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TTransport;

/**
 * date 2020/5/24 23:46
 *
 * @author casper
 */
public class ProtocolFactory {


    /**
     * 构建通讯协议
     *
     * @param tTransport 底层通讯socket
     * @param serverName 服务名称
     * @return
     */
    public TProtocol buildProtocol(TTransport tTransport, String serverName) {
        return new TMultiplexedProtocol(new TBinaryProtocol(tTransport), serverName);
    }


}
