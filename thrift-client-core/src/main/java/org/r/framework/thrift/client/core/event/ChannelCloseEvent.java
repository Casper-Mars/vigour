package org.r.framework.thrift.client.core.event;

/**
 * date 2020/6/24 下午2:10
 *
 * @author casper
 **/
public class ChannelCloseEvent {

    private final String ip;

    private final int port;

    public ChannelCloseEvent(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }
}
