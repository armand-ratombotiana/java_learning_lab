# Weka Projects - Module 75

This module covers machine learning algorithms using Weka, including classification, regression, clustering, and model evaluation.

## Mini-Project: Classification and Clustering Pipeline (2-4 hours)

### Overview
Build a Java application using Weka to create a machine learning pipeline for classification and clustering. This project demonstrates data preprocessing, model training, evaluation, and prediction using Weka's algorithms.

### Project Structure
```
weka-mini/
├── pom.xml
├── src/main/java/com/learning/weka/
│   ├── MiniProjectApplication.java
│   ├── classifier/
│   │   └── ClassificationService.java
│   ├── clusterer/
│   │   └── ClusteringService.java
│   ├── evaluation/
│   │   └── ModelEvaluator.java
│   └── preprocessing/
│       └── DataPreprocessor.java
└── src/main/resources/
    └── data/
```

### Implementation

#### pom.xml
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <groupId>com.learning</groupId>
    <artifactId>weka-mini</artifactId>
    <version>1.0.0</version>
    <name>Weka Mini Project</name>
    
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.0</version>
    </parent>
    
    <properties>
        <java.version>21</java.version>
        <weka.version>3.8.6</weka.version>
    </properties>
    
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter</artifactId>
        </dependency>
        <dependency>
            <groupId>nz.ac.waikato.cms.weka</groupId>
            <artifactId>weka-dev</artifactId>
            <version>${weka.version}</version>
        </dependency>
    </dependencies>
</project>
```

#### Classification Service
```java
package com.learning.weka.classifier;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.trees.J48;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.classifiers.functions.Logistic;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.lazy.IBk;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.StringToNominal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.*;

@Service
public class ClassificationService {
    
    private static final Logger logger = LoggerFactory.getLogger(ClassificationService.class);
    
    private Classifier trainedModel;
    private Instances trainingData;
    
    public void loadTrainingData(String arffPath) throws Exception {
        DataSource source = new DataSource(arffPath);
        trainingData = source.getDataSet();
        
        if (trainingData.classIndex() == -1) {
            trainingData.setClassIndex(trainingData.numAttributes() - 1);
        }
        
        logger.info("Loaded {} instances with {} attributes", 
            trainingData.numInstances(), trainingData.numAttributes());
    }
    
    public void trainJ48() throws Exception {
        logger.info("Training J48 decision tree...");
        
        J48 tree = new J48();
        tree.setUnpruned(true);
        tree.buildClassifier(trainingData);
        
        trainedModel = tree;
        
        logger.info("J48 model trained successfully");
    }
    
    public void trainNaiveBayes() throws Exception {
        logger.info("Training Naive Bayes...");
        
        NaiveBayes nb = new NaiveBayes();
        nb.buildClassifier(trainingData);
        
        trainedModel = nb;
        
        logger.info("Naive Bayes model trained successfully");
    }
    
    public void trainIBk(int k) throws Exception {
        logger.info("Training IBk with k={}...", k);
        
        IBk knn = new IBk(k);
        knn.buildClassifier(trainingData);
        
        trainedModel = knn;
        
        logger.info("K-NN model trained successfully");
    }
    
    public void trainLogistic() throws Exception {
        logger.info("Training Logistic Regression...");
        
        Logistic lr = new Logistic();
        lr.buildClassifier(trainingData);
        
        trainedModel = lr;
        
        logger.info("Logistic Regression model trained successfully");
    }
    
    public Evaluation evaluateModel(Instances testData) throws Exception {
        if (trainedModel == null) {
            throw new IllegalStateException("No model trained yet");
        }
        
        Evaluation eval = new Evaluation(trainingData);
        eval.evaluateModel(trainedModel, testData);
        
        return eval;
    }
    
    public Evaluation crossValidate(int folds) throws Exception {
        if (trainedModel == null) {
            throw new IllegalStateException("No model trained yet");
        }
        
        Evaluation eval = new Evaluation(trainingData);
        eval.crossValidateModel(trainedModel, trainingData, folds, new Random(1));
        
        return eval;
    }
    
    public ClassificationResult classify(double[] attributes) throws Exception {
        if (trainedModel == null) {
            throw new IllegalStateException("No model trained yet");
        }
        
        weka.core.DenseInstance instance = new weka.core.DenseInstance(1.0, attributes);
        instance.setDataset(trainingData);
        
        double prediction = trainedModel.classifyInstance(instance);
        double[] distribution = trainedModel.distributionForInstance(instance);
        
        String predictedClass = trainingData.classAttribute().value((int) prediction);
        
        Map<String, Double> probabilities = new HashMap<>();
        for (int i = 0; i < distribution.length; i++) {
            probabilities.put(
                trainingData.classAttribute().value(i),
                distribution[i]
            );
        }
        
        return new ClassificationResult(predictedClass, prediction, probabilities);
    }
    
