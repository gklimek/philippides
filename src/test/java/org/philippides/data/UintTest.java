package org.philippides.data;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class UintTest {
    
    @Before
    public void setUp() {
        Register.registerEncodings();
    }

    @Test
    public void testFromStreamDefaultEncoding() throws IOException {
        ByteArrayInputStream is = new ByteArrayInputStream(
                new byte[] {0x70, (byte) 0xa0, (byte) 0xb0, (byte) 0xc0, (byte) 0xd0});
        IValue value = ValueUtil.fromStream(is);
        assertEquals(Uint.class, value.getClass());

        Uint uintValue = (Uint) value;
        assertEquals(256L * 256 * 256 * 0xa0 + 256 * 256 * 0xb0 + 256 * 0xc0 + 0xd0,  uintValue.longValue());

        assertEquals(-1, is.read());
    }

    @Test
    public void testFromStreamSmalluintEncoding() throws IOException {
        ByteArrayInputStream is = new ByteArrayInputStream(new byte[] {0x52, (byte) 0xa0});
        IValue value = ValueUtil.fromStream(is);
        assertEquals(Uint.class, value.getClass());

        Uint uintValue = (Uint) value;
        assertEquals(0xa0,  uintValue.longValue());

        assertEquals(-1, is.read());
    }

    @Test
    public void testFromStreamUint0Encoding() throws IOException {
        ByteArrayInputStream is = new ByteArrayInputStream(new byte[] {0x43});
        IValue value = ValueUtil.fromStream(is);
        assertEquals(Uint.class, value.getClass());

        Uint uintValue = (Uint) value;
        assertEquals(0,  uintValue.longValue());

        assertEquals(-1, is.read());
    }
}
