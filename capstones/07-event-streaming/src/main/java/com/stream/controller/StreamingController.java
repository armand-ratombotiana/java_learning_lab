package com.stream.controller;

import com.stream.service.EventStreamingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/streaming")
@RequiredArgsConstructor
public class StreamingController {

    private final EventStreamingService streamingService;

    @PostMapping("/topics/{topic}")
    public ResponseEntity<Map<String, Object>> createTopic(
            @PathVariable String topic,
            @RequestBody Map<String, Object> request) {

        int partitions = ((Number) request.getOrDefault("partitions", 3)).intValue();
        short replicationFactor = ((Number) request.getOrDefault("replicationFactor", (short) 1)).shortValue();

        try {
            streamingService.createTopic(topic, partitions, replicationFactor);
            return ResponseEntity.ok(Map.of("topic", topic, "partitions", partitions, "status", "created"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/produce/{topic}")
    public ResponseEntity<Map<String, Object>> produce(
            @PathVariable String topic,
            @RequestBody Map<String, Object> request) {

        String key = (String) request.getOrDefault("key", "");
        String value = (String) request.get("value");

        if (value == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "value is required"));
        }

        long offset = streamingService.produce(topic, key, value);
        return ResponseEntity.ok(Map.of("topic", topic, "offset", offset, "success", offset >= 0));
    }

    @PostMapping("/consume/{topic}/subscribe")
    public ResponseEntity<Map<String, Object>> subscribe(
            @PathVariable String topic,
            @RequestBody Map<String, Object> request) {

        String groupId = (String) request.get("groupId");
        String consumerId = (String) request.get("consumerId");
        @SuppressWarnings("unchecked")
        List<String> topics = (List<String>) request.getOrDefault("topics", List.of(topic));

        streamingService.subscribe(groupId, consumerId, topics);
        return ResponseEntity.ok(Map.of("status", "subscribed", "groupId", groupId, "consumerId", consumerId));
    }

    @GetMapping("/consume/{topic}")
    public ResponseEntity<Map<String, Object>> consume(
            @PathVariable String topic,
            @RequestParam String groupId,
            @RequestParam String consumerId,
            @RequestParam(defaultValue = "0") int partition,
            @RequestParam(defaultValue = "0") long offset,
            @RequestParam(defaultValue = "10") int maxRecords) {

        var records = streamingService.consume(groupId, consumerId, topic, partition, offset, maxRecords);

        return ResponseEntity.ok(Map.of(
            "topic", topic,
            "partition", partition,
            "records", records.stream()
                .map(r -> Map.of(
                    "offset", r.offset(),
                    "key", r.key() != null ? new String(r.key()) : "",
                    "value", new String(r.value()),
                    "timestamp", r.timestamp()
                ))
                .toList()
        ));
    }

    @GetMapping("/topics/{topic}/metadata")
    public ResponseEntity<Map<String, Object>> getMetadata(@PathVariable String topic) {
        return ResponseEntity.ok(streamingService.getTopicMetadata(topic));
    }
}