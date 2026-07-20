package com.javalab.04;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

public class MainImplementation {
    
    private final Map<String, String> store = new ConcurrentHashMap<>();
    
    public Mono<String> requestResponse(String key) {
        return Mono.justOrEmpty(store.get(key)).defaultIfEmpty("Not found");
    }
    
    public Mono<Void> fireAndForget(String key, String value) {
        store.put(key, value);
        return Mono.empty();
    }
    
    public Flux<String> requestStream(String prefix) {
        return Flux.fromIterable(store.entrySet())
            .filter(e -> e.getKey().startsWith(prefix))
            .map(Map.Entry::getValue);
    }
    
    public Flux<String> channel(Flux<String> requests) {
        return requests.map(req -> "Processed: " + req);
    }
}
