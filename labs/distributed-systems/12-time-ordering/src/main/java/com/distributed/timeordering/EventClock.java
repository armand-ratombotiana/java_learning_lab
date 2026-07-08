package com.distributed.timeordering;

import java.io.Serializable;

public interface EventClock<T extends Serializable> {
    T tick();
    T send();
    void receive(T other, long currentTimeMillis);
    T getValue();
}
