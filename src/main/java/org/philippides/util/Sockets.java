package org.philippides.util;

import java.io.Closeable;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Sockets {
    private static final Logger LOG = Logger.getLogger(Sockets.class.getName());

    private Sockets() {
    }

    public static void quietClose(Closeable closeable) {
        try {
            closeable.close();
        } catch (IOException e) {
            LOG.log(Level.INFO, "Exception while quiet close", e);
        }
    }
}
