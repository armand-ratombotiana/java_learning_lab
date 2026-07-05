package com.arch.hexagonal;

public interface Port<T, R> {
    R execute(T input);
}
