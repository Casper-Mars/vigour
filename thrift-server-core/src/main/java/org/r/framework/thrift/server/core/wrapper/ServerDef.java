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
package org.r.framework.thrift.server.core.wrapper;

import org.apache.thrift.TProcessor;
import org.r.framework.thrift.server.core.server.netty.core.TDuplexProtocolFactory;

import java.util.concurrent.Executor;

/**
 * Descriptor for a Thrift Server. This defines a listener port that Nifty need to start a Thrift endpoint.
 */
public class ServerDef
{
    private final int serverPort;
    private final int maxFrameSize;
    private final int maxConnections;
    private final int queuedResponseLimit;
    private final TProcessor processor;
    private final TDuplexProtocolFactory duplexProtocolFactory;
    private final Executor executor;
    private final String name;




    public ServerDef(
            String name,
            int serverPort,
            int maxFrameSize,
            int queuedResponseLimit,
            int maxConnections,
            TDuplexProtocolFactory duplexProtocolFactory,
            TProcessor processor,
            Executor executor
    )

    {
        this.name = name;
        this.serverPort = serverPort;
        this.maxFrameSize = maxFrameSize;
        this.maxConnections = maxConnections;
        this.queuedResponseLimit = queuedResponseLimit;
        this.processor = processor;
        this.duplexProtocolFactory = duplexProtocolFactory;
        this.executor = executor;
    }

    public int getServerPort()
    {
        return serverPort;
    }

    public int getMaxFrameSize()
    {
        return maxFrameSize;
    }

    public int getMaxConnections()
    {
        return maxConnections;
    }

    public int getQueuedResponseLimit()
    {
        return queuedResponseLimit;
    }


    public TDuplexProtocolFactory getDuplexProtocolFactory()
    {
        return duplexProtocolFactory;
    }

    public Object getClientIdleTimeout() {
        return null;
    }

    public Object getTaskTimeout() { return null; }

    public Object getQueueTimeout() { return null; }

    public Executor getExecutor()
    {
        return executor;
    }

    public String getName()
    {
        return name;
    }

    public TProcessor getProcessor() {
        return processor;
    }

}
