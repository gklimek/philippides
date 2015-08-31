package org.philippides;

import java.util.concurrent.BlockingQueue;

public interface IMessageStore {
    void put(Terminus terminus, Message message);
    BlockingQueue<Message> subscribe(Terminus terminus);
}
