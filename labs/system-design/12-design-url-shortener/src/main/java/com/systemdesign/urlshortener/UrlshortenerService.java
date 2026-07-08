package com.systemdesign.urlshortener;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;

public record UrlshortenerService(String serviceId, int version, boolean enabled) {
    private static final Logger LOG = Logger.getLogger(UrlshortenerService.class.getName());
    private static final ConcurrentHashMap<String, Object> store = new ConcurrentHashMap<>();
    private static final AtomicLong requestCounter = new AtomicLong(0);

    public UrlshortenerService {
        LOG.info("Initializing " + serviceId + " version " + version);
    }

    public String process(String input) {
        long rid = requestCounter.incrementAndGet();
        if (!enabled) throw new IllegalStateException("Service " + serviceId + " is disabled");
        String result = "[" + serviceId + ":" + version + "] " + input;
        store.put("last:" + rid, result);
        return result;
    }

    public static long getRequestCount() { return requestCounter.get(); }
    public static void reset() { store.clear(); requestCounter.set(0); }
}