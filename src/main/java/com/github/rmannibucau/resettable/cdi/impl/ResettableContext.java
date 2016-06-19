/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.github.rmannibucau.resettable.cdi.impl;

import com.github.rmannibucau.resettable.cdi.api.ResettableScoped;
import com.github.rmannibucau.resettable.cdi.api.event.ResetRequest;

import javax.enterprise.context.spi.AlterableContext;
import javax.enterprise.context.spi.Contextual;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.Typed;
import java.lang.annotation.Annotation;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Typed
public class ResettableContext implements AlterableContext {
    private static final Object VALUE = new Object();

    private final AlterableContext delegate;
    private final ConcurrentMap<Contextual<?>, Object> instances = new ConcurrentHashMap<>();

    public ResettableContext(final AlterableContext context) {
        this.delegate = context;
    }

    @Override
    public Class<? extends Annotation> getScope() {
        return ResettableScoped.class;
    }

    @Override
    public <T> T get(final Contextual<T> contextual, final CreationalContext<T> creationalContext) {
        final T t = delegate.get(contextual, creationalContext);
        instances.putIfAbsent(contextual, t);
        return t;
    }

    @Override
    public <T> T get(final Contextual<T> contextual) {
        return delegate.get(contextual);
    }

    @Override
    public boolean isActive() {
        return delegate.isActive();
    }

    @Override
    public void destroy(final Contextual<?> contextual) {
        instances.remove(contextual);
        delegate.destroy(contextual);
    }

    Result reset(final ResetRequest request) {
        return new Result(instances.entrySet().stream().filter(e -> request.shouldReset(e.getKey(), e.getValue())).mapToInt(k -> {
            // note: we can need to lock to be fully correct there but if we don't the behavior is still ok in term of behavior
            // since reset events are not expected to have a high throughouput
            final Contextual<?> key = k.getKey();
            instances.remove(key);
            delegate.destroy(key);
            return 1;
        }).sum());
    }

    public static class Result {
        private final int count;

        public Result(final int count) {
            this.count = count;
        }

        public int getCount() {
            return count;
        }
    }
}
