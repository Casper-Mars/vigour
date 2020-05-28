package org.r.framework.thrift.client.core.builder;

import org.apache.thrift.protocol.TProtocol;
import org.r.framework.thrift.client.core.wrapper.ServerWrapper;

/**
 * date 2020/5/7 22:24
 *
 * @author casper
 */
public interface ProtocolBuilder {


    TProtocol build(ServerWrapper wrapper);

}
