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

import javax.enterprise.context.spi.Contextual;
import javax.enterprise.inject.spi.Bean;

/**
 * Firing this event will force to reset @ResettableScoped beans
 * matching the event shouldReset(Contextual) filtering.
 */
@Event
public class ResetRequest {
    /**
     * @param contextual the bean against which the evaluation should be done.
     * @return true if the bean should be resetted.
     */
    public boolean shouldReset(final Contextual<?> contextual, final Object instance) {
        return !Bean.class.isInstance(contextual) || shouldReset(Bean.class.cast(contextual), instance);
    }

    /**
     * just a helper method for shouldReset(Contextual) typing the parameter more.
     */
    public boolean shouldReset(final Bean<?> bean, final Object instance) {
        return true;
    }
}
