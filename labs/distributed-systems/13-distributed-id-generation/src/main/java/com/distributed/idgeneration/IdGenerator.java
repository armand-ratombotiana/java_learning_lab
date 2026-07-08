package com.distributed.idgeneration;

public interface IdGenerator<T> {
    T generate();
    long extractTimestamp(T id);
}
