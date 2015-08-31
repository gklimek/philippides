package org.philippides;

import org.philippides.data.IValue;

public interface Sender {
    void send(IValue value, byte[] payload);
}