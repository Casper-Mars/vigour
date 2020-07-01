package org.r.framework.thrift.netty.wrapper;



import org.r.framework.thrift.netty.core.codec.ThriftClientChannel;

import java.util.Objects;

/**
 * date 2020/6/23 下午1:54
 *
 * @author casper
 **/
public class ChannelWrapper {

    private final ThriftClientChannel channel;

    private final String ip;

    private final int port;

    public ChannelWrapper(ThriftClientChannel channel, String ip, int port) {
        this.channel = channel;
        this.ip = ip;
        this.port = port;
    }

    public ThriftClientChannel getChannel() {
        return channel;
    }

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChannelWrapper that = (ChannelWrapper) o;
        return port == that.port &&
                ip.equals(that.ip);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ip, port);
    }
}
