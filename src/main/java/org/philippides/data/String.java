package org.philippides.data;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;

import org.philippides.util.Bytes;
import org.philippides.util.Identity;

public class String extends Primitive {
    static final Charset UTF_8 = Charset.forName("UTF-8");

    static final int STR8_UTF8 = 0xa1;
    static final int STR32_UTF8 = 0xb1;
    
    private java.lang.String value;

    public String(java.lang.String value) {
        this.value = value;
    }
    
    public String(String other) {
        this(other.value);
    }

    static void registerEncodings() {
        registerEncoding(STR8_UTF8, String::fromStream);
        registerEncoding(STR32_UTF8, String::fromStream);
    }
    
    public java.lang.String stringValue() {
        return value;
    }
    
    public static String fromStream(InputStream is) throws IOException {
        int formatCode = is.read();
        switch (formatCode) {
        case STR8_UTF8:
            int len = Bytes.read1ByteInteger(is);
            byte[] bytes = Bytes.readBytes(is, len);
            return new String(new java.lang.String(bytes, UTF_8));
        case STR32_UTF8:
            return null;
        default:
            throw new IllegalStateException("Expected String's formatCode. Got: " + formatCode);
        }
    }
    
    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return Identity.isEqual(this, obj, (String o) -> value.equals(o.value));
    }

    @Override
    public void write(OutputStream os) throws IOException {
        byte[] bytes = value.getBytes(UTF_8);
        if (bytes.length < 256) {
            os.write(STR8_UTF8);
            os.write(bytes.length);
            os.write(bytes);
        } else {
            os.write(STR32_UTF8);
            Bytes.write4ByteInteger(os, bytes.length);
            os.write(bytes);
        }
    }

    @Override
    public java.lang.String toString() {
        return "String [value=" + value + "]";
    }
}