    public void saveModel(String path) throws Exception {
        if (trainedModel == null) {
            throw new IllegalStateException("No model trained yet");
        }
        
        weka.core.SerializationHelper.write(path, trainedModel);
        logger.info("Model saved to {}", path);
    }
    
    public void loadModel(String path) throws Exception {
        trainedModel = (Classifier) weka.core.SerializationHelper.read(path);
        logger.info("Model loaded from {}", path);
    }
    
    public String getModelSummary() {
        if (trainedModel == null) {
            return "No model loaded";
        }
        
        return trainedModel.toString();
    }
}

class ClassificationResult {
    private final String predictedClass;
    private final double rawPrediction;
    private final Map<String, Double> probabilities;
    
    public ClassificationResult(String predictedClass, double rawPrediction, 
                                Map<String, Double> probabilities) {
        this.predictedClass = predictedClass;
        this.rawPrediction = rawPrediction;
        this.probabilities = probabilities;
    }
    
    public String getPredictedClass() { return predictedClass; }
    public double getRawPrediction() { return rawPrediction; }
    public Map<String, Double> getProbabilities() { return probabilities; }
}
```

#### Clustering Service
```java
package com.learning.weka.clusterer;

import weka.clusterers.Clusterer;
import weka.clusterers.SimpleKMeans;
import weka.clusterers.EM;
import weka.clusterers.DBScan;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ClusteringService {
    
    private static final Logger logger = LoggerFactory.getLogger(ClusteringService.class);
    
    private Clusterer clusterer;
    private Instances data;
    private int numberOfClusters;
    
    public void loadData(String arffPath) throws Exception {
        DataSource source = new DataSource(arffPath);
        data = source.getDataSet();
        
        logger.info("Loaded {} instances for clustering", data.numInstances());
    }
    
    public void clusterWithKMeans(int numClusters) throws Exception {
        logger.info("Running K-Means with {} clusters...", numClusters);
        
        SimpleKMeans kMeans = new SimpleKMeans();
        kMeans.setNumClusters(numClusters);
        kMeans.setSeed(10);
        kMeans.buildClusterer(data);
        
        clusterer = kMeans;
        numberOfClusters = numClusters;
        
        logger.info("K-Means clustering complete");
    }
    
    public void clusterWithEM(int maxClusters) throws Exception {
        logger.info("Running EM clustering with max {} clusters...", maxClusters);
        
        EM em = new EM();
        em.setMaxClusters(maxClusters);
        em.buildClusterer(data);
        
        clusterer = em;
        numberOfClusters = em.numberOfClusters();
        
        logger.info("EM clustering complete with {} clusters", numberOfClusters);
    }
    
    public void clusterWithDBScan(double epsilon, int minPoints) throws Exception {
        logger.info("Running DBSCAN with epsilon={}, minPoints={}...", epsilon, minPoints);
        
        DBScan dbscan = new DBScan();
        dbscan.setEpsilon(epsilon);
        dbscan.setMinPoints(minPoints);
        dbscan.buildClusterer(data);
        
        clusterer = dbscan;
        
        numberOfClusters = clusterer.numberOfClusters();
        
        logger.info("DBSCAN clustering complete with {} clusters", numberOfClusters);
    }
    
    public List<ClusterAssignment> assignClusters() throws Exception {
        if (clusterer == null) {
            throw new IllegalStateException("No clustering performed yet");
        }
        
        List<ClusterAssignment> assignments = new ArrayList<>();
        
        for (int i = 0; i < data.numInstances(); i++) {
            int cluster = clusterer.clusterInstance(data.instance(i));
            double[] distribution = clusterer.distributionForInstance(data.instance(i));
            
            assignments.add(new ClusterAssignment(i, cluster, distribution));
        }
        
        return assignments;
    }
    
    public Map<Integer, List<Integer>> getClusterMembers() throws Exception {
        List<ClusterAssignment> assignments = assignClusters();
        
        Map<Integer, List<Integer>> members = new HashMap<>();
        
        for (ClusterAssignment assignment : assignments) {
            members.computeIfAbsent(assignment.getCluster(), k -> new ArrayList<>())
                .add(assignment.getInstanceIndex());
        }
        
        return members;
    }
    
    public Map<Integer, double[]> getClusterCentroids() throws Exception {
        if (!(clusterer instanceof SimpleKMeans)) {
            throw new IllegalStateException("Centroids only available for K-Means");
        }
        
        SimpleKMeans kMeans = (SimpleKMeans) clusterer;
        Instances centroids = kMeans.getClusterCentroids();
        
        Map<Integer, double[]> centroidMap = new HashMap<>();
        
        for (int i = 0; i < centroids.numInstances(); i++) {
            centroidMap.put(i, centroids.instance(i).toDoubleArray());
        }
        
        return centroidMap;
    }
    
    public double getClusteringQuality() throws Exception {
        if (clusterer == null) {
            throw new IllegalStateException("No clustering performed yet");
        }
        
        double sumSquaredError = 0;
        
        for (int i = 0; i < data.numInstances(); i++) {
            int cluster = clusterer.clusterInstance(data.instance(i));
            
            if (clusterer instanceof SimpleKMeans) {
                double[] centroid = getClusterCentroids().get(cluster);
                double[] instance = data.instance(i).toDoubleArray();
                
                double distance = 0;
                for (int j = 0; j < instance.length; j++) {
                    double diff = instance[j] - centroid[j];
                    distance += diff * diff;
                }
                sumSquaredError += distance;
            }
        }
        
        return sumSquaredError;
    }
    
    public String getClustererInfo() {
        if (clusterer == null) {
            return "No clusterer trained";
        }
        
        return clusterer.toString();
    }
}

