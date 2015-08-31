package org.philippides.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.junit.Test;

import static org.junit.Assert.*;

public class BytesTest {
    @Test
    public void testToUnsignedInt() {
        long unsignedInt = Bytes.toUnsignedInt(new byte[] {0x70, (byte) 0x80, (byte) 0x90, (byte) 0xa0}, 0);
        assertEquals(0x708090a0, unsignedInt);
    }
    
    @Test
    public void testRead1ByteInteger() throws IOException {
        assertEquals(192, Bytes.read1ByteInteger(new ByteArrayInputStream(new byte[] {(byte) 0xc0})));
    }

    @Test
    public void testToUnsignedShort() {
        int unsignedShort = Bytes.toUnsignedShort(new byte[] {(byte) 0x80, (byte) 0x90}, 0);
        assertEquals(0x00008090, unsignedShort);
    }

    @Test
    public void testFromUnsignedInt() {
        byte[] bytes = new byte[4];
        Bytes.fromUnsignedInt(bytes, 0, 0xc1729364);
        assertArrayEquals(new byte[] {(byte) 0xc1, 0x72, (byte) 0x93, 0x64}, bytes);
    }

    @Test
    public void testWrite4ByteInteger() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Bytes.write4ByteInteger(baos, 0xc1729364);
        assertArrayEquals(new byte[] {(byte) 0xc1, 0x72, (byte) 0x93, 0x64}, baos.toByteArray());
    }
}
