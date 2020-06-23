package org.r.framework.thrift.client.core.provider;

import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TMultiplexedProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.r.framework.thrift.client.core.observer.ServiceObserver;
import org.r.framework.thrift.client.core.wrapper.ServiceWrapper;
import org.springframework.util.CollectionUtils;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * date 2020/5/7 21:59
 *
 * @author casper
 */
public class DefaultProtocolProvider implements ProtocolProvider {

    private ConcurrentMap<String, TProtocol> pool = new ConcurrentHashMap<>();

    private final ServiceInfoProvider serviceInfoProvider;
    private List<ServiceObserver> observers;

    public DefaultProtocolProvider(ServiceInfoProvider serviceInfoProvider) {
        this.serviceInfoProvider = serviceInfoProvider;
        observers = new LinkedList<>();
        refresh();
    }

    private void refresh() {
        List<ServiceWrapper> allServer = serviceInfoProvider.getAllServer();
        if (!CollectionUtils.isEmpty(allServer)) {
            for (ServiceWrapper serviceWrapper : allServer) {
                registry(serviceWrapper.getName(), serviceWrapper.getHost(), serviceWrapper.getPort());
            }
        }
        notifyAllObserver();
    }

    private void registry(String serverName, String host, int port) {
        TTransport transport = new TSocket(host, port);
        pool.put(serverName, new TMultiplexedProtocol(new TBinaryProtocol(transport), serverName));
    }

    private void notifyAllObserver() {
        if (!CollectionUtils.isEmpty(this.observers)) {
            for (ServiceObserver observer : this.observers) {
                observer.updateClientList();
            }
        }
    }

    @Override
    public TProtocol getProtocol(String serverName) {
        return pool.get(serverName);
    }

    @Override
    public void addObserver(ServiceObserver observer) {
        this.observers.add(observer);
    }


}
