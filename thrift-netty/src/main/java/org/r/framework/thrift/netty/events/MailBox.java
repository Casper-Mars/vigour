package org.r.framework.thrift.netty.events;

/**
 * date 2020/6/24 下午2:11
 *
 * @author casper
 **/
public class MailBox<T extends ChannelConnectEvent> {


    private final Postman<T> postman;

    public MailBox(Postman<T> postman) {
        this.postman = postman;
    }

    /**
     * 翻入数据
     *
     * @param mail 数据
     */
    public void putMail(T mail){
        postman.delivery(mail);
    }


}
