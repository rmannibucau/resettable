package com.github.rmannibucau.resettable.cdi.impl;

import com.github.rmannibucau.resettable.cdi.api.event.AfterReset;
import com.github.rmannibucau.resettable.cdi.api.event.BeforeReset;
import com.github.rmannibucau.resettable.cdi.api.event.ResetRequest;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;

@ApplicationScoped
public class ResetObserver {
    @Inject
    private ResettableExtension extension;

    @Inject
    private BeanManager bm;

    public void reset(@Observes final ResetRequest resetRequest) {
        bm.fireEvent(new BeforeReset(resetRequest));
        ResettableContext.Result result = null;
        Exception error = null;
        try {
            result = extension.getContext().reset(resetRequest);
        } catch (final Exception ex) {
            error = ex;
        } finally {
            bm.fireEvent(new AfterReset(resetRequest, result, error));
        }
    }
}
