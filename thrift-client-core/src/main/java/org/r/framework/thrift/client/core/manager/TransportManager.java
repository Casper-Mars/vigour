package org.r.framework.thrift.client.core.manager;

import org.apache.thrift.transport.TTransportException;
import org.r.framework.thrift.client.core.exception.TransportFailException;
import org.r.framework.thrift.client.core.wrapper.ServerWrapper;
import org.r.framework.thrift.client.core.wrapper.TransportWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * date 2020/5/21 18:46
 *
 * @author casper
 */
public class TransportManager {

    private final Logger log = LoggerFactory.getLogger(TransportManager.class);

    /**
     * transport列表
     */
    private final Map<String, TransportWrapper> transports;

    /**
     * 观察者
     */
    private final List<Function<TransportWrapper, Boolean>> observers;

    public TransportManager() {
        this.observers = new LinkedList<>();
        this.transports = new ConcurrentHashMap<>();
    }

    /**
     * 根据host地址和端口获取协议信息
     *
     * @param host 地址
     * @param port 端口
     * @return
     */
    public TransportWrapper getTransportWrapper(String host, int port) throws TransportFailException {
        String signature = signature(host, port);
        TransportWrapper transportWrapper = transports.get(signature);
        if (transportWrapper == null) {
            synchronized (this) {
                transportWrapper = this.transports.get(signature);
                if (transportWrapper == null) {
                    transportWrapper = new TransportWrapper(host, port);
                    try {
                        transportWrapper.getTransport().open();
                    } catch (TTransportException e) {
                        log.error("can not open transport:" + host + ":" + port);
                        log.error("{}", e.getMessage(), e);
                        throw new TransportFailException("无法打开链接");
                    }
                    transports.put(signature, transportWrapper);
                }
            }
        }
        return transportWrapper;
    }


    /**
     * 获取签名
     *
     * @param host 地址
     * @param port 端口
     * @return
     */
    private String signature(String host, int port) {
        return String.format("%s:%d", host, port);
    }


    /**
     * 添加观察者
     *
     * @param observer 观察者
     */
    public void observedBy(Function<TransportWrapper, Boolean> observer) {
        this.observers.add(observer);
    }


    /**
     * 删除transport
     *
     * @param host 地址
     * @param port 端口
     */
    public void deleteTransport(String host, int port) {
        String signature = signature(host, port);
        TransportWrapper transportWrapper = transports.get(signature);
        if (transportWrapper != null) {
            this.transports.remove(signature);
            deleteNotification(transportWrapper);
            transportWrapper.getTransport().close();
        }
    }

    /**
     * 更新transport列表
     *
     * @param serverWrappers 最新的transport列表
     * @return 新增的transport列表
     */
    public void updateTransportList(List<ServerWrapper> serverWrappers) {
        /*删除最新列表上没有的服务地址*/
        Map<String, ServerWrapper> serverWrapperMap = serverWrappers.stream().collect(Collectors.toMap(t -> signature(t.getHost(), t.getPort()), t -> t, (k1, k2) -> k1));
        Collection<String> signaltrues = serverWrapperMap.keySet();
        for (String signaltrue : transports.keySet()) {
            if (!signaltrues.contains(signaltrue)) {
                TransportWrapper wrapper = transports.get(signaltrue);
                deleteTransport(wrapper.getHost(), wrapper.getPort());
            }
        }
    }


    /**
     * transport被删除的通知，通知所有的观察者，
     * 通知返回true，则要移除当前的观察者
     *
     * @param tTransport 被删除的transport
     */
    private void deleteNotification(TransportWrapper tTransport) {
        List<Function<TransportWrapper, Boolean>> deleteList = new LinkedList<>();
        for (Function<TransportWrapper, Boolean> observer : this.observers) {
            Boolean apply = observer.apply(tTransport);
            if (apply == null || apply) {
                deleteList.add(observer);
            }
        }
        if (!CollectionUtils.isEmpty(deleteList)) {
            this.observers.removeAll(deleteList);
        }
    }

}
