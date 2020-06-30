package org.r.framework.thrift.netty;

/**
 * date 2020/6/25 14:30
 *
 * @author casper
 */
public class ThriftMessageConstants {

    /**
     * 消息请求id的字节数，4个字节
     */
    public static final int MESSAGE_REQUEST_ID_SIZE = 4;


    /**
     * thrift协议帧的长度变量是4个字节的整形，此常量是指这个长度变量的字节数
     */
    public static final int LENGTH_FIELD_LENGTH = 4;


}
