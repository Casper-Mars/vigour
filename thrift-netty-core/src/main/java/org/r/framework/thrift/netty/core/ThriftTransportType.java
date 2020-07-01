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
package org.r.framework.thrift.netty.core;

public enum ThriftTransportType {

    /**
     * 数据包的类型，区分unframed和framed的作用是为了兼容原生的thrift客户端和经过netty封装的thrift客户端
     * 在接收数据的时候判断出来，并记录在对应的消息中，以便在响应的时候做响应的逻辑
     */
    UNFRAMED,
    FRAMED
}
