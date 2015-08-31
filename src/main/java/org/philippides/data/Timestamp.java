package org.philippides.data;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.philippides.util.Bytes;

public class Timestamp extends Primitive {
    static final int MS64_ENCODING = 0x83;
    
    private long value;

    public Timestamp(long value) {
        this.value = value;
    }

    public Timestamp(Timestamp other) {
        this.value = other.value;
    }

    static void registerEncodings() {
        registerEncoding(MS64_ENCODING, Timestamp::fromStream);
    }

    public long longValue() {
        return value;
    }

    public static Timestamp fromStream(InputStream is) throws IOException {
        int formatCode = is.read();
        if (formatCode == MS64_ENCODING) {
            return new Timestamp(Bytes.read8ByteLong(is));
        } else {
            throw new IllegalStateException("Expected Timestamp's formatCode. Got: " + formatCode);
        }
    }
    
    @Override
    public void write(OutputStream os) throws IOException {
        os.write(MS64_ENCODING);
        Bytes.write8ByteLong(os, value);
    }

    @Override
    public java.lang.String toString() {
        return getClass().getSimpleName() + "[" + value + "]";
    }
}
