package org.philippides.frame;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.philippides.util.Streams;
import org.philippides.util.Validation;

public class Writer {
    public Frame write(int channel, Type type, Content content) throws IOException {
        Validation.check(!Type.UNKNOWN.equals(type), () -> new IllegalArgumentException("can't set type to UNKNOWN"));

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        
        content.getPerformative().write(bytes);
        Streams.copyStream(content.getPayloadStream(), bytes);
        
        return Frame.fromBytes(new byte[0], bytes.toByteArray(), type, channel);
    }
}
