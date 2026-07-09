package com.oracleebs.manufacturing;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class WipSimulator {
    public enum WipJobStatus { RELEASED, IN_PROCESS, COMPLETED, CLOSED, SCRAPPED }

    public static class WipJob {
        private final String jobId;
        private final String assemblyItem;
        private final int plannedQuantity;
        private int completedQuantity;
        private int scrappedQuantity;
        private final String routingCode;
        private WipJobStatus status;
        private final LocalDate releaseDate;
        private final List<WipOperation> operations;

        public WipJob(String jobId, String assembly, int plannedQty, String routing, LocalDate release) {
            this.jobId = jobId;
            this.assemblyItem = assembly;
            this.plannedQuantity = plannedQty;
            this.completedQuantity = 0;
            this.scrappedQuantity = 0;
            this.routingCode = routing;
            this.status = WipJobStatus.RELEASED;
            this.releaseDate = release;
            this.operations = new ArrayList<>();
        }

        public String getJobId() { return jobId; }
        public String getAssemblyItem() { return assemblyItem; }
        public int getPlannedQuantity() { return plannedQuantity; }
        public int getCompletedQuantity() { return completedQuantity; }
        public int getScrappedQuantity() { return scrappedQuantity; }
        public String getRoutingCode() { return routingCode; }
        public WipJobStatus getStatus() { return status; }
        public LocalDate getReleaseDate() { return releaseDate; }
        public List<WipOperation> getOperations() { return Collections.unmodifiableList(operations); }

        public void addOperation(WipOperation op) { operations.add(op); }
        public void setStatus(WipJobStatus s) { status = s; }

        public void completeMove(int qty) { completedQuantity += qty; }
        public void scrap(int qty) { scrappedQuantity += qty; }
    }

    public static class WipOperation {
        private final int sequence;
        private final String department;
        private final String resourceCode;
        private final double setupHours;
        private final double runHoursPerUnit;

        public WipOperation(int seq, String dept, String resource, double setup, double run) {
            this.sequence = seq;
            this.department = dept;
            this.resourceCode = resource;
            this.setupHours = setup;
            this.runHoursPerUnit = run;
        }

        public int getSequence() { return sequence; }
        public String getDepartment() { return department; }
        public String getResourceCode() { return resourceCode; }
        public double getSetupHours() { return setupHours; }
        public double getRunHoursPerUnit() { return runHoursPerUnit; }

        public double totalHours(int qty) { return setupHours + runHoursPerUnit * qty; }
    }

    private final Map<String, WipJob> jobs;

    public WipSimulator() {
        this.jobs = new ConcurrentHashMap<>();
    }

    public WipJob createJob(String jobId, String assembly, int qty, String routing, LocalDate release) {
        WipJob job = new WipJob(jobId, assembly, qty, routing, release);
        jobs.put(jobId, job);
        return job;
    }

    public boolean completeOperation(String jobId, int completedQty) {
        WipJob job = jobs.get(jobId);
        if (job == null) return false;
        if (job.getStatus() != WipJobStatus.IN_PROCESS && job.getStatus() != WipJobStatus.RELEASED) return false;
        job.setStatus(WipJobStatus.IN_PROCESS);
        job.completeMove(completedQty);
        if (job.getCompletedQuantity() >= job.getPlannedQuantity()) {
            job.setStatus(WipJobStatus.COMPLETED);
        }
        return true;
    }

    public boolean scrapJob(String jobId, int scrapQty) {
        WipJob job = jobs.get(jobId);
        if (job == null) return false;
        job.scrap(scrapQty);
        if (job.getScrappedQuantity() >= job.getPlannedQuantity()) {
            job.setStatus(WipJobStatus.SCRAPPED);
        }
        return true;
    }

    public Optional<WipJob> getJob(String jobId) {
        return Optional.ofNullable(jobs.get(jobId));
    }

    public List<WipJob> getJobsByStatus(WipJobStatus status) {
        return jobs.values().stream().filter(j -> j.getStatus() == status).toList();
    }

    public static WipSimulator createDefault() {
        WipSimulator sim = new WipSimulator();
        var job = sim.createJob("JOB001", "FIN001", 100, "ROUTING_A", LocalDate.now());
        job.addOperation(new WipOperation(10, "ASSEMBLY", "LINE_1", 1.0, 0.5));
        job.addOperation(new WipOperation(20, "TEST", "QC_1", 0.5, 0.25));
        return sim;
    }
}
