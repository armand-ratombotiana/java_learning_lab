package com.dataeng.seventeen;

import java.util.*;
import java.util.stream.*;

public class PointInTimeJoin {

    public record Label(String entityKey, long timestamp, Object value) {}
    public record Feature(String entityKey, long timestamp, String name, Object value) {}

    public static List<Map<String, Object>> execute(List<Label> labels, List<Feature> features) {
        Map<String, List<Feature>> featuresByEntity = features.stream()
            .collect(Collectors.groupingBy(Feature::entityKey));

        return labels.stream().map(label -> {
            List<Feature> entityFeatures = featuresByEntity.getOrDefault(label.entityKey(), List.of());
            Map<String, Object> result = new HashMap<>();
            result.put("entity_key", label.entityKey());
            result.put("label_timestamp", label.timestamp());
            result.put("label_value", label.value());

            Map<String, List<Feature>> featuresByName = entityFeatures.stream()
                .filter(f -> f.timestamp() <= label.timestamp())
                .collect(Collectors.groupingBy(Feature::name));

            for (var entry : featuresByName.entrySet()) {
                var latest = entry.getValue().stream()
                    .max(Comparator.comparingLong(Feature::timestamp));
                latest.ifPresent(f -> result.put(entry.getKey(), f.value()));
            }

            return result;
        }).collect(Collectors.toList());
    }

    public static void validateNoLeakage(List<Label> labels, List<Feature> features) {
        for (Label label : labels) {
            for (Feature feature : features) {
                if (feature.entityKey().equals(label.entityKey())
                    && feature.timestamp() > label.timestamp()) {
                    throw new IllegalStateException(
                        "Data leakage detected: feature " + feature.name()
                        + " for entity " + feature.entityKey()
                        + " has timestamp " + feature.timestamp()
                        + " > label timestamp " + label.timestamp());
                }
            }
        }
    }
}
