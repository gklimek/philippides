package org.philippides.data;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.philippides.util.Bytes;

public class Ushort extends Primitive {
    static final int DEFAULT_ENCODING = 0x60;
    
    private int value;

    public Ushort(int value) {
        this.value = value;
    }

    public Ushort(Ushort other) {
        this.value = other.value;
    }

    static void registerEncodings() {
        registerEncoding(DEFAULT_ENCODING, Uint::fromStream);
    }

    public int intValue() {
        return value;
    }

    public static Ushort fromStream(InputStream is) throws IOException {
        int formatCode = is.read();
        if (formatCode == DEFAULT_ENCODING) {
            return new Ushort(Bytes.read2ByteInteger(is));
        } else {
            throw new IllegalStateException("Expected Ushort's formatCode. Got: " + formatCode);
        }
    }
    
    @Override
    public void write(OutputStream os) throws IOException {
        os.write(DEFAULT_ENCODING);
        Bytes.write2ByteInteger(os, value);
    }

    @Override
    public java.lang.String toString() {
        return getClass().getSimpleName() + "[" + value + "]";
    }
}
