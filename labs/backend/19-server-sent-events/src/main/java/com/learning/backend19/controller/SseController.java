package com.learning.backend19.controller;

import com.learning.backend19.model.StockPrice;
import com.learning.backend19.service.StockPriceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
@RequestMapping("/api/events")
public class SseController {

    private static final Logger log = LoggerFactory.getLogger(SseController.class);
    private final StockPriceService stockPriceService;

    public SseController(StockPriceService stockPriceService) {
        this.stockPriceService = stockPriceService;
    }

    @GetMapping(path = "/mvc-stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamMvc() {
        SseEmitter emitter = new SseEmitter(60_000L);
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            try {
                for (int i = 0; i < 10; i++) {
                    var stock = stockPriceService.generatePrice("AAPL");
                    emitter.send(SseEmitter.event()
                        .id(String.valueOf(i))
                        .name("stock-price")
                        .data(Map.of(
                            "symbol", stock.symbol(),
                            "price", stock.price(),
                            "timestamp", stock.timestamp().toString()
                        )));
                    Thread.sleep(1000);
                }
                emitter.complete();
            } catch (Exception e) {
                emitter.completeWithError(e);
            }
        });
        executor.shutdown();
        log.info("MVC SSE stream started");
        return emitter;
    }

    @GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<StockPrice>> streamReactive() {
        log.info("Reactive SSE stream started");
        return Flux.interval(Duration.ofSeconds(1))
            .map(i -> {
                var stock = stockPriceService.generatePrice("GOOGL");
                return ServerSentEvent.<StockPrice>builder()
                    .id(String.valueOf(i))
                    .event("stock-price")
                    .data(stock)
                    .comment("Live price feed")
                    .build();
            });
    }
}
