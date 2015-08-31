package org.philippides.data;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class ValueUtil {
    private ValueUtil() {
    }
    
    public static IValue fromStream(InputStream is) throws IOException {
        IValue result;

        is.mark(1);
        int b = is.read();
        is.reset();
        if (0 == b) {
            result = Described.fromStream(is);
        } else if (-1 != b){
            result = Primitive.fromStream(is);
        } else {
            result = null;
        }
        
        return result;
    }
    
    @SuppressWarnings("unchecked")
    public static <T extends IValue> T cast(IValue v, Class<T> clazz) {
        if (null == v) {
            return null;
        } else if (v instanceof Null) {
            return null;
        } else if (clazz.isAssignableFrom(v.getClass())) {
            return (T)v;
        } else {
            return constructFromValue(v, clazz);
        }
    }

    private static <T extends IValue> T constructFromValue(IValue v, Class<T> clazz) {
        if (IAddress.class.equals(clazz) && v instanceof String) {
            return (T)new AddressString((String)v);
        }
        try {
            Constructor<T> constructor = clazz.getConstructor(v.getClass());
            return constructor.newInstance(v);
        } catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException
                | IllegalArgumentException | InvocationTargetException e) {
            throw new IllegalStateException("Can't construct instance of class " + clazz + " from " + v, e);
        }
    }
}
