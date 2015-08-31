package org.philippides;

import org.philippides.frame.Header;

public class NotSuppportedProtocol extends Exception {
    private static final long serialVersionUID = 1L;

    private Header header;

    public NotSuppportedProtocol(Header header) {
        this.header = header;
    }

    @Override
    public String getMessage() {
        return "Protocol not supported. Header bytes: " + header;
    }
}
