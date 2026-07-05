package com.arch.pipeline;

public interface Stage<I, O> {
    O process(I input);
}
