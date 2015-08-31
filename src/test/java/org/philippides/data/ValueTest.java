package org.philippides.data;

import java.io.IOException;
import java.io.OutputStream;

import org.junit.Test;

import static org.junit.Assert.*;

public class ValueTest {

    class SpecificValue implements IValue {
        @Override
        public void write(OutputStream os) throws IOException {}
    }
    
    class AnotherSpecificValue implements IValue {
        @Override
        public void write(OutputStream os) throws IOException {}
    }
    
    @Test
    public void testCastNull() {
        assertNull(ValueUtil.cast(null, SpecificValue.class));
    }

    @Test
    public void testCastNullValue() {
        assertNull(ValueUtil.cast(Null.NULL, SpecificValue.class));
    }
    

    @Test
    public void testCastValue() {
        SpecificValue aValue = new SpecificValue();
        SpecificValue castedValue = ValueUtil.cast(aValue, SpecificValue.class);
        assertEquals(aValue, castedValue);
    }

    @Test(expected = IllegalStateException.class)
    public void testCastSomethingElse() {
        SpecificValue aValue = new SpecificValue();
        ValueUtil.cast(aValue, AnotherSpecificValue.class);
    }
}
