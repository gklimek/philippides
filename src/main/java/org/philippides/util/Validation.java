package org.philippides.util;

import java.util.function.Supplier;

public class Validation {
    private Validation() {
    }
    
    public static <E extends Exception> void check(boolean condition, Supplier<E> exceptionSupplier) throws E {
        if (!condition) {
            throw exceptionSupplier.get();
        }
    }
}
