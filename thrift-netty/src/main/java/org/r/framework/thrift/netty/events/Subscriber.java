package org.r.framework.thrift.netty.events;

/**
 * date 2020/6/24 下午2:14
 *
 * @author casper
 **/
public interface Subscriber<T extends ChannelConnectEvent> {


    /**
     * 读邮件
     *
     * @param mail 邮件
     */
    void readMail(T mail);


}