class ClusterAssignment {
    private final int instanceIndex;
    private final int cluster;
    private final double[] distribution;
    
    public ClusterAssignment(int instanceIndex, int cluster, double[] distribution) {
        this.instanceIndex = instanceIndex;
        this.cluster = cluster;
        this.distribution = distribution;
    }
    
    public int getInstanceIndex() { return instanceIndex; }
    public int getCluster() { return cluster; }
    public double[] getDistribution() { return distribution; }
}
```

#### Model Evaluator
```java
package com.learning.weka.evaluation;

import weka.classifiers.Evaluation;
import weka.core.Instances;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class ModelEvaluator {
    
    private static final Logger logger = LoggerFactory.getLogger(ModelEvaluator.class);
    
    public EvaluationResult evaluate(Evaluation eval) {
        EvaluationResult result = new EvaluationResult();
        
        result.setAccuracy(eval.pctCorrect());
        result.setErrorRate(eval.pctIncorrect());
        result.setPrecision(eval.precision(1));
        result.setRecall(eval.recall(1));
        result.setF1Score(eval.fMeasure(1));
        
        result.setConfusionMatrix(eval.confusionMatrix());
        result.setTruePositives((int) eval.numTruePositives(1));
        result.setTrueNegatives((int) eval.numTrueNegatives(1));
        result.setFalsePositives((int) eval.numFalsePositives(1));
        result.setFalseNegatives((int) eval.numFalseNegatives(1));
        
        double[] classPrecisions = new double[eval.numClasses()];
        double[] classRecalls = new double[eval.numClasses()];
        double[] classFMeasures = new double[eval.numClasses()];
        
        for (int i = 0; i < eval.numClasses(); i++) {
            classPrecisions[i] = eval.precision(i);
            classRecalls[i] = eval.recall(i);
            classFMeasures[i] = eval.fMeasure(i);
        }
        
        result.setClassPrecisions(classPrecisions);
        result.setClassRecalls(classRecalls);
        result.setClassFMeasures(classFMeasures);
        
        result.setMeanAbsoluteError(eval.meanAbsoluteError());
        result.setRootMeanSquaredError(eval.rootMeanSquaredError());
        result.setRelativeAbsoluteError(eval.relativeAbsoluteError());
        result.setRootRelativeSquaredError(eval.rootRelativeSquaredError());
        
        logger.info("Evaluation complete - Accuracy: {:.2f}%", result.getAccuracy());
        
        return result;
    }
    
    public String generateReport(EvaluationResult result, String modelName) {
        StringBuilder report = new StringBuilder();
        
        report.append("=== Model Evaluation Report ===\n");
        report.append("Model: ").append(modelName).append("\n\n");
        
        report.append("--- Overall Metrics ---\n");
        report.append(String.format("Accuracy: %.2f%%\n", result.getAccuracy()));
        report.append(String.format("Error Rate: %.2f%%\n", result.getErrorRate()));
        report.append(String.format("Precision: %.4f\n", result.getPrecision()));
        report.append(String.format("Recall: %.4f\n", result.getRecall()));
        report.append(String.format("F1 Score: %.4f\n", result.getF1Score()));
        
        report.append("\n--- Error Metrics ---\n");
        report.append(String.format("Mean Absolute Error: %.4f\n", result.getMeanAbsoluteError()));
        report.append(String.format("Root Mean Squared Error: %.4f\n", 
            result.getRootMeanSquaredError()));
        
        return report.toString();
    }
}

