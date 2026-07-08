package com.security19;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class ThreatDetectionEngine {

    private final Map<String, List<SecurityEvent>> eventWindow = new ConcurrentHashMap<>();
    private static final long WINDOW_SECONDS = 300;

    public List<Alert> processEvent(SecurityEvent event) {
        List<Alert> alerts = new ArrayList<>();
        String key = event.sourceIp + ":" + event.targetUser;
        eventWindow.computeIfAbsent(key, k -> new ArrayList<>()).add(event);
        List<SecurityEvent> recent = eventWindow.get(key).stream()
            .filter(e -> e.timestamp.isAfter(Instant.now().minusSeconds(WINDOW_SECONDS)))
            .collect(Collectors.toList());
        eventWindow.put(key, recent);
        if ("LOGIN_FAILURE".equals(event.eventType) && recent.size() > 5) {
            alerts.add(new Alert("BRUTE_FORCE", "HIGH",
                "Multiple login failures from " + event.sourceIp + " for user " + event.targetUser,
                Instant.now()));
        }
        if ("PRIVILEGE_ESCALATION".equals(event.eventType)) {
            alerts.add(new Alert("PRIV_ESCALATION", "CRITICAL",
                "Privilege escalation by user " + event.targetUser,
                Instant.now()));
        }
        return alerts;
    }

    public Alert enrichAlert(Alert alert) {
        alert.enriched = true;
        return alert;
    }

    public record SecurityEvent(
        String eventType, String sourceIp, String targetUser,
        Instant timestamp, String details
    ) {}

    public static class Alert {
        public final String ruleName, severity, message;
        public final Instant timestamp;
        public boolean enriched;
        public Alert(String ruleName, String severity, String message, Instant timestamp) {
            this.ruleName = ruleName; this.severity = severity;
            this.message = message; this.timestamp = timestamp;
        }
    }

    public static void main(String[] args) {
        ThreatDetectionEngine engine = new ThreatDetectionEngine();
        for (int i = 0; i < 6; i++) {
            var event = new SecurityEvent("LOGIN_FAILURE", "192.168.1.100", "admin",
                Instant.now(), "Invalid password attempt " + (i+1));
            var alerts = engine.processEvent(event);
            alerts.forEach(a -> System.out.println(a.severity + ": " + a.message));
        }
    }
}
