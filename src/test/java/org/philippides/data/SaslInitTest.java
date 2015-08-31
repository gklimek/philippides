package org.philippides.data;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class SaslInitTest {
    
    @Before
    public void setUp() {
        Register.registerEncodings();
    }

    @Test
    public void testSaslInit() throws IOException {
        ByteArrayInputStream is = new ByteArrayInputStream(new byte[] {0,83,65,(byte)192,12,1,(byte)163,9,65,78,79,78,89,77,79,85,83});
        IValue value = ValueUtil.fromStream(is);
        assertEquals(SaslInit.class, value.getClass());

        assertEquals(-1, is.read());
    }
}
