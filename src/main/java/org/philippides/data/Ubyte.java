package org.philippides.data;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Ubyte extends Primitive {
    static final int DEFAULT_ENCODING = 0x50;
    public static final int MAX_VALUE = 255;

    private int value;

    public Ubyte(int value) {
        this.value = value;
    }

    public Ubyte(Ubyte other) {
        this(other.value);
    }

    static void registerEncodings() {
        registerEncoding(DEFAULT_ENCODING, Ubyte::fromStream);
    }

    public int intValue() {
        return value;
    }

    public static Ubyte fromStream(InputStream is) throws IOException {
        int formatCode = is.read();
        if (formatCode == DEFAULT_ENCODING) {
            return new Ubyte(is.read());
        } else {
            throw new IllegalStateException("Expected Ubyte's formatCode. Got: " + formatCode);
        }
    }
    
    @Override
    public void write(OutputStream os) throws IOException {
        os.write(DEFAULT_ENCODING);
        os.write(value);
    }

    @Override
    public java.lang.String toString() {
        return getClass().getSimpleName() + "[" + value + "]";
    }
}