class EvaluationResult {
    private double accuracy;
    private double errorRate;
    private double precision;
    private double recall;
    private double f1Score;
    private double[][] confusionMatrix;
    private int truePositives;
    private int trueNegatives;
    private int falsePositives;
    private int falseNegatives;
    private double[] classPrecisions;
    private double[] classRecalls;
    private double[] classFMeasures;
    private double meanAbsoluteError;
    private double rootMeanSquaredError;
    private double relativeAbsoluteError;
    private double rootRelativeSquaredError;
    
    public double getAccuracy() { return accuracy; }
    public void setAccuracy(double accuracy) { this.accuracy = accuracy; }
    public double getErrorRate() { return errorRate; }
    public void setErrorRate(double errorRate) { this.errorRate = errorRate; }
    public double getPrecision() { return precision; }
    public void setPrecision(double precision) { this.precision = precision; }
    public double getRecall() { return recall; }
    public void setRecall(double recall) { this.recall = recall; }
    public double getF1Score() { return f1Score; }
    public void setF1Score(double f1Score) { this.f1Score = f1Score; }
    public double[][] getConfusionMatrix() { return confusionMatrix; }
    public void setConfusionMatrix(double[][] confusionMatrix) { 
        this.confusionMatrix = confusionMatrix; 
    }
    public int getTruePositives() { return truePositives; }
    public void setTruePositives(int truePositives) { this.truePositives = truePositives; }
    public int getTrueNegatives() { return trueNegatives; }
    public void setTrueNegatives(int trueNegatives) { this.trueNegatives = trueNegatives; }
    public int getFalsePositives() { return falsePositives; }
    public void setFalsePositives(int falsePositives) { this.falsePositives = falsePositives; }
    public int getFalseNegatives() { return falseNegatives; }
    public void setFalseNegatives(int falseNegatives) { this.falseNegatives = falseNegatives; }
    public double[] getClassPrecisions() { return classPrecisions; }
    public void setClassPrecisions(double[] classPrecisions) { this.classPrecisions = classPrecisions; }
    public double[] getClassRecalls() { return classRecalls; }
    public void setClassRecalls(double[] classRecalls) { this.classRecalls = classRecalls; }
    public double[] getClassFMeasures() { return classFMeasures; }
    public void setClassFMeasures(double[] classFMeasures) { this.classFMeasures = classFMeasures; }
    public double getMeanAbsoluteError() { return meanAbsoluteError; }
    public void setMeanAbsoluteError(double meanAbsoluteError) { 
        this.meanAbsoluteError = meanAbsoluteError; 
    }
    public double getRootMeanSquaredError() { return rootMeanSquaredError; }
    public void setRootMeanSquaredError(double rootMeanSquaredError) { 
        this.rootMeanSquaredError = rootMeanSquaredError; 
    }
    public double getRelativeAbsoluteError() { return relativeAbsoluteError; }
    public void setRelativeAbsoluteError(double relativeAbsoluteError) { 
        this.relativeAbsoluteError = relativeAbsoluteError; 
    }
    public double getRootRelativeSquaredError() { return rootRelativeSquaredError; }
    public void setRootRelativeSquaredError(double rootRelativeSquaredError) { 
        this.rootRelativeSquaredError = rootRelativeSquaredError; 
    }
}
```

### Build and Run

```bash
# Prepare ARFF data file
# Create data/train.arff with Weka format

mvn clean compile

java -cp target/classes:$(find ~/.m2/repository -name "weka*.jar" | tr '\n' ':') \
  com.learning.weka.MiniProjectApplication
