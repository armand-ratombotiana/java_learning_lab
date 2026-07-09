package com.apex.apt;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ApexAdvancedWorksheet {
    public record CollectionEntry(String seq, String attr01, String attr02, String attr03, String attr04, String attr05) {}
    public record CacheEntry(String key, Object value, long ttl) {}
    public record MailMessage(String to, String subject, String body, String status) {}
    public record Plugin(String name, String type, String version, Map<String, String> attributes) {}

    private final Map<String, List<CollectionEntry>> collections = new ConcurrentHashMap<>();
    private final Map<String, CacheEntry> cache = new ConcurrentHashMap<>();
    private final List<MailMessage> mailQueue = new ArrayList<>();
    private final Map<String, Plugin> plugins = new ConcurrentHashMap<>();

    public void createCollection(String name) { collections.putIfAbsent(name, new ArrayList<>()); }

    public void addToCollection(String name, String attr01, String attr02, String attr03, String attr04, String attr05) {
        collections.computeIfAbsent(name, k -> new ArrayList<>())
            .add(new CollectionEntry(UUID.randomUUID().toString(), attr01, attr02, attr03, attr04, attr05));
    }

    public List<CollectionEntry> getCollection(String name) {
        return List.copyOf(collections.getOrDefault(name, List.of()));
    }

    public int getCollectionCount(String name) {
        return collections.getOrDefault(name, List.of()).size();
    }

    public void deleteCollection(String name) { collections.remove(name); }

    public void cachePut(String key, Object value, long ttlMs) {
        cache.put(key, new CacheEntry(key, value, ttlMs));
    }

    @SuppressWarnings("unchecked")
    public <T> T cacheGet(String key) {
        var entry = cache.get(key);
        if (entry == null) return null;
        return (T) entry.value();
    }

    public void cacheRemove(String key) { cache.remove(key); }
    public void cacheClear() { cache.clear(); }
    public int cacheSize() { return cache.size(); }

    public String sendMail(String to, String subject, String body) {
        var msg = new MailMessage(to, subject, body, "QUEUED");
        mailQueue.add(msg);
        return "Mail queued to " + to;
    }

    public List<MailMessage> getMailQueue() { return List.copyOf(mailQueue); }

    public void registerPlugin(Plugin p) { plugins.put(p.name(), p); }
    public Plugin getPlugin(String name) { return plugins.get(name); }

    public String generatePdf(String title, String content) {
        return "<pdf><title>" + title + "</title><content>" + content + "</content></pdf>";
    }

    public String createZipEntry(String name, String content) {
        return "ZIP:" + name + ":" + content.length() + " chars";
    }

    public static ApexAdvancedWorksheet createSample() {
        var ws = new ApexAdvancedWorksheet();
        ws.createCollection("EMP_CART");
        ws.addToCollection("EMP_CART", "100", "King", "24000", null, null);
        ws.addToCollection("EMP_CART", "101", "Kochhar", "17000", null, null);
        ws.cachePut("config", Map.of("theme", "dark", "lang", "en"), 3600000);
        ws.registerPlugin(new Plugin("Signature Pad", "ITEM", "1.0", Map.of("format", "PNG")));
        ws.registerPlugin(new Plugin("Star Rating", "ITEM", "2.1", Map.of("max", "5")));
        return ws;
    }
}