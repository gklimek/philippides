package org.philippides.data;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.philippides.util.Bytes;

public class Uint extends Primitive {
    static final int DEFAULT_ENCODING = 0x70;
    static final int SMALLUINT_ENCODING = 0x52;
    static final int UINT0_ENCODING = 0x43;

    static final Uint ZERO = new Uint(0L);
    
    private long value;

    public Uint(long value) {
        this.value = value;
    }

    public Uint(Uint other) {
        this.value = other.value;
    }

    static void registerEncodings() {
        registerEncoding(DEFAULT_ENCODING, Uint::fromStream);
        registerEncoding(SMALLUINT_ENCODING, Uint::fromStream);
        registerEncoding(UINT0_ENCODING, Uint::fromStream);
    }

    public long longValue() {
        return value;
    }

    public static Uint fromStream(InputStream is) throws IOException {
        int formatCode = is.read();
        switch (formatCode) {
        case DEFAULT_ENCODING:
            return new Uint(Bytes.read4ByteInteger(is));
        case SMALLUINT_ENCODING:
            return new Uint(is.read());
        case UINT0_ENCODING:
            return ZERO;
        default:
            throw new IllegalStateException("Expected Uint's formatCode. Got: " + formatCode);
        }
    }

    @Override
    public void write(OutputStream os) throws IOException {
        if (0 == value) {
            os.write(UINT0_ENCODING);
        } else if (value < 256){
            os.write(SMALLUINT_ENCODING);
            os.write((int) value);
        } else {
            os.write(DEFAULT_ENCODING);
            Bytes.write4ByteInteger(os, value);
        }
    }

    @Override
    public java.lang.String toString() {
        return getClass().getSimpleName() + "[" + value + "]";
    }
}
