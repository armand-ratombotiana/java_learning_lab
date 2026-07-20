package com.capstone.mlplatform;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class TrainingPipeline {
    private final Map<String, PipelineRun> runs = new ConcurrentHashMap<>();
    private final AtomicLong runIdGen = new AtomicLong(0);

    public record PipelineRun(String runId, String pipelineName, String modelType,
                               Map<String, Object> parameters, PipelineStatus status,
                               Instant startedAt, Instant completedAt,
                               Map<String, Double> metrics) {
        public PipelineRun {
            parameters = parameters == null ? Map.of() : Map.copyOf(parameters);
            metrics = metrics == null ? Map.of() : Map.copyOf(metrics);
        }
    }

    public enum PipelineStatus { PENDING, RUNNING, COMPLETED, FAILED, CANCELLED }

    public record TrainingConfig(String pipelineName, String modelType,
                                  Map<String, Object> hyperparameters, double trainSplit) {
        public TrainingConfig {
            hyperparameters = hyperparameters == null ? Map.of() : Map.copyOf(hyperparameters);
        }
    }

    public PipelineRun startRun(TrainingConfig config) {
        String runId = "run-" + runIdGen.incrementAndGet();
        PipelineRun run = new PipelineRun(runId, config.pipelineName(), config.modelType(),
            config.hyperparameters(), PipelineStatus.RUNNING, Instant.now(), null, Map.of());
        runs.put(runId, run);
        return run;
    }

    public PipelineRun completeRun(String runId, Map<String, Double> metrics) {
        PipelineRun run = runs.get(runId);
        if (run == null) throw new IllegalArgumentException("Run not found: " + runId);
        PipelineRun completed = new PipelineRun(runId, run.pipelineName(), run.modelType(),
            run.parameters(), PipelineStatus.COMPLETED, run.startedAt(), Instant.now(), metrics);
        runs.put(runId, completed);
        return completed;
    }

    public PipelineRun failRun(String runId, Map<String, Double> partialMetrics) {
        PipelineRun run = runs.get(runId);
        if (run == null) throw new IllegalArgumentException("Run not found: " + runId);
        PipelineRun failed = new PipelineRun(runId, run.pipelineName(), run.modelType(),
            run.parameters(), PipelineStatus.FAILED, run.startedAt(), Instant.now(), partialMetrics);
        runs.put(runId, failed);
        return failed;
    }

    public Optional<PipelineRun> getRun(String runId) {
        return Optional.ofNullable(runs.get(runId));
    }

    public List<PipelineRun> getRunsByPipeline(String pipelineName) {
        return runs.values().stream()
            .filter(r -> r.pipelineName().equals(pipelineName))
            .sorted(Comparator.comparing(PipelineRun::startedAt).reversed())
            .toList();
    }

    public List<PipelineRun> getRecentRuns(int limit) {
        return runs.values().stream()
            .sorted(Comparator.comparing(PipelineRun::startedAt).reversed())
            .limit(limit)
            .toList();
    }

    public int runCount() { return runs.size(); }
    public void clear() { runs.clear(); runIdGen.set(0); }
}
