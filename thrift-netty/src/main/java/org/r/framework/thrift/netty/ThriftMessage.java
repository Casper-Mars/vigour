/*
 * Copyright (C) 2012-2016 Facebook, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.r.framework.thrift.netty;


import io.netty.buffer.ByteBuf;

public class ThriftMessage {

    private final ByteBuf originBuf;
    private final ThriftTransportType transportType;
    private long processStartTimeMillis;
    private final Integer requestId;
    private ByteBuf contentBuf;


    public ThriftMessage(ByteBuf originBuf, ThriftTransportType transportType) {
        this(originBuf, transportType, null);
    }

    public ThriftMessage(ByteBuf originBuf, ThriftTransportType transportType, Integer requestId) {
        this.originBuf = originBuf;
        this.transportType = transportType;
        this.requestId = requestId;
    }

    public ByteBuf getOriginBuf() {
        return originBuf;
    }

    public Integer getRequestId() {
        return requestId;
    }

    public ThriftTransportType getTransportType() {
        return transportType;
    }


    /**
     * 获取消息的字节数据，包括原始的thrift数据和请求的id
     *
     * @return
     */
    public ByteBuf getContent() {
        if (contentBuf == null) {
            synchronized (this) {
                if (contentBuf == null) {
                    contentBuf = originBuf.alloc()
                            .buffer(ThriftMessageConstants.MESSAGE_REQUEST_ID_SIZE + originBuf.readableBytes())
                            .writeInt(requestId)
                            .writeBytes(originBuf);
                    originBuf.resetReaderIndex();
                }
            }
        }
        return contentBuf;
    }


    /**
     * 获取构建消息体的工厂，工厂负责产生和此消息的传输类型一样的消息体
     *
     * @return
     */
    public Factory getMessageFactory() {
        return messageBuffer -> new ThriftMessage(messageBuffer, getTransportType(), requestId);
    }

    /**
     * Standard Thrift clients require ordered responses, so even though Nifty can run multiple
     * requests from the same client at the same time, the responses have to be held until all
     * previous responses are ready and have been written. However, through the use of extended
     * protocols and codecs, a request can indicate that the client understands
     * out-of-order responses.
     *
     * @return {@code true} if ordered responses are required
     */
    public boolean isOrderedResponsesRequired() {
        return false;
    }

    public long getProcessStartTimeMillis() {
        return processStartTimeMillis;
    }

    public void setProcessStartTimeMillis(long processStartTimeMillis) {
        this.processStartTimeMillis = processStartTimeMillis;
    }

    public interface Factory {
        ThriftMessage create(ByteBuf messageBuffer);
    }
}
