package org.philippides.data;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class UlongTest {
    
    @Before
    public void setUp() {
        Register.registerEncodings();
    }

    @Test
    public void testULongSmall() throws IOException {
        ByteArrayInputStream is = new ByteArrayInputStream(new byte[] {0x53, 0x10});
        IValue value = ValueUtil.fromStream(is);
        assertEquals(Ulong.class, value.getClass());

        Ulong uLongValue = (Ulong) value;
        assertEquals(16, uLongValue.longValue());

        assertEquals(-1, is.read());
    }

    @Test
    public void testULongParse() {
        Ulong uLong = Ulong.parseUlong("0x00000000:0x00000041");
        assertEquals(65, uLong.longValue());
    }
}
