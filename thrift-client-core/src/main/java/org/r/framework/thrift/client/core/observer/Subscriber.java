package org.r.framework.thrift.client.core.observer;

/**
 * date 2020/6/24 下午2:14
 *
 * @author casper
 **/
public interface Subscriber<T> {


    /**
     * 读邮件
     *
     * @param mail 邮件
     */
    void readMail(T mail);


}
