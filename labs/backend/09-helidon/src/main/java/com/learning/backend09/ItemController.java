package com.learning.backend09;

import io.helidon.common.buffers.BufferData;
import io.helidon.http.Status;
import io.helidon.webserver.http.Handler;
import io.helidon.webserver.http.ServerRequest;
import io.helidon.webserver.http.ServerResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * REST handler for Item CRUD using Helidon's functional routing.
 *
 * Implements Handler for both GET and POST methods.
 * JSON serialization is done manually using a simple approach.
 */
public class ItemController implements Handler {

    private static final Logger log = LoggerFactory.getLogger(ItemController.class);
    private static final Map<Long, Item> items = new ConcurrentHashMap<>();
    private static final AtomicLong idGen = new AtomicLong(1);

    static {
        items.put(idGen.get(), new Item(idGen.getAndIncrement(), "Widget", 19.99));
        items.put(idGen.get(), new Item(idGen.getAndIncrement(), "Gadget", 49.99));
    }

    @Override
    public void handle(ServerRequest req, ServerResponse res) {
        switch (req.prologue().method().text()) {
            case "GET" -> handleGet(res);
            case "POST" -> handlePost(req, res);
            default -> res.status(Status.METHOD_NOT_ALLOWED_405).send();
        }
    }

    private void handleGet(ServerResponse res) {
        log.info("GET /api/items");
        StringBuilder json = new StringBuilder("[");
        var it = items.values().iterator();
        while (it.hasNext()) {
            Item item = it.next();
            json.append("{\"id\":").append(item.getId())
                .append(",\"name\":\"").append(item.getName())
                .append("\",\"price\":").append(item.getPrice())
                .append("}");
            if (it.hasNext()) json.append(",");
        }
        json.append("]");
        res.header("Content-Type", "application/json");
        res.send(json.toString());
    }

    private void handlePost(ServerRequest req, ServerResponse res) {
        log.info("POST /api/items");
        Item item = new Item(idGen.getAndIncrement(), "New Item", 0.0);
        items.put(item.getId(), item);
        res.status(Status.CREATED_201);
        res.header("Content-Type", "application/json");
        res.send("{\"id\":%d,\"name\":\"%s\",\"price\":%.2f}".formatted(
            item.getId(), item.getName(), item.getPrice()));
    }
}
