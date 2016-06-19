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

import com.github.rmannibucau.resettable.cdi.api.event.AfterReset;
import com.github.rmannibucau.resettable.cdi.api.event.ResetRequest;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;

public class ResettableEndpoint extends HttpServlet {
    @Inject
    private BeanManager bm;

    @Inject
    private Waiter waiter;

    @Override
    public void init() throws ServletException {
        super.init();
        getServletContext().log("Started @ResettableScoped on " + getInitParameter("mapping"));
    }

    @Override
    protected void doGet(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
        final LatchEvent event = new LatchEvent();
        bm.fireEvent(event);
        try {
            event.latch.await();
        } catch (final InterruptedException e) {
            Thread.interrupted();
            // we likely don't care much since we are shutting down
        }
        resp.setContentType("application/json");
        resp.getWriter().write("{\"count\":" + event.afterReset.getCount() + "}");
    }

    static class LatchEvent extends ResetRequest {
        private final CountDownLatch latch = new CountDownLatch(1);
        private volatile AfterReset afterReset;
    }

    @ApplicationScoped
    public static class Waiter {
        void after(@Observes final AfterReset afterReset) {
            final ResetRequest request = afterReset.getRequest();
            if (LatchEvent.class.isInstance(request)) {
                final LatchEvent latchEvent = LatchEvent.class.cast(request);
                latchEvent.latch.countDown();
                latchEvent.afterReset = afterReset;
            }
        }
    }
}
