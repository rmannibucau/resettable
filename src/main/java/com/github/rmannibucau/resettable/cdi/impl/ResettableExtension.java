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
import com.github.rmannibucau.resettable.cdi.impl.servlet.ResettableEndpoint;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.spi.AlterableContext;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.BeforeBeanDiscovery;
import javax.enterprise.inject.spi.Extension;
import java.util.stream.Stream;

public class ResettableExtension implements Extension {
    private ResettableContext context;

    void activeScope(@Observes final BeforeBeanDiscovery bbd, final BeanManager bm) {
        bbd.addScope(ResettableScoped.class, true, false);

        // ensure these beans are registered
        Stream.of(ResetObserver.class, ResettableEndpoint.Waiter.class)
                .forEach(t -> bbd.addAnnotatedType(bm.createAnnotatedType(t)));
    }

    void activeContext(@Observes final AfterBeanDiscovery abd, final BeanManager bm) {
        context = new ResettableContext(AlterableContext.class.cast(bm.getContext(ApplicationScoped.class)));
        abd.addContext(context);
    }

    public ResettableContext getContext() {
        if (context == null) {
            throw new IllegalStateException("Calling getContext() too early, CDI is not yet started");
        }
        return context;
    }
}
