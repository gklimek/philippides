package org.philippides.data;

import java.io.IOException;
import java.io.InputStream;

import org.philippides.util.Validation;

public abstract class Primitive implements IValue {
    interface PrimitiveDecoder {
        Primitive fromStream(InputStream is) throws IOException;
    }
    
    private static final PrimitiveDecoder[] DECODERS = new PrimitiveDecoder[256];
    
    static void registerEncoding(int formatCode, PrimitiveDecoder decoder) {
        Validation.check(formatCode >=0 && formatCode < 256,
                () -> new IllegalArgumentException("Invalid format code: " + formatCode));
        DECODERS[formatCode] = decoder;
    }
    
    public static Primitive fromStream(InputStream is) throws IOException {
        is.mark(1);
        int formatCode = is.read();
        is.reset();
        
        PrimitiveDecoder decoder = DECODERS[formatCode];
        Validation.check(null != decoder, () -> new IllegalStateException("Format code not supported: " + formatCode));
        return decoder.fromStream(is);
    }
}
