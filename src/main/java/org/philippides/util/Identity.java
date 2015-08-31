package org.philippides.util;

import java.util.function.Predicate;


public class Identity {
    private Identity() {
    }
    
    public static <T> boolean isEqual(T thisObject, Object otherObject, Predicate<T> equalityPredicate) {
        if (null == otherObject) {
            return false;
        }
        if (thisObject == otherObject) {
            return true;
        }
        if (thisObject.getClass().isAssignableFrom(otherObject.getClass())) {
            return applyEqualityPredicate(otherObject, equalityPredicate);
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    private static <T> boolean applyEqualityPredicate(Object otherObject, Predicate<T> equalityPredicate) {
        return equalityPredicate.test((T) otherObject);
    }
}
