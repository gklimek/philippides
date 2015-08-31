package org.philippides.util;

import static org.junit.Assert.*;

import org.junit.Test;

public class IdentityTest {
    class TestClass {
        private int value;

        public TestClass(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
        
        public boolean equals(Object obj) {
            return Identity.isEqual(this, obj, o -> value == o.value);
        }
    }

    private TestClass testValue11 = new TestClass(1);
    private TestClass testValue12 = new TestClass(1);
    private TestClass testValue21 = new TestClass(2);

    @Test
    public void testEqualsSameObject() {
        assertTrue(testValue11.equals(testValue11));
    }

    @Test
    public void testEqualsSameValue() {
        assertTrue(testValue11.equals(testValue12));
    }

    @Test
    public void testNotEqualsNull() {
        assertFalse(testValue11.equals(null));
    }

    @Test
    public void testNotEqualsOtherClass() {
        assertFalse(testValue11.equals("Other class"));
    }

    @Test
    public void testNotEqualsDifferentValue() {
        assertFalse(testValue11.equals(testValue21));
    }
}
