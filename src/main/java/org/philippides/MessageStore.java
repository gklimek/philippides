package org.philippides;

import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;

public class MessageStore implements IMessageStore {
    private final Map<Terminus, List<BlockingQueue<Message>>> terminusQueues = new ConcurrentHashMap<>();

    @Override
    public void put(Terminus terminus, Message message) {
        List<BlockingQueue<Message>> list = terminusQueues.get(terminus);
        if (null != list) {
            for(BlockingQueue<Message> queue : list) {
                queue.offer(message);
            }
        }
    }

    @Override
    public BlockingQueue<Message> subscribe(Terminus terminus) {
        List<BlockingQueue<Message>> queues = terminusQueues.computeIfAbsent(terminus, t -> new CopyOnWriteArrayList<>());
        BlockingQueue<Message> result = new LinkedBlockingQueue<>();
        queues.add(result);
        return result;
    }
}