```

### Extension Ideas
1. Add feature selection
2. Implement ensemble methods
3. Add model comparison
4. Implement stacking
5. Add data visualization

---

## Real-World Project: Production ML Pipeline (8+ hours)

### Overview
Build a comprehensive production ML pipeline using Weka for automated model selection, hyperparameter tuning, feature engineering, and deployment. This system handles large datasets and provides REST API for predictions.

### Project Structure
```
production-ml-pipeline/
├── pom.xml
├── src/main/java/com/learning/weka/
│   ├── ProductionMlApplication.java
│   ├── pipeline/
│   │   ├── TrainingPipeline.java
│   │   └── PredictionPipeline.java
│   ├── service/
│   │   ├── ModelService.java
│   │   ├── FeatureEngineeringService.java
│   │   ├── ModelSelectionService.java
│   │   └── HyperparameterTuningService.java
│   ├── model/
│   │   ├── TrainedModel.java
│   │   └── PredictionResult.java
│   ├── controller/
│   │   └── MlController.java
│   └── dto/
│       └── TrainRequest.java
└── src/main/resources/
    └── models/
```

### Implementation

#### pom.xml
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <groupId>com.learning</groupId>
    <artifactId>production-ml-pipeline</artifactId>
    <version>1.0.0</version>
    <name>Production ML Pipeline</name>
    
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.0</version>
    </parent>
    
    <properties>
        <java.version>21</java.version>
        <weka.version>3.8.6</weka.version>
    </properties>
    
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>nz.ac.waikato.cms.weka</groupId>
            <artifactId>weka-dev</artifactId>
            <version>${weka.version}</version>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-mongodb</artifactId>
        </dependency>
    </dependencies>
</project>
```

#### Feature Engineering Service
```java
package com.learning.weka.service;

import weka.core.Instances;
import weka.core.Attribute;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Add;
import weka.filters.unsupervised.attribute.Remove;
import weka.filters.unsupervised.attribute.Normalize;
import weka.filters.unsupervised.attribute.Standardize;
import weka.filters.supervised.attribute.AttributeSelection;
import weka.filters.supervised.instance.Resample;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

@Service
public class FeatureEngineeringService {
    
    private static final Logger logger = LoggerFactory.getLogger(FeatureEngineeringService.class);
    
    public Instances normalize(Instances data) throws Exception {
        logger.info("Normalizing data...");
        
        Normalize normalize = new Normalize();
        normalize.setInputFormat(data);
        
        Instances normalized = Filter.useFilter(data, normalize);
        
        logger.info("Normalization complete");
        
        return normalized;
    }
    
    public Instances standardize(Instances data) throws Exception {
        logger.info("Standardizing data...");
        
        Standardize standardize = new Standardize();
        standardize.setInputFormat(data);
        
        Instances standardized = Filter.useFilter(data, standardize);
        
        logger.info("Standardization complete");
        
        return standardized;
    }
    
    public Instances selectFeatures(Instances data, int numFeatures) throws Exception {
        logger.info("Selecting top {} features...", numFeatures);
        
        String[] options = {
            "-E", "weka.attributeSelection.CorrelationAttributeEval",
            "-S", "weka.attributeSelection.Ranker",
            "-P", String.valueOf(numFeatures)
        };
        
        AttributeSelection filter = new AttributeSelection();
        filter.setOptions(options);
        filter.setInputFormat(data);
        
        Instances selected = Filter.useFilter(data, filter);
        
        logger.info("Feature selection complete, selected {} attributes", 
            selected.numAttributes());
        
        return selected;
    }
    
    public Instances removeAttributes(Instances data, int[] indices) throws Exception {
        logger.info("Removing {} attributes...", indices.length);
        
        Remove remove = new Remove();
        remove.setAttributeIndicesArray(indices);
        remove.setInputFormat(data);
        
        Instances filtered = Filter.useFilter(data, remove);
        
        return filtered;
    }
    
    public Instances resample(Instances data, double sampleSize, boolean biasToFirstClass) 
            throws Exception {
        
        Resample resample = new Resample();
        resample.setSampleSizePercent(sampleSize);
        resample.setBiasToUniformClass(biasToFirstClass);
        resample.setInputFormat(data);
        
        Instances resampled = Filter.useFilter(data, resample);
        
        logger.info("Resampled from {} to {} instances", 
            data.numInstances(), resampled.numInstances());
        
        return resampled;
    }
    
    public Instances handleMissingValues(Instances data) throws Exception {
        logger.info("Handling missing values...");
        
        for (int i = 0; i < data.numAttributes(); i++) {
            Attribute attr = data.attribute(i);
            
            if (attr.isNumeric()) {
                double sum = 0;
                int count = 0;
                
                for (int j = 0; j < data.numInstances(); j++) {
                    if (!data.instance(j).isMissing(i)) {
                        sum += data.instance(j).value(i);
                        count++;
                    }
                }
                
                double mean = count > 0 ? sum / count : 0;
                
                for (int j = 0; j < data.numInstances(); j++) {
                    if (data.instance(j).isMissing(i)) {
                        data.instance(j).setValue(i, mean);
                    }
                }
            } else if (attr.isNominal()) {
                String mostFrequent = findMostFrequentValue(data, i);
                
                for (int j = 0; j < data.numInstances(); j++) {
                    if (data.instance(j).isMissing(i)) {
                        data.instance(j).setValue(i, attr.indexOfValue(mostFrequent));
                    }
                }
            }
        }
        
        return data;
    }
    
    private String findMostFrequentValue(Instances data, int attrIndex) {
        Map<String, Integer> valueCounts = new HashMap<>();
        
        for (int i = 0; i < data.numInstances(); i++) {
            if (!data.instance(i).isMissing(attrIndex)) {
                String value = data.attribute(attrIndex)
                    .value((int) data.instance(i).value(attrIndex));
                valueCounts.merge(value, 1, Integer::sum);
            }
        }
        
        return valueCounts.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse("?");
    }
    
    public Instances createDerivedFeatures(Instances data, String[] formula) 
            throws Exception {
        
        Add add = new Add();
        add.setAttributeName("Derived_Feature");
        add.setInputFormat(data);
        
        Instances withDerived = Filter.useFilter(data, add);
        
        return withDerived;
    }
    
    public Map<String, Double> getFeatureCorrelations(Instances data, int targetIndex) {
        Map<String, Double> correlations = new HashMap<>();
        
        for (int i = 0; i < data.numAttributes(); i++) {
            if (i != targetIndex && data.attribute(i).isNumeric()) {
                double correlation = calculateCorrelation(data, i, targetIndex);
                correlations.put(data.attribute(i).name(), correlation);
            }
        }
        
        return correlations.entrySet().stream()
            .sorted(Map.Entry.<String, Double>comparingByValue()
                .reversed())
            .collect(LinkedHashMap::new,
                (m, e) -> m.put(e.getKey(), e.getValue()),
                Map::putAll);
    }
    
    private double calculateCorrelation(Instances data, int attr1Index, int attr2Index) {
        double sumX = 0, sumY = 0, sumXY = 0, sumX2 = 0, sumY2 = 0;
        int n = 0;
        
        for (int i = 0; i < data.numInstances(); i++) {
            if (!data.instance(i).isMissing(attr1Index) && 
                !data.instance(i).isMissing(attr2Index)) {
                
                double x = data.instance(i).value(attr1Index);
                double y = data.instance(i).value(attr2Index);
                
                sumX += x;
                sumY += y;
                sumXY += x * y;
                sumX2 += x * x;
                sumY2 += y * y;
                n++;
            }
        }
        
        if (n < 2) return 0;
        
        double numerator = n * sumXY - sumX * sumY;
        double denominator = Math.sqrt((n * sumX2 - sumX * sumX) * 
                                       (n * sumY2 - sumY * sumY));
        
        return denominator > 0 ? numerator / denominator : 0;
    }
}
```

