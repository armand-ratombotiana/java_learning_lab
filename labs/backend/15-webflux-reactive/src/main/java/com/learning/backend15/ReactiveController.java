package com.learning.backend15;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Annotation-based reactive controller using @RestController with WebFlux.
 *
 * Methods return Flux<T> (multiple items) or Mono<T> (0-1 item).
 * Spring WebFlux handles the non-blocking streaming automatically.
 *
 * MediaType.TEXT_EVENT_STREAM enables Server-Sent Events (SSE) streaming.
 */
@RestController
@RequestMapping("/api/reactive")
public class ReactiveController {

    private static final Logger log = LoggerFactory.getLogger(ReactiveController.class);
    private final ReactiveService reactiveService;

    public ReactiveController(ReactiveService reactiveService) {
        this.reactiveService = reactiveService;
    }

    /**
     * Returns a single greeting as Mono.
     */
    @GetMapping("/hello")
    public Mono<String> hello() {
        log.info("Reactive hello endpoint");
        return Mono.just("Hello from WebFlux! Time: " + LocalDateTime.now());
    }

    /**
     * Streams a sequence of events every second (Server-Sent Events).
     */
    @GetMapping(value = "/clock", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> streamClock() {
        log.info("Starting clock stream");
        return Flux.interval(Duration.ofSeconds(1))
            .map(i -> "Tick " + (i + 1) + " at " + LocalDateTime.now());
    }

    /**
     * Returns an inventory summary combining multiple reactive sources.
     */
    @GetMapping("/inventory")
    public Mono<String> inventorySummary() {
        return reactiveService.getInventorySummary();
    }

    /**
     * Calls internal API via WebClient and returns combined result.
     */
    @GetMapping("/external")
    public Mono<String> callExternal() {
        return reactiveService.callExternalApi();
    }
}
