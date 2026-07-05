package com.arch.actor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ActorSystem {
    private final Map<String, Actor> actors = new ConcurrentHashMap<>();
    private final ExecutorService executor = Executors.newCachedThreadPool();

    public void register(String name, Actor actor) {
        actors.put(name, actor);
        executor.submit(actor);
    }

    public void send(String actorName, Object message) {
        Actor actor = actors.get(actorName);
        if (actor != null) {
            actor.send(message);
        }
    }

    public void shutdown() {
        actors.values().forEach(Actor::stop);
        executor.shutdown();
    }
}
