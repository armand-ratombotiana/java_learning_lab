package com.networking.deep.lab06;

import java.util.*;
import java.util.concurrent.*;
import java.util.regex.*;
import java.util.stream.*;

public class NetworkSecurityDeep {

    public record Packet(String srcIp, String dstIp, int srcPort, int dstPort, String protocol, String payload) {}
    public record FirewallRule(String id, String srcCidr, String dstCidr, String protocol, int dstPort, String action) {}
    public record WafRule(String id, String pattern, String category, String action) {}
    public record IdsSignature(String id, String pattern, String category) {}

    public static class FirewallEngine {
        private final List<FirewallRule> rules = new CopyOnWriteArrayList<>();
        private final Set<String> connectionTable = ConcurrentHashMap.newKeySet();

        public void addRule(FirewallRule rule) { rules.add(rule); }

        public String evaluate(Packet packet) {
            var match = rules.stream()
                .filter(r -> r.dstPort() == packet.dstPort() || r.dstPort() == 0)
                .filter(r -> r.protocol().equals("*") || r.protocol().equals(packet.protocol()))
                .findFirst();
            var result = match.map(FirewallRule::action).orElse("DENY");
            if ("ALLOW".equals(result)) {
                connectionTable.add(packet.srcIp() + ":" + packet.srcPort() + "-" + packet.dstIp() + ":" + packet.dstPort());
            }
            return result;
        }
    }

    public static class WafEngine {
        private final List<WafRule> rules = new CopyOnWriteArrayList<>();

        public void addRule(WafRule rule) { rules.add(rule); }

        public Optional<WafRule> detect(String requestBody) {
            return rules.stream()
                .filter(r -> Pattern.compile(r.pattern(), Pattern.CASE_INSENSITIVE).matcher(requestBody).find())
                .findFirst();
        }
    }

    public static class IdsEngine {
        private final List<IdsSignature> signatures = new CopyOnWriteArrayList<>();

        public void addSignature(IdsSignature sig) { signatures.add(sig); }

        public List<IdsSignature> analyze(Packet packet) {
            return signatures.stream()
                .filter(s -> packet.payload() != null && Pattern.compile(s.pattern(), Pattern.CASE_INSENSITIVE).matcher(packet.payload()).find())
                .toList();
        }
    }

    public static class TokenBucketRateLimiter {
        private final long capacity;
        private final long refillRate;
        private final Map<String, Long> tokens = new ConcurrentHashMap<>();
        private final Map<String, Long> lastRefill = new ConcurrentHashMap<>();

        public TokenBucketRateLimiter(long capacity, long refillRate) {
            this.capacity = capacity;
            this.refillRate = refillRate;
        }

        public synchronized boolean allow(String key) {
            long now = System.nanoTime();
            long last = lastRefill.getOrDefault(key, now);
            long currentTokens = tokens.getOrDefault(key, capacity);
            long elapsed = now - last;
            long newTokens = Math.min(capacity, currentTokens + elapsed * refillRate / 1_000_000_000);

            if (newTokens > 0) {
                tokens.put(key, newTokens - 1);
                lastRefill.put(key, now);
                return true;
            }
            return false;
        }
    }

    public static void main(String[] args) {
        System.out.println("=== Firewall ===");
        var fw = new FirewallEngine();
        fw.addRule(new FirewallRule("allow-http", "0.0.0.0/0", "10.0.0.0/8", "TCP", 80, "ALLOW"));
        fw.addRule(new FirewallRule("allow-https", "0.0.0.0/0", "10.0.0.0/8", "TCP", 443, "ALLOW"));
        fw.addRule(new FirewallRule("deny-all", "0.0.0.0/0", "10.0.0.0/8", "*", 0, "DENY"));

        var webRequest = new Packet("1.2.3.4", "10.0.0.1", 54321, 80, "TCP", "GET /");
        var sshAttempt = new Packet("5.6.7.8", "10.0.0.1", 12345, 22, "TCP", "SSH login");
        System.out.println("HTTP: " + fw.evaluate(webRequest));
        System.out.println("SSH: " + fw.evaluate(sshAttempt));

        System.out.println("\n=== WAF ===");
        var waf = new WafEngine();
        waf.addRule(new WafRule("SQLI-001", "'.*OR.*=.*'", "SQL Injection", "BLOCK"));
        waf.addRule(new WafRule("XSS-001", "<script>.*</script>", "XSS", "BLOCK"));

        var sqlInjection = waf.detect("username=admin' OR '1'='1");
        sqlInjection.ifPresent(r -> System.out.println("Detected: " + r.id() + " (" + r.category() + ")"));
        var xss = waf.detect("comment=<script>alert(1)</script>");
        xss.ifPresent(r -> System.out.println("Detected: " + r.id() + " (" + r.category() + ")"));
        var clean = waf.detect("username=john&password=secret");
        System.out.println("Clean request: " + clean.isEmpty());

        System.out.println("\n=== IDS ===");
        var ids = new IdsEngine();
        ids.addSignature(new IdsSignature("ET-001", ".*/etc/passwd.*", "Path Traversal"));
        ids.addSignature(new IdsSignature("ET-002", ".*cmd=.*", "Command Injection"));

        var matches = ids.analyze(new Packet("1.2.3.4", "10.0.0.1", 80, 8080, "HTTP", "GET /etc/passwd"));
        matches.forEach(m -> System.out.println("Alert: " + m.id() + " " + m.category()));

        System.out.println("\n=== Rate Limiter ===");
        var limiter = new TokenBucketRateLimiter(5, 2);
        int allowed = 0;
        for (int i = 0; i < 10; i++) {
            if (limiter.allow("client-1")) allowed++;
        }
        System.out.println("Rate limited: " + allowed + "/10 allowed");
    }
}