#### Model Selection Service
```java
package com.learning.weka.service;

import weka.classifiers.Classifier;
import weka.classifiers.trees.J48;
import weka.classifiers.rules.DecisionTable;
import weka.classifiers.rules.Part;
import weka.classifiers.functions.Logistic;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.classifiers.functions.SMO;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.bayes.NaiveBayesUpdateable;
import weka.classifiers.lazy.IBk;
import weka.classifiers.meta.AdaBoost1;
import weka.classifiers.meta.Bagging;
import weka.classifiers.evaluation.Evaluation;
import weka.core.Instances;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ModelSelectionService {
    
    private static final Logger logger = LoggerFactory.getLogger(ModelSelectionService.class);
    
    private final Map<String, Classifier> models;
    
    public ModelSelectionService() {
        this.models = new HashMap<>();
        initializeModels();
    }
    
    private void initializeModels() {
        models.put("j48", new J48());
        models.put("naivebayes", new NaiveBayes());
        models.put("logistic", new Logistic());
        models.put("smo", new SMO());
        models.put("ibk-3", new IBk(3));
        models.put("ibk-5", new IBk(5));
        models.put("mlp", new MultilayerPerceptron());
        models.put("adaboost", new AdaBoost1());
        models.put("bagging", new Bagging());
        models.put("decisiontable", new DecisionTable());
    }
    
    public List<ModelComparisonResult> compareModels(Instances data, int folds) 
            throws Exception {
        
        List<ModelComparisonResult> results = new ArrayList<>();
        
        for (Map.Entry<String, Classifier> entry : models.entrySet()) {
            String name = entry.getKey();
            Classifier classifier = entry.getValue();
            
            logger.info("Evaluating model: {}", name);
            
            Evaluation eval = new Evaluation(data);
            eval.crossValidateModel(classifier, data, folds, new Random(42));
            
            ModelComparisonResult result = new ModelComparisonResult(
                name,
                classifier.getClass().getSimpleName(),
                eval.pctCorrect(),
                eval.precision(1),
                eval.recall(1),
                eval.fMeasure(1),
                eval.meanAbsoluteError(),
                eval.rootMeanSquaredError()
            );
            
            results.add(result);
            
            logger.info("{} - Accuracy: {:.2f}%", name, result.getAccuracy());
        }
        
        results.sort((a, b) -> Double.compare(b.getAccuracy(), a.getAccuracy()));
        
        return results;
    }
    
    public ModelComparisonResult selectBestModel(Instances data, int folds) 
            throws Exception {
        
        List<ModelComparisonResult> results = compareModels(data, folds);
        
        return results.get(0);
    }
    
    public Classifier getDefaultClassifier() {
        return new J48();
    }
    
    public Classifier getEnsembleClassifier() throws Exception {
        Bagging bagging = new Bagging();
        bagging.setClassifier(new J48());
        bagging.setNumIterations(10);
        
        return bagging;
    }
}

class ModelComparisonResult {
    private final String name;
    private final String className;
    private final double accuracy;
    private final double precision;
    private final double recall;
    private final double f1Score;
    private final double mae;
    private final double rmse;
    
    public ModelComparisonResult(String name, String className, double accuracy,
                                 double precision, double recall, double f1Score,
                                 double mae, double rmse) {
        this.name = name;
        this.className = className;
        this.accuracy = accuracy;
        this.precision = precision;
        this.recall = recall;
        this.f1Score = f1Score;
        this.mae = mae;
        this.rmse = rmse;
    }
    
    public String getName() { return name; }
    public String getClassName() { return className; }
    public double getAccuracy() { return accuracy; }
    public double getPrecision() { return precision; }
    public double getRecall() { return recall; }
    public double getF1Score() { return f1Score; }
    public double getMae() { return mae; }
    public double getRmse() { return rmse; }
}
```

