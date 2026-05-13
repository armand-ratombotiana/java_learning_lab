package com.mlplatform.controller;

import com.mlplatform.model.Experiment;
import com.mlplatform.model.ModelVersion;
import com.mlplatform.service.MLPlatformService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/ml")
@RequiredArgsConstructor
public class MLController {

    private final MLPlatformService mlService;

    @PostMapping("/experiments")
    public ResponseEntity<Map<String, Object>> createExperiment(@RequestBody Map<String, Object> request) {
        @SuppressWarnings("unchecked")
        Map<String, String> hyperparameters = (Map<String, String>) request.get("hyperparameters");

        Experiment exp = mlService.createExperiment(
            (String) request.get("name"),
            (String) request.getOrDefault("description", ""),
            hyperparameters,
            (String) request.get("dataset")
        );

        return ResponseEntity.ok(Map.of(
            "experimentId", exp.getId(),
            "name", exp.getName(),
            "status", exp.getStatus()
        ));
    }

    @PostMapping("/experiments/{id}/start")
    public ResponseEntity<Map<String, Object>> startTraining(@PathVariable String id) {
        String runId = mlService.startTraining(id);
        return ResponseEntity.ok(Map.of("runId", runId, "status", "STARTED"));
    }

    @GetMapping("/experiments/{id}")
    public ResponseEntity<Experiment> getExperiment(@PathVariable String id) {
        return ResponseEntity.ok(mlService.getExperiment(id));
    }

    @GetMapping("/experiments")
    public ResponseEntity<List<Experiment>> listExperiments() {
        return ResponseEntity.ok(mlService.getAllExperiments());
    }

    @PostMapping("/models/register")
    public ResponseEntity<Map<String, Object>> registerModel(@RequestBody Map<String, Object> request) {
        @SuppressWarnings("unchecked")
        List<String> features = (List<String>) request.get("features");

        ModelVersion model = mlService.registerModel(
            (String) request.get("name"),
            (String) request.get("experimentId"),
            (String) request.getOrDefault("modelType", "classifier"),
            features
        );

        return ResponseEntity.ok(Map.of(
            "modelId", model.getId(),
            "name", model.getName(),
            "version", model.getVersion()
        ));
    }

    @PostMapping("/models/{id}/deploy")
    public ResponseEntity<Map<String, String>> deployModel(@PathVariable String id) {
        mlService.deployModel(id);
        return ResponseEntity.ok(Map.of("status", "DEPLOYED", "modelId", id));
    }

    @PostMapping("/models/{name}/predict")
    public ResponseEntity<Map<String, Object>> predict(
            @PathVariable String name,
            @RequestBody Map<String, Double> features) {
        return ResponseEntity.ok(mlService.predict(name, features));
    }
}