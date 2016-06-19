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
package com.github.rmannibucau.resettable.cdi.api.event;

import com.github.rmannibucau.resettable.cdi.api.Event;
import com.github.rmannibucau.resettable.cdi.impl.ResettableContext;

/**
 * Triggered once reset has been done.
 */
@Event
public class AfterReset extends BaseEvent {
    private final ResettableContext.Result result;
    private final Exception exception;

    public AfterReset(final ResetRequest request, final ResettableContext.Result result, final Exception error) {
        super(request);
        this.result = result;
        this.exception = error;
    }

    public int getCount() {
        return result.getCount();
    }

    public Exception getException() {
        return exception;
    }
}
