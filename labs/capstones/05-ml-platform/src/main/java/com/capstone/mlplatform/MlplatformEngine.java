package com.capstone.mlplatform;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;

public record MlplatformEngine(String engineId, String version, boolean active) {
    private static final Logger LOG = Logger.getLogger(MlplatformEngine.class.getName());
    private static final ConcurrentHashMap<String, Object> store = new ConcurrentHashMap<>();
    private static final AtomicLong opCounter = new AtomicLong(0);
    private static final AtomicLong errCounter = new AtomicLong(0);

    public MlplatformEngine {
        LOG.info("Init " + engineId + " v" + version);
        if (engineId == null || engineId.isBlank()) throw new IllegalArgumentException("ID required");
    }

    public String execute(String op, String payload) {
        opCounter.incrementAndGet();
        if (!active) { errCounter.incrementAndGet(); throw new IllegalStateException("Engine inactive"); }
        if (op == null || op.isBlank()) throw new IllegalArgumentException("Op required");
        var result = "[" + engineId + ":" + version + "] " + op + " -> " + (payload != null ? payload.toUpperCase() : "");
        store.put("op:" + opCounter.get(), result);
        return result;
    }

    public static long getOpCount() { return opCounter.get(); }
    public static long getErrCount() { return errCounter.get(); }
    public static void reset() { store.clear(); opCounter.set(0); errCounter.set(0); }
}