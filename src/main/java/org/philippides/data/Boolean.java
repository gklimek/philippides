package org.philippides.data;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Boolean extends Primitive {
    static final int DEFAULT_ENCODING = 0x56;
    static final int TRUE_ENCODING = 0x41;
    static final int FALSE_ENCODING = 0x42;

    public static final Boolean TRUE = new Boolean(true);
    public static final Boolean FALSE = new Boolean(false);

    private boolean value;

    public Boolean(boolean value) {
        this.value = value;
    }

    public Boolean(Boolean other) {
        this(other.value);
    }

    static void registerEncodings() {
        registerEncoding(DEFAULT_ENCODING, Boolean::fromStream);
        registerEncoding(TRUE_ENCODING, Boolean::fromStream);
        registerEncoding(FALSE_ENCODING, Boolean::fromStream);
    }

    public boolean booleanValue() {
        return value;
    }

    public static Boolean fromStream(InputStream is) throws IOException {
        int formatCode = is.read();
        switch (formatCode) {
        case DEFAULT_ENCODING:
            int read = is.read();
            return decode(read);
        case TRUE_ENCODING:
            return TRUE;
        case FALSE_ENCODING:
            return FALSE;
        default:
            throw new IllegalStateException("Expected Boolean's formatCode. Got: " + formatCode);
        }
    }

    private static Boolean decode(int byteValue) {
        if (0 == byteValue) {
            return FALSE;
        } else if (1 == byteValue) {
            return TRUE;
        } else {
            throw new IllegalStateException("Expected 0 or 1 for Boolean. Got: " + byteValue);
        }
    }
    
    @Override
    public void write(OutputStream os) throws IOException {
        os.write(value ? TRUE_ENCODING : FALSE_ENCODING);
    }

    @Override
    public java.lang.String toString() {
        return getClass().getSimpleName() + "[" + value + "]";
    }

    @Override
    public int hashCode() {
        return value ? 1 : 0;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Boolean other = (Boolean) obj;
        if (value != other.value) {
            return false;
        }
        return true;
    }
}
