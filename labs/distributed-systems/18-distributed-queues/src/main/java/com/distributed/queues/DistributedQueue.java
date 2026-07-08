package com.distributed.queues;

import java.util.Optional;

public interface DistributedQueue<T> {
    String send(T message);
    Optional<T> receive(String consumerId);
    void acknowledge(String messageId);
    void nack(String messageId);
    int getSize();
}
