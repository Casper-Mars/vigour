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
package org.r.framework.thrift.server.core.server.netty.core;

import io.netty.util.HashedWheelTimer;

import javax.annotation.PreDestroy;
import java.io.Closeable;
import java.util.concurrent.TimeUnit;

public final class DefaultTimer
    extends HashedWheelTimer
    implements Closeable
{
    public DefaultTimer(String prefix, long tickDuration, TimeUnit unit, int ticksPerWheel)
    {
        super(r -> new Thread(r,prefix),
                tickDuration,
                unit,
                ticksPerWheel);
    }

    public DefaultTimer(String prefix)
    {
        this(prefix, 100, TimeUnit.MILLISECONDS, 512);
    }

    @PreDestroy
    @Override
    public void close()
    {
        stop();
    }
}
