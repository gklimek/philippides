package org.philippides.data;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.philippides.util.Validation;

public abstract class Described implements IValue {
    interface DescribedDecoder {
        Described fromStream(InputStream is) throws IOException;
    }

    private static final Map<Primitive, DescribedDecoder> DECODERS = new HashMap<>();
    
    static void registerEncoding(Primitive descriptor, DescribedDecoder decoder) {
        DECODERS.put(descriptor, decoder);
    }
    
    public static Described fromStream(InputStream is) throws IOException {
        int zero = is.read();
        Validation.check(0 == zero, () -> new IllegalStateException("Expected 0 for described value"));
        
        Primitive descriptor = Primitive.fromStream(is);
        DescribedDecoder decoder = DECODERS.get(descriptor);
        Validation.check(null != decoder, () -> new IllegalStateException("Unknown descriptor: " + descriptor));
        return decoder.fromStream(is);
    }
}
