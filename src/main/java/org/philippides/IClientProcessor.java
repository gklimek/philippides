package org.philippides;

import java.io.InputStream;
import java.io.OutputStream;

public interface IClientProcessor {

    void process(InputStream inputStream, OutputStream outputStream);

}
