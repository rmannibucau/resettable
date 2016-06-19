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
package com.github.rmannibucau.resettable.cdi.api;

import com.github.rmannibucau.resettable.cdi.api.event.ResetRequest;
import org.apache.openejb.junit.ApplicationComposer;
import org.apache.openejb.testing.Classes;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@Classes(cdi = true, innerClassesAsBean = true)
@RunWith(ApplicationComposer.class)
public class ResettableScopedTest {
    @Inject
    private BeanManager bm;

    @Inject
    private App1 app1;

    @Inject
    private App2 app2;

    @Test
    public void resetAll() {
        final Collection<String> ids = initIds();
        bm.fireEvent(new ResetRequest());
        Stream.of(app1, app2).forEach(app -> assertFalse(ids.contains(app.getId())));
    }

    @Test
    public void resetSelected() {
        final Collection<String> ids = initIds();
        bm.fireEvent(new ResetRequest() {
            @Override
            public boolean shouldReset(final Bean<?> bean, final Object instance) {
                return bean.getBeanClass() == App1.class && app1.equals(instance);
            }
        });
        assertFalse(ids.contains(app1.getId()));
        assertTrue(ids.contains(app2.getId()));
    }

    private Collection<String> initIds() {
        final Collection<String> ids = new ArrayList<>();
        Stream.of(app1, app2).forEach(app -> ids.add(app.getId()));
        Stream.of(app1, app2).forEach(app -> assertTrue(ids.contains(app.getId()))); // check beans are app scoped
        return ids;
    }

    protected static class Base {
        private final String id = UUID.randomUUID().toString();

        public String getId() {
            return id;
        }
    }

    @ResettableScoped
    public static class App1 extends Base {
    }

    @ResettableScoped
    public static class App2 extends Base {
    }
}
