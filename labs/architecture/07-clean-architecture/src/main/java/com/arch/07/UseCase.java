package com.arch.clean;

public interface UseCase<I, O> {
    O execute(I input);
}
