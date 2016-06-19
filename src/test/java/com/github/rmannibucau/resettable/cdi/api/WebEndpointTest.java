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

import com.github.rmannibucau.resettable.cdi.impl.servlet.ResettableEndpoint;
import org.apache.openejb.junit.ApplicationComposer;
import org.apache.openejb.loader.IO;
import org.apache.openejb.testing.Classes;
import org.apache.openejb.testing.EnableServices;
import org.apache.openejb.testing.RandomPort;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import javax.servlet.annotation.WebServlet;
import java.io.IOException;
import java.net.URL;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

@EnableServices("http")
@Classes(cdi = true, innerClassesAsBean = true)
@RunWith(ApplicationComposer.class)
public class WebEndpointTest {
    @RandomPort("http")
    private URL base;

    @Inject
    private App app;

    @Test
    public void resetAll() throws IOException {
        final String id = app.getId();
        assertEquals("{\"count\":1}", IO.slurp(new URL(base.toExternalForm() + "/openejb/reset")));
        assertNotEquals(id, app.getId());
    }

    @ResettableScoped
    public static class App {
        private final String id = UUID.randomUUID().toString();

        public String getId() {
            return id;
        }
    }

    @WebServlet("/reset")
    public static class EmbeddedEndpoint extends ResettableEndpoint {
    }
}
