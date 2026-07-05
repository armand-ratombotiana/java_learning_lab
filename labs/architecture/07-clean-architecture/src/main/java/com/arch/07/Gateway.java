package com.arch.clean;

public interface Gateway<T> {
    T findById(String id);
    void save(T entity);
}
