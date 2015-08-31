package org.philippides.frame;

import java.io.InputStream;
import java.util.function.Supplier;

import org.philippides.data.IValue;

public class Content {
    private IValue performative;
    private Supplier<InputStream> payloadStreamSupplier;

    public Content(IValue performative, Supplier<InputStream> payloadStreamSupplier) {
        this.performative = performative;
        this.payloadStreamSupplier = payloadStreamSupplier;
    }

    public Content(IValue performative) {
        this(performative, null);
    }

    public IValue getPerformative() {
        return performative;
    }

    public InputStream getPayloadStream() {
        return null != payloadStreamSupplier ? payloadStreamSupplier.get() : null;
    }

    @Override
    public String toString() {
        return "Content [performative=" + performative + "]";
    }
}
