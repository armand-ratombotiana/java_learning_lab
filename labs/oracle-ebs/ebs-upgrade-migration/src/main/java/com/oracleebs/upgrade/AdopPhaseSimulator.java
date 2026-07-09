package com.oracleebs.upgrade;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class AdopPhaseSimulator {
    public enum AdopPhase { PREPARE, APPLY, FINALIZE, CUTOVER, CLEANUP }
    public enum AdopStatus { NOT_STARTED, IN_PROGRESS, COMPLETED, FAILED }

    public static class AdopCycle {
        private final String cycleId;
        private final Map<AdopPhase, AdopStatus> phaseStatus;
        private final Map<AdopPhase, Instant> phaseTimestamps;
        private final String patchName;
        private String edtionName;
        private String errorMessage;

        public AdopCycle(String cycleId, String patchName) {
            this.cycleId = cycleId;
            this.patchName = patchName;
            this.phaseStatus = new ConcurrentHashMap<>();
            this.phaseTimestamps = new ConcurrentHashMap<>();
            for (AdopPhase phase : AdopPhase.values()) {
                phaseStatus.put(phase, AdopStatus.NOT_STARTED);
            }
        }

        public String getCycleId() { return cycleId; }
        public String getPatchName() { return patchName; }
        public String getEditionName() { return edtionName; }
        public void setEditionName(String e) { edtionName = e; }
        public String getErrorMessage() { return errorMessage; }
        public void setErrorMessage(String m) { errorMessage = m; }

        public AdopStatus getPhaseStatus(AdopPhase phase) { return phaseStatus.get(phase); }
        public void setPhaseStatus(AdopPhase phase, AdopStatus status) {
            phaseStatus.put(phase, status);
            if (status == AdopStatus.IN_PROGRESS) {
                phaseTimestamps.put(phase, Instant.now());
            }
        }
        public Instant getPhaseTimestamp(AdopPhase phase) { return phaseTimestamps.get(phase); }
    }

    private final Map<String, AdopCycle> cycles;
    private static final List<AdopPhase> PHASE_ORDER = List.of(
        AdopPhase.PREPARE, AdopPhase.APPLY, AdopPhase.FINALIZE, AdopPhase.CUTOVER, AdopPhase.CLEANUP
    );

    public AdopPhaseSimulator() {
        this.cycles = new ConcurrentHashMap<>();
    }

    public AdopCycle startCycle(String cycleId, String patchName) {
        AdopCycle cycle = new AdopCycle(cycleId, patchName);
        cycle.setEditionName("EDITION_" + cycleId);
        cycles.put(cycleId, cycle);
        cycle.setPhaseStatus(AdopPhase.PREPARE, AdopStatus.IN_PROGRESS);
        return cycle;
    }

    public boolean advancePhase(String cycleId) {
        AdopCycle cycle = cycles.get(cycleId);
        if (cycle == null) return false;

        for (int i = 0; i < PHASE_ORDER.size(); i++) {
            AdopPhase current = PHASE_ORDER.get(i);
            if (cycle.getPhaseStatus(current) == AdopStatus.IN_PROGRESS) {
                cycle.setPhaseStatus(current, AdopStatus.COMPLETED);
                if (i + 1 < PHASE_ORDER.size()) {
                    cycle.setPhaseStatus(PHASE_ORDER.get(i + 1), AdopStatus.IN_PROGRESS);
                }
                return true;
            }
        }
        return false;
    }

    public boolean failPhase(String cycleId, String errorMessage) {
        AdopCycle cycle = cycles.get(cycleId);
        if (cycle == null) return false;

        for (AdopPhase phase : PHASE_ORDER) {
            if (cycle.getPhaseStatus(phase) == AdopStatus.IN_PROGRESS) {
                cycle.setPhaseStatus(phase, AdopStatus.FAILED);
                cycle.setErrorMessage(errorMessage);
                return true;
            }
        }
        return false;
    }

    public Optional<AdopCycle> getCycle(String cycleId) {
        return Optional.ofNullable(cycles.get(cycleId));
    }

    public AdopPhase getCurrentPhase(String cycleId) {
        AdopCycle cycle = cycles.get(cycleId);
        if (cycle == null) return null;
        return PHASE_ORDER.stream()
            .filter(p -> cycle.getPhaseStatus(p) == AdopStatus.IN_PROGRESS)
            .findFirst()
            .orElse(null);
    }

    public boolean isCycleComplete(String cycleId) {
        AdopCycle cycle = cycles.get(cycleId);
        if (cycle == null) return false;
        return cycle.getPhaseStatus(AdopPhase.CLEANUP) == AdopStatus.COMPLETED;
    }

    public static AdopPhaseSimulator createDefault() {
        AdopPhaseSimulator sim = new AdopPhaseSimulator();
        sim.startCycle("CYCLE_001", "Patch 35042176");
        return sim;
    }
}
