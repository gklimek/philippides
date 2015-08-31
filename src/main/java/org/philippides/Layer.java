package org.philippides;

import java.io.IOException;
import java.util.List;

import org.philippides.frame.Frame;

public interface Layer {

    boolean isDone();

    void onFrame(Frame inboundFrame) throws IOException;

    boolean expectFrame();

    List<Frame> toSend() throws IOException;

    boolean innerLayersExpected();
}
