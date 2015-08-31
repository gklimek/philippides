package org.philippides.data;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.philippides.util.Bytes;
import org.philippides.util.Identity;

public class Symbol extends Primitive {
    static final int SYM8_ENCODING = 0xa3;
    static final int SYM32_ENCODING = 0xb3;
    
    private java.lang.String value;

    public Symbol(java.lang.String value) {
        this.value = value;
    }
    
    public Symbol(Symbol other) {
        this(other.value);
    }
    
    static void registerEncodings() {
        registerEncoding(SYM8_ENCODING, Symbol::fromStream);
        registerEncoding(SYM32_ENCODING, Symbol::fromStream);
    }
    
    public java.lang.String stringValue() {
        return value;
    }
    
    public static Symbol fromStream(InputStream is) throws IOException {
        int formatCode = is.read();
        switch (formatCode) {
        case SYM8_ENCODING:
            int len = Bytes.read1ByteInteger(is);
            byte[] bytes = Bytes.readBytes(is, len);
            return new Symbol(new java.lang.String(bytes,0,len));
        case SYM32_ENCODING:
            return null;
        default:
            throw new IllegalStateException("Expected Symbol's formatCode. Got: " + formatCode);
        }
    }
    
    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return Identity.isEqual(this, obj, (Symbol o) -> value.equals(o.value));
    }

    @Override
    public void write(OutputStream os) throws IOException {
        if (value.length() < Bytes.BYTE_CAPACITY) {
            os.write(SYM8_ENCODING);
            os.write(value.length());
            os.write(value.getBytes());
        } else {
            throw new IllegalStateException("SYM32_ENCODING not implemented yet");
        }
    }

    @Override
    public java.lang.String toString() {
        return getClass().getSimpleName() + "[" + value + "]";
    }
}
