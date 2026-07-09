package com.oracleebs.technical;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class ConcurrentManager {
    public enum ManagerStatus { ACTIVE, INACTIVE, PAUSED }

    public static class RequestWrapper {
        private final String programShortName;
        private final String phase;
        private final String status;
        private final Map<String, String> parameters;
        private String logFileName;
        private String outFileName;

        public RequestWrapper(String program, String phase, String status, Map<String, String> params) {
            this.programShortName = program;
            this.phase = phase;
            this.status = status;
            this.parameters = params;
        }

        public String getProgramShortName() { return programShortName; }
        public String getPhase() { return phase; }
        public String getStatus() { return status; }
        public Map<String, String> getParameters() { return parameters; }
        public String getLogFileName() { return logFileName; }
        public void setLogFileName(String f) { logFileName = f; }
        public String getOutFileName() { return outFileName; }
        public void setOutFileName(String f) { outFileName = f; }
    }

    private final String managerName;
    private ManagerStatus status;
    private final int sleepSeconds;
    private final List<RequestWrapper> running;
    private final List<RequestWrapper> completed;
    private final AtomicLong processedCount;

    public ConcurrentManager(String name, int sleep) {
        this.managerName = name;
        this.sleepSeconds = sleep;
        this.status = ManagerStatus.ACTIVE;
        this.running = new ArrayList<>();
        this.completed = new ArrayList<>();
        this.processedCount = new AtomicLong(0);
    }

    public boolean submit(RequestWrapper request) {
        if (status != ManagerStatus.ACTIVE) return false;
        running.add(request);
        return true;
    }

    public void processRequest(String programShortName) {
        running.stream()
            .filter(r -> r.getProgramShortName().equals(programShortName))
            .findFirst()
            .ifPresent(r -> {
                r.setLogFileName(programShortName + "_" + System.currentTimeMillis() + ".log");
                r.setOutFileName(programShortName + "_" + System.currentTimeMillis() + ".out");
                completed.add(r);
                running.remove(r);
                processedCount.incrementAndGet();
            });
    }

    public void setStatus(ManagerStatus s) { status = s; }
    public ManagerStatus getStatus() { return status; }
    public String getManagerName() { return managerName; }
    public int getSleepSeconds() { return sleepSeconds; }
    public long getProcessedCount() { return processedCount.get(); }
    public List<RequestWrapper> getRunning() { return Collections.unmodifiableList(running); }
    public List<RequestWrapper> getCompleted() { return Collections.unmodifiableList(completed); }

    public static ConcurrentManager createDefault() {
        ConcurrentManager mgr = new ConcurrentManager("STANDARD_MANAGER", 60);
        mgr.submit(new RequestWrapper("GLPOST", "P", "NORMAL", Map.of("period", "JAN-2026")));
        mgr.submit(new RequestWrapper("APINV", "P", "NORMAL", Map.of("batch", "B001")));
        return mgr;
    }
}
