package com.arch.sidecar;

import java.util.function.Function;

public class ServiceAdapter<T, R> {
    private final Function<T, R> legacyImplementation;
    private final Function<T, R> newImplementation;
    private boolean useNewImpl = false;

    public ServiceAdapter(Function<T, R> legacy, Function<T, R> newImpl) {
        this.legacyImplementation = legacy;
        this.newImplementation = newImpl;
    }

    public R execute(T input) {
        return useNewImpl ? newImplementation.apply(input) : legacyImplementation.apply(input);
    }

    public void switchToNew() { this.useNewImpl = true; }
    public void switchToLegacy() { this.useNewImpl = false; }
    public boolean isUsingNew() { return useNewImpl; }
}
