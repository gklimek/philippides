package org.philippides.data;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

import org.philippides.util.Bytes;

public class Binary extends Primitive {
    static final int VBIN8_ENCODING = 0xa0;
    static final int VBIN32_ENCODING = 0xb0;
    
    private byte[] value;

    public Binary(byte[] value) {
        this.value = value;
    }
    
    public Binary(Binary other) {
        this.value = other.value;
    }
    
    static void registerEncodings() {
        registerEncoding(VBIN8_ENCODING, Binary::fromStream);
        registerEncoding(VBIN32_ENCODING, Binary::fromStream);
    }
    
    public byte[] bytesValue() {
        return value;
    }
    
    public static Binary fromStream(InputStream is) throws IOException {
        int formatCode = is.read();
        long length;
        switch (formatCode) {
        case VBIN8_ENCODING:
            length = is.read();
            break;
        case VBIN32_ENCODING:
            length = Bytes.read4ByteInteger(is);
            break;
        default:
            throw new IllegalStateException("Expected Binary's formatCode. Got: " + formatCode);
        }
        byte[] buffer = new byte[(int) length];
        new DataInputStream(is).readFully(buffer);
        return new Binary(buffer);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (null == obj) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        if (obj instanceof Binary) {
            Binary r = (Binary) obj;
            return Arrays.equals(value, r.value);
        }
        return false;
    }

    @Override
    public void write(OutputStream os) throws IOException {
        if (value.length < 256) {
            os.write(VBIN8_ENCODING);
            os.write(value.length);
        } else {
            os.write(VBIN32_ENCODING);
            Bytes.write4ByteInteger(os, value.length);
        }
        os.write(value);
    }

    @Override
    public java.lang.String toString() {
        return getClass().getSimpleName() + "[" + value + "]";
    }
}
