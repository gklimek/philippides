package org.philippides.data;

import java.io.IOException;
import java.io.OutputStream;

public interface IValue {

    void write(OutputStream os) throws IOException;
}