#### Training Pipeline
```java
package com.learning.weka.pipeline;

import com.learning.weka.service.*;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.*;

@Service
public class TrainingPipeline {
    
    private static final Logger logger = LoggerFactory.getLogger(TrainingPipeline.class);
    
    private final FeatureEngineeringService featureService;
    private final ModelSelectionService modelService;
    
    public TrainingPipeline(FeatureEngineeringService featureService,
                           ModelSelectionService modelService) {
        this.featureService = featureService;
        this.modelService = modelService;
    }
    
    public TrainingResult train(String dataPath, String targetAttribute, 
                               PipelineConfig config) throws Exception {
        
        logger.info("Starting training pipeline...");
        
        logger.info("Step 1: Loading data from {}", dataPath);
        DataSource source = new DataSource(dataPath);
        Instances data = source.getDataSet();
        data.setClassIndex(data.attribute(targetAttribute) != null ? 
            data.attribute(targetAttribute).index() : data.numAttributes() - 1);
        
        logger.info("Loaded {} instances with {} attributes", 
            data.numInstances(), data.numAttributes());
        
        if (config.isHandleMissing()) {
            logger.info("Step 2: Handling missing values");
            data = featureService.handleMissingValues(data);
        }
        
        if (config.isNormalize()) {
            logger.info("Step 3: Normalizing data");
            data = featureService.normalize(data);
        }
        
        if (config.isStandardize()) {
            logger.info("Step 4: Standardizing data");
            data = featureService.standardize(data);
        }
        
        if (config.getFeatureSelection() > 0) {
            logger.info("Step 5: Selecting top {} features", config.getFeatureSelection());
            data = featureService.selectFeatures(data, config.getFeatureSelection());
        }
        
        logger.info("Step 6: Model selection and training");
        List<ModelComparisonResult> comparisons = modelService.compareModels(
            data, config.getCrossValidationFolds());
        
        Classifier bestModel = models.get(comparisons.get(0).getName());
        bestModel.buildClassifier(data);
        
        logger.info("Step 7: Final evaluation");
        Evaluation eval = new Evaluation(data);
        eval.crossValidateModel(bestModel, data, config.getCrossValidationFolds(), 
            new Random(42));
        
        String modelPath = "models/trained_" + System.currentTimeMillis() + ".model";
        weka.core.SerializationHelper.write(modelPath, bestModel);
        
        TrainingResult result = new TrainingResult(
            comparisons,
            comparisons.get(0),
            modelPath,
            eval
        );
        
        logger.info("Training pipeline complete!");
        
        return result;
    }
    
    private Map<String, Classifier> getModelInstances() {
        Map<String, Classifier> map = new HashMap<>();
        
        try {
            map.put("j48", new weka.classifiers.trees.J48());
            map.put("naivebayes", new weka.classifiers.bayes.NaiveBayes());
            map.put("logistic", new weka.classifiers.functions.Logistic());
            map.put("smo", new weka.classifiers.functions.SMO());
            map.put("ibk", new weka.classifiers.lazy.IBk(3));
        } catch (Exception e) {
            logger.error("Error initializing models", e);
        }
        
        return map;
    }
}

class PipelineConfig {
    private boolean handleMissing = true;
    private boolean normalize = false;
    private boolean standardize = true;
    private int featureSelection = 0;
    private int crossValidationFolds = 10;
    
    public boolean isHandleMissing() { return handleMissing; }
    public void setHandleMissing(boolean handleMissing) { 
        this.handleMissing = handleMissing; 
    }
    public boolean isNormalize() { return normalize; }
    public void setNormalize(boolean normalize) { this.normalize = normalize; }
    public boolean isStandardize() { return standardize; }
    public void setStandardize(boolean standardize) { this.standardize = standardize; }
    public int getFeatureSelection() { return featureSelection; }
    public void setFeatureSelection(int featureSelection) { 
        this.featureSelection = featureSelection; 
    }
    public int getCrossValidationFolds() { return crossValidationFolds; }
    public void setCrossValidationFolds(int crossValidationFolds) { 
        this.crossValidationFolds = crossValidationFolds; 
    }
}

class TrainingResult {
    private final List<ModelComparisonResult> comparisons;
    private final ModelComparisonResult bestModel;
    private final String modelPath;
    private final Evaluation evaluation;
    
    public TrainingResult(List<ModelComparisonResult> comparisons,
                         ModelComparisonResult bestModel,
                         String modelPath,
                         Evaluation evaluation) {
        this.comparisons = comparisons;
        this.bestModel = bestModel;
        this.modelPath = modelPath;
        this.evaluation = evaluation;
    }
    
    public List<ModelComparisonResult> getComparisons() { return comparisons; }
    public ModelComparisonResult getBestModel() { return bestModel; }
    public String getModelPath() { return modelPath; }
    public Evaluation getEvaluation() { return evaluation; }
}
```

