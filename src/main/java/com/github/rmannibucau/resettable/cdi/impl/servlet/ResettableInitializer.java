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
package com.github.rmannibucau.resettable.cdi.impl.servlet;

import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import java.util.Set;

import static java.util.Optional.ofNullable;

public class ResettableInitializer implements ServletContainerInitializer {
    @Override
    public void onStartup(final Set<Class<?>> c, final ServletContext ctx) throws ServletException {
        final String mapping = getConfig(ctx, "resettable.endpoint", "/reset");
        final ServletRegistration.Dynamic servlet = ctx.addServlet("ResettableScoped", ResettableEndpoint.class);
        servlet.setInitParameter("mapping", mapping);
        servlet.addMapping(mapping);
    }

    private static String getConfig(final ServletContext ctx, final String key, final String defaultValue) {
        return ofNullable(System.getProperty(key))
                .orElseGet(() -> ofNullable(ctx.getInitParameter(key)).orElse(defaultValue));
    }
}
