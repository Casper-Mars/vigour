package org.r.framework.thrift.netty.events;

/**
 * date 2020/6/30 17:56
 *
 * @author casper
 */
public class ChannelConnectionCloseEvent extends ChannelConnectEvent {

    private final String ip;

    private final int port;

    public ChannelConnectionCloseEvent(String ip, int port) {
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
