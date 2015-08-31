package org.philippides.data;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class ListTest {
    
    @Before
    public void setUp() {
        Register.registerEncodings();
    }

    @Test
    public void testList8() throws IOException {
        ByteArrayInputStream is = new ByteArrayInputStream(new byte[] {(byte)0xc0, 0x03, 0x01, 0x53, 0x01});
        IValue value = ValueUtil.fromStream(is);
        assertEquals(List.class, value.getClass());

        List listValue = (List) value;
        assertEquals(1, listValue.getList().size());

        assertEquals(-1, is.read());
    }
}
