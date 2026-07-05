package com.arch.layered;

import java.util.UUID;

public class Entity {
    private final String id;
    private final String data;

    public Entity(String data) {
        this.id = UUID.randomUUID().toString().substring(0, 8);
        this.data = data;
    }

    public String getId() { return id; }
    public String getData() { return data; }
}
