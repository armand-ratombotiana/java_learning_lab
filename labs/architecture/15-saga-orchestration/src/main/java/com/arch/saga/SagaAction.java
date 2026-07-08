package com.arch.saga;

public interface SagaAction {
    Object execute(Object payload);
}
