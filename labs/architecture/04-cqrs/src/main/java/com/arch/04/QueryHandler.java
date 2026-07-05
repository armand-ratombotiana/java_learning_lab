package com.arch.cqrs;

public interface QueryHandler<T extends Query, R> {
    R handle(T query) throws Exception;
}
