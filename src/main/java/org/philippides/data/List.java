package org.philippides.data;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;

import org.philippides.util.Bytes;

public class List extends Primitive {
    static final int LIST0_ENCODING = 0x45;
    static final int LIST8_ENCODING = 0xc0;
    static final int LIST32_ENCODING = 0xd0;

    private static List EMPTY_LIST = new List(Collections.emptyList());

    private final java.util.List<IValue> values;
    
    public List(java.util.List<IValue> list) {
        this.values = new ArrayList<>(list);
    }
    
    static void registerEncodings() {
        registerEncoding(LIST0_ENCODING, List::fromStream);
        registerEncoding(LIST8_ENCODING, List::fromStream);
        registerEncoding(LIST32_ENCODING, List::fromStream);
    }
    
    public java.util.List<IValue> getList() {
        return Collections.unmodifiableList(values);
    }

    public static List fromStream(InputStream is) throws IOException {
        int formatCode = is.read();
        switch (formatCode) {
        case LIST0_ENCODING:
            return EMPTY_LIST;
        case LIST8_ENCODING:
            int size1 = is.read();
            int count1 = is.read();
            return readValues(is, count1);
        case LIST32_ENCODING:
            long size4 = Bytes.read4ByteInteger(is);
            long count4 = Bytes.read4ByteInteger(is);
            return readValues(is, count4);
        default:
            throw new IllegalStateException("Expected List's formatCode. Got: " + formatCode);
        }
    }

    private static List readValues(InputStream is, long count) throws IOException {
        java.util.List<IValue> tmp = new ArrayList<>((int)count);
        for(int i=0; i<count; i++) {
            tmp.add(ValueUtil.fromStream(is));
        }
        return new List(tmp);
    }

    @Override
    public void write(OutputStream os) throws IOException {
        ByteArrayOutputStream tmp = new ByteArrayOutputStream();
        for(IValue value : values) {
            if (null != value) {
                value.write(tmp);
            } else {
                Null.NULL.write(tmp);
            }
        }

        int size = tmp.size();
        int count = values.size();

        if (0 == count) {
            os.write(LIST0_ENCODING);
        } else if (size <= Ubyte.MAX_VALUE && count <= Ubyte.MAX_VALUE) {
            os.write(LIST8_ENCODING);
            os.write(size);
            os.write(count);
            tmp.writeTo(os);
        } else {
            os.write(LIST32_ENCODING);
            Bytes.write4ByteInteger(os, size);
            Bytes.write4ByteInteger(os, count);
            tmp.writeTo(os);
        }
    }
}
