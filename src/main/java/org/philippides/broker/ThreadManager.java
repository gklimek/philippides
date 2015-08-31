package org.philippides.broker;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.philippides.IThreadManager;
import org.philippides.endpoint.Link;

public class ThreadManager implements IThreadManager {
    private Map<Link, Thread> linkSendingThreads = new ConcurrentHashMap<>();
    
    @Override
    public void startLinkSendingThread(Link link, Runnable runnable) {
        Thread linkSendingThread = new Thread(runnable);
        if (null != linkSendingThreads.putIfAbsent(link, linkSendingThread)) {
            throw new IllegalStateException("Trying to create more than one sending thread for a link");
        }
        linkSendingThread.start();
    }
}
