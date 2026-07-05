package com.arch.saga;

public interface SagaStep {
    void execute(String context) throws Exception;
    void compensate(String context);
}
