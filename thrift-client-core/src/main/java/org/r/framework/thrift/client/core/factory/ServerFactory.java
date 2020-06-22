package org.r.framework.thrift.client.core.factory;

import org.apache.thrift.protocol.TProtocol;
import org.r.framework.thrift.client.core.thread.ServerExecutor;
import org.r.framework.thrift.client.core.wrapper.TransportWrapper;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * date 2020/5/24 23:41
 *
 * @author casper
 */
public class ServerFactory {

    private final TransportWrapper transportWrapper;
    private final ProtocolFactory protocolFactory;
    private final String serverName;

    private volatile ServerExecutor client;

    public ServerFactory(String serverName, TransportWrapper transport, ProtocolFactory protocolFactory) {
        this.transportWrapper = transport;
        this.serverName = serverName;
        this.protocolFactory = protocolFactory;
    }

    /**
     * 获取服务执行器
     * @param clazz thrift客户端的实现类的class
     * @return
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws InstantiationException
     */
    public ServerExecutor getClient(Class<?> clazz) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        if(client == null){
            synchronized (this){
                if(client == null){
                    TProtocol protocol = protocolFactory.buildProtocol(transportWrapper.getTransport(), serverName);
                    Constructor<?> constructor = clazz.getConstructor(TProtocol.class);
                    Object instance = constructor.newInstance(protocol);
                    client = new ServerExecutor(instance,transportWrapper.getServiceExecutor());
                }
            }
        }
        return client;
    }

    public TransportWrapper getTransportWrapper() {
        return transportWrapper;
    }
}
