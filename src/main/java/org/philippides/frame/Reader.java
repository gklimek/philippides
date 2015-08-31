package org.philippides.frame;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.function.Supplier;

import org.philippides.data.IValue;
import org.philippides.data.ValueUtil;

public class Reader {
    public Content read(Frame frame) throws IOException {
        ByteArrayInputStream frameBodyInputStream = frame.getBodyInputStream();
        IValue performative = ValueUtil.fromStream(frameBodyInputStream);
        Supplier<InputStream> payloadStreamSupplier = () -> frame.getBodyInputStream(-frameBodyInputStream.available());
        return new Content(performative, payloadStreamSupplier);
    }
}
