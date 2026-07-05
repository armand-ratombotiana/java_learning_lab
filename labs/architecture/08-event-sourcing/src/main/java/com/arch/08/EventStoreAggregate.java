package com.arch.eventsourcing;

import java.util.List;
import java.util.ArrayList;

public abstract class EventStoreAggregate {
    protected String id;
    private int version = 0;
    private final List<Event> changes = new ArrayList<>();

    public String getId() { return id; }
    public int getVersion() { return version; }

    protected abstract void apply(Event event);

    protected void applyChange(Event event) {
        apply(event);
        changes.add(event);
    }

    public List<Event> getUncommittedChanges() {
        return new ArrayList<>(changes);
    }

    public void markChangesCommitted() {
        changes.clear();
    }

    public void loadFromHistory(List<Event> history) {
        for (Event event : history) {
            apply(event);
            version++;
        }
    }
}
