package org.philippides.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Bytes {
    public static final int BITS_PER_BYTE = 8;
    public static final int BYTE_CAPACITY = 1 << BITS_PER_BYTE;
    public static final int BYTES_PER_INT = 4;
    public static final int BYTES_PER_SHORT = 2;
    public static final int LAST_BYTE_MASK = 0xff;

    private Bytes() {
    }
    
    public static String toString(byte[] bytes) {
        return toString(bytes, 0, bytes.length);
    }
    
    public static String toString(byte[] bytes, int offset, int len) {
        return IntStream
                .range(offset, Integer.min(bytes.length, len))
                .mapToObj(i -> Integer.toString(Byte.toUnsignedInt(bytes[i])))
                .collect(Collectors.joining(","));
    }
    
    public static long toUnsignedInt(byte[] bytes, int offset) {
        long result = 0;
        for(int i=0; i<BYTES_PER_INT; i++) {
            result <<= BITS_PER_BYTE;
            result += bytes[offset + i] & LAST_BYTE_MASK;
        }
        return result;
    }

    public static int toUnsignedShort(byte[] bytes, int offset) {
        int result = 0;
        for(int i=0; i<BYTES_PER_SHORT; i++) {
            result <<= BITS_PER_BYTE;
            result += bytes[offset + i] & LAST_BYTE_MASK;
        }
        return result;
    }

    public static void fromUnsignedInt(byte[] bytes, int offset, long value) {
        long tmp = value;
        for(int i = BYTES_PER_INT - 1; i>=0; i--) {
            bytes[offset + i] = (byte) (tmp & LAST_BYTE_MASK);
            tmp >>>= BITS_PER_BYTE;
        }
    }
    
    public static int read1ByteInteger(InputStream is) throws IOException {
        int oneByte = is.read();
        Validation.check(-1 != oneByte, () -> new IOException("Premature EOF"));
        return oneByte;
    }
    
    public static int read2ByteInteger(InputStream is) throws IOException {
        int result = read1ByteInteger(is);
        result = (result << BITS_PER_BYTE) + read1ByteInteger(is);
        return result;
    }
    
    public static void write2ByteInteger(OutputStream os, int value) throws IOException {
        os.write(value >> BITS_PER_BYTE & LAST_BYTE_MASK);
        os.write(value & LAST_BYTE_MASK);
    }

    public static long read4ByteInteger(InputStream is) throws IOException {
        long result = read1ByteInteger(is);
        result = (result << BITS_PER_BYTE) + read1ByteInteger(is);
        result = (result << BITS_PER_BYTE) + read1ByteInteger(is);
        result = (result << BITS_PER_BYTE) + read1ByteInteger(is);
        return result;
    }
    
    public static void write4ByteInteger(OutputStream os, long value) throws IOException {
        os.write((int) (value >> (BITS_PER_BYTE + BITS_PER_BYTE + BITS_PER_BYTE) & LAST_BYTE_MASK));
        os.write((int) (value >> (BITS_PER_BYTE + BITS_PER_BYTE) & LAST_BYTE_MASK));
        os.write((int) (value >> BITS_PER_BYTE & LAST_BYTE_MASK));
        os.write((int) (value & LAST_BYTE_MASK));
    }

    public static byte[] readBytes(InputStream is, int len) throws IOException {
        byte[] buffer = new byte[len];
        int readSoFar = 0;
        while (readSoFar < len) {
            int read = is.read(buffer, readSoFar, len - readSoFar);
            Validation.check(-1 != read, () -> new IOException("Unexpected end of stream"));
            readSoFar += read;
        }
        return buffer;
    }

    public static long read8ByteLong(InputStream is) throws IOException {
        long hi = read4ByteInteger(is);
        long lo = read4ByteInteger(is);
        return hi << (BYTES_PER_INT * BITS_PER_BYTE) + lo;
    }

    public static void write8ByteLong(OutputStream os, long value) throws IOException {
        write4ByteInteger(os, value >> (BYTES_PER_INT * BITS_PER_BYTE));
        write4ByteInteger(os, value);
    }
}
