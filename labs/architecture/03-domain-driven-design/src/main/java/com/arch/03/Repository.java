package com.arch.ddd;

public interface Repository<T extends Aggregate> {
    void save(T aggregate);
    T findById(String id);
    void delete(String id);
}
