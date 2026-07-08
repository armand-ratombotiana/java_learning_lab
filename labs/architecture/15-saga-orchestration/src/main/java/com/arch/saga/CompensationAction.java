package com.arch.saga;

public interface CompensationAction {
    void compensate(Object payload);
}
