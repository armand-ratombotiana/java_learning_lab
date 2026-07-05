package com.arch.pipeline;

import java.util.ArrayList;
import java.util.List;

public class Pipeline<I, O> {
    private final List<Stage<?, ?>> stages = new ArrayList<>();

    @SuppressWarnings("unchecked")
    public O execute(I input) {
        Object result = input;
        for (Stage<?, ?> stage : stages) {
            result = ((Stage<Object, Object>) stage).process(result);
        }
        return (O) result;
    }

    public Pipeline<I, O> addStage(Stage<?, ?> stage) {
        stages.add(stage);
        return this;
    }
}
