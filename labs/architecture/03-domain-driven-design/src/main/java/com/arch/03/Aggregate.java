package com.arch.ddd;

import java.util.ArrayList;
import java.util.List;

public abstract class Aggregate {
    protected String id;
    private final List<DomainEvent> domainEvents = new ArrayList<>();
    private int version = 0;

    public String getId() { return id; }
    public int getVersion() { return version; }

    protected void apply(DomainEvent event) {
        domainEvents.add(event);
        version++;
    }

    public List<DomainEvent> getDomainEvents() {
        return new ArrayList<>(domainEvents);
    }

    public void clearEvents() {
        domainEvents.clear();
    }
}
