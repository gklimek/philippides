package org.philippides.data;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Null extends Primitive {
    static final int DEFAULT_ENCODING = 0x40;
    
    public static final Null NULL = new Null();
    
    public Null() {
    }
    
    static void registerEncodings() {
        registerEncoding(DEFAULT_ENCODING, Null::fromStream);
    }
    
    public static Null fromStream(InputStream is) throws IOException {
        int formatCode = is.read();
        if (formatCode == DEFAULT_ENCODING) {
            return NULL;
        } else {
            throw new IllegalStateException("Expected Null's formatCode. Got: " + formatCode);
        }
    }
    
    @Override
    public int hashCode() {
        return 1;
    }

    @Override
    public boolean equals(Object obj) {
        if (null == obj) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        if (obj instanceof Null) {
            return true;
        }
        return false;
    }

    @Override
    public java.lang.String toString() {
        return "Null";
    }

    @Override
    public void write(OutputStream os) throws IOException {
        os.write(DEFAULT_ENCODING);
    }
}
