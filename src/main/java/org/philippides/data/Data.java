package org.philippides.data;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Data extends Described implements ISection {
    private Binary value;

    public Data(Binary value) {
        this.value = value;
    }

    static void registerEncodings() {
        registerEncoding(Ulong.parseUlong("0x00000000:0x00000075"), Data::fromStream);
    }

    public Binary getValue() {
        return value;
    }

    public static Data fromStream(InputStream is) throws IOException {
        Binary binary = Binary.fromStream(is);
        return new Data(binary);
    }

    @Override
    public void write(OutputStream os) throws IOException {
        os.write(0);
        Ulong.parseUlong("0x00000000:0x00000075").write(os);
        value.write(os);
    }

    @Override
    public java.lang.String toString() {
        return "Data [value=" + value + "]";
    }
}