#### ML Controller
```java
package com.learning.weka.controller;

import com.learning.weka.pipeline.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ml")
public class MlController {
    
    private final TrainingPipeline trainingPipeline;
    
    public MlController(TrainingPipeline trainingPipeline) {
        this.trainingPipeline = trainingPipeline;
    }
    
    @PostMapping("/train")
    public ResponseEntity<TrainingResult> train(
            @RequestParam("file") MultipartFile file,
            @RequestParam String targetAttribute,
            @RequestParam(defaultValue = "10") int folds) throws Exception {
        
        String tempPath = "/tmp/training_data.arff";
        file.transferTo(new java.io.File(tempPath));
        
        PipelineConfig config = new PipelineConfig();
        config.setCrossValidationFolds(folds);
        
        TrainingResult result = trainingPipeline.train(
            tempPath, targetAttribute, config);
        
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/models/compare")
    public ResponseEntity<List<ModelComparisonResult>> compareModels(
            @RequestParam String dataPath) throws Exception {
        
        DataSource source = new DataSource(dataPath);
        Instances data = source.getDataSet();
        
        return ResponseEntity.ok(null);
    }
}

record TrainRequest(String dataPath, String targetAttribute, PipelineConfig config) {}
```

### Build and Run

```bash
mvn clean package

java -jar target/production-ml-pipeline-1.0.0.jar

curl -X POST http://localhost:8080/api/ml/train \
  -F "file=@data.arff" \
  -d "targetAttribute=class"
```

### Extension Ideas
1. Add autoML capabilities
2. Implement model versioning
3. Add A/B testing
4. Implement real-time feature engineering
5. Add monitoring and drift detection