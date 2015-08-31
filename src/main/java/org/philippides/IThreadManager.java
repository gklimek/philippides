package org.philippides;

import org.philippides.endpoint.Link;

public interface IThreadManager {

    void startLinkSendingThread(Link link, Runnable runnable);

}