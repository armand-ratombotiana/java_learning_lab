package com.mlplatform.service;

import com.mlplatform.model.Experiment;
import com.mlplatform.model.ModelVersion;
import com.mlplatform.pipeline.TrainingPipeline;
import com.mlplatform.repository.ExperimentRepository;
import com.mlplatform.repository.ModelRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class MLPlatformService {

    private final ExperimentRepository experimentRepository;
    private final ModelRepository modelRepository;
    private final TrainingPipeline trainingPipeline;

    public Experiment createExperiment(String name, String description, Map<String, String> hyperparameters, String dataset) {
        Experiment experiment = new Experiment();
        experiment.setName(name);
        experiment.setDescription(description);
        experiment.setHyperparameters(hyperparameters);
        experiment.setTrainingDataset(dataset);
        experiment.setUserId("default");

        experiment = experimentRepository.save(experiment);
        log.info("Created experiment: {}", experiment.getId());
        return experiment;
    }

    public String startTraining(String experimentId) {
        Experiment experiment = experimentRepository.findById(experimentId)
            .orElseThrow(() -> new RuntimeException("Experiment not found: " + experimentId));

        return trainingPipeline.startTraining(
            experimentId,
            experiment.getHyperparameters(),
            experiment.getTrainingDataset()
        );
    }

    public Experiment getExperiment(String experimentId) {
        return experimentRepository.findById(experimentId)
            .orElseThrow(() -> new RuntimeException("Experiment not found: " + experimentId));
    }

    public List<Experiment> getAllExperiments() {
        return experimentRepository.findAll();
    }

    public ModelVersion registerModel(String name, String experimentId, String modelType, List<String> features) {
        List<ModelVersion> existing = modelRepository.findByName(name);

        ModelVersion model = new ModelVersion();
        model.setName(name);
        model.setVersion(existing.size() + 1);
        model.setExperimentId(experimentId);
        model.setModelType(modelType);
        model.setInputFeatures(features);
        model.setOutputType("classification");
        model.setArtifactUri("s3://models/" + name + "/v" + (existing.size() + 1) + ".pt");

        model = modelRepository.save(model);
        log.info("Registered model: {} version {}", name, model.getVersion());
        return model;
    }

    public Optional<ModelVersion> getLatestModel(String name) {
        return modelRepository.findByName(name).stream()
            .filter(m -> "DEPLOYED".equals(m.getStatus()))
            .findFirst();
    }

    public void deployModel(String modelId) {
        ModelVersion model = modelRepository.findById(modelId)
            .orElseThrow(() -> new RuntimeException("Model not found: " + modelId));

        model.deploy();
        modelRepository.save(model);
        log.info("Deployed model: {}", modelId);
    }

    public Map<String, Object> predict(String modelName, Map<String, Double> features) {
        ModelVersion model = getLatestModel(modelName)
            .orElseThrow(() -> new RuntimeException("Model not found: " + modelName));

        double prediction = Math.random();
        model.setPredictionCount(model.getPredictionCount() + 1);
        modelRepository.save(model);

        return Map.of(
            "model", modelName,
            "version", model.getVersion(),
            "prediction", prediction > 0.5 ? "POSITIVE" : "NEGATIVE",
            "confidence", prediction
        );
    }

    public record PredictionResult(String model, int version, String prediction, double confidence) {}
}