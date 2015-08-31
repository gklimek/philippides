package org.philippides.endpoint;

import org.philippides.frame.Content;

public interface ChannelSender {
    void send(int remoteChannel, Content content);
}