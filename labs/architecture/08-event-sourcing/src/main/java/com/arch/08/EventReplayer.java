package com.arch.eventsourcing;

import java.util.List;

public class EventReplayer {
    public <T extends EventStoreAggregate> T replay(T aggregate, List<Event> events) {
        aggregate.loadFromHistory(events);
        System.out.println("Replayed " + events.size() + " events for aggregate: " + aggregate.getId());
        return aggregate;
    }
}
