package org.philippides.data;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.philippides.util.Bytes;

public class Ulong extends Primitive {
    private static final java.lang.String HEX_PREFIX = "0x";
    private static final int HEX_PREFIX_LENGTH = HEX_PREFIX.length();
    private static final int HEX_RADIX = 16;

    static final int DEFAULT_ENCODING = 0x80;
    static final int SMALLULONG_ENCODING = 0x53;
    static final int ULONG0_ENCODING = 0x44;
    
    static final Ulong ZERO = new Ulong(0L);
    
    private long value;

    public Ulong(long value) {
        this.value = value;
    }
    
    public long longValue() {
        return value;
    }
    
    static void registerEncodings() {
        registerEncoding(DEFAULT_ENCODING, Ulong::fromStream);
        registerEncoding(SMALLULONG_ENCODING, Ulong::fromStream);
        registerEncoding(ULONG0_ENCODING, Ulong::fromStream);
    }

    public static Ulong fromStream(InputStream is) throws IOException {
        int formatCode = is.read();
        switch (formatCode) {
        case DEFAULT_ENCODING:
            throw new IllegalStateException("default 8 byte encoding not implemented yet");
        case SMALLULONG_ENCODING:
            return new Ulong(is.read());
        case ULONG0_ENCODING:
            return ZERO;
        default:
            throw new IllegalStateException("Expected ULong's formatCode. Got: " + formatCode);
        }
    }
    
    public static Ulong parseUlong(java.lang.String str) {
        if (str.startsWith(HEX_PREFIX)) {
            return new Ulong(Long.parseUnsignedLong(str.substring(HEX_PREFIX_LENGTH).replaceAll(":0x", ""), HEX_RADIX));
        }
        throw new IllegalArgumentException("Can't parse " + str + " as ULong");
    }

    @Override
    public int hashCode() {
        return (int)value;
    }

    @Override
    public boolean equals(Object obj) {
        if (null == obj) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        if (obj instanceof Ulong) {
            Ulong r = (Ulong) obj;
            return value == r.value;
        }
        return false;
    }

    @Override
    public void write(OutputStream os) throws IOException {
        if (0 == value) {
            os.write(ULONG0_ENCODING);
        } else if (value < Bytes.BYTE_CAPACITY){
            os.write(SMALLULONG_ENCODING);
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
