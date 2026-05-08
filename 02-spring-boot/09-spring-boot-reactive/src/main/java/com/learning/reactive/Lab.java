package com.learning.reactive;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicLong;

@SpringBootApplication
public class Lab {
    public static void main(String[] args) {
        SpringApplication.run(Lab.class, args);
    }
}

@RestController
@RequestMapping("/api/reactive")
class ReactiveController {

    private final AtomicLong idGen = new AtomicLong(1);
    private final Sinks.Many<Event> eventSink = Sinks.many().multicast().onBackpressureBuffer();

    @GetMapping("/hello")
    public Mono<String> hello() {
        return Mono.just("Hello from WebFlux!")
            .delayElement(Duration.ofMillis(100));
    }

    @GetMapping("/users/{id}")
    public Mono<User> getUser(@PathVariable Long id) {
        return Mono.just(new User(id, "User" + id, "user" + id + "@example.com"));
    }

    @GetMapping("/users")
    public Flux<User> getUsers(@RequestParam(defaultValue = "5") int count) {
        return Flux.range(1, count)
            .delayElements(Duration.ofMillis(200))
            .map(i -> new User((long) i, "User" + i, "user" + i + "@example.com"));
    }

    @PostMapping("/events")
    public Mono<Event> createEvent(@RequestBody Event event) {
        event.setId(idGen.getAndIncrement());
        eventSink.tryEmitNext(event);
        return Mono.just(event);
    }

    @GetMapping(value = "/events/stream", produces = "text/event-stream")
    public Flux<Event> streamEvents() {
        return eventSink.asFlux();
    }
}

class User {
    private Long id;
    private String name;
    private String email;

    public User() {}
    public User(Long id, String name, String email) {
        this.id = id; this.name = name; this.email = email;
    }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}

class Event {
    private Long id;
    private String type;
    private String data;

    public Event() {}
    public Event(Long id, String type, String data) {
        this.id = id; this.type = type; this.data = data;
    }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getData() { return data; }
    public void setData(String data) { this.data = data; }
}
