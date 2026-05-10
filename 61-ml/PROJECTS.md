# Machine Learning Projects - Module 61

This module covers machine learning basics, Weka, and Apache Commons Math for data analysis.

## Mini-Project: Weka Classification Application (2-4 hours)

### Overview
Build a Java application that uses Weka for classification tasks, implementing data preprocessing, model training, and prediction.

### Project Structure
```
weka-demo/
├── src/main/java/com/learning/ml/
│   ├── WekaDemoApplication.java
│   ├── classifier/ProductClassifier.java
│   ├── preprocessing/DataPreprocessor.java
│   └── evaluation/ModelEvaluator.java
├── data/
│   └── training-data.csv
├── pom.xml
└── build.sh
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
    <artifactId>weka-demo</artifactId>
    <version>1.0.0</version>
    
    <properties>
        <java.version>17</java.version>
        <weka.version>3.8.6</weka.version>
    </properties>
    
    <dependencies>
        <dependency>
            <groupId>nz.ac.waikato.cms.weka</groupId>
            <artifactId>weka-stable</artifactId>
            <version>${weka.version}</version>
        </dependency>
        <dependency>
            <groupId>org.apache.commons</groupId>
            <artifactId>commons-math3</artifactId>
            <version>3.6.1</version>
        </dependency>
    </dependencies>
    
    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.11.0</version>
                <configuration>
                    <source>${java.version}</source>
                    <target>${java.version}</target>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
```

#### Java Implementation
```java
// ProductClassifier.java
package com.learning.ml.classifier;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.trees.J48;
import weka.core.Instances;
import weka.core.converters.ArffLoader;
import weka.core.converters.CSVLoader;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.StringToNominal;

import java.io.FileReader;
import java.io.Serializable;
import java.util.Random;

public class ProductClassifier implements Serializable {
    
    private Classifier classifier;
    private Instances trainingData;
    private String classAttribute;
    
    public ProductClassifier() {
        this.classifier = new J48();
    }
    
    public void train(String dataPath) throws Exception {
        CSVLoader loader = new CSVLoader();
        loader.setSource(new java.io.File(dataPath));
        Instances data = loader.getDataSet();
        
        if (data.classIndex() == -1) {
            data.setClassIndex(data.numAttributes() - 1);
        }
        
        this.trainingData = data;
        this.classifier.buildClassifier(data);
        
        System.out.println("Training completed. Dataset size: " + data.numInstances());
    }
    
    public void trainFromArff(String arffPath) throws Exception {
        FileReader reader = new FileReader(arffPath);
        Instances data = new Instances(reader);
        
        if (data.classIndex() == -1) {
            data.setClassIndex(data.numAttributes() - 1);
        }
        
        this.trainingData = data;
        this.classifier.buildClassifier(data);
    }
    
    public String predict(InstanceData instance) throws Exception {
        double[] values = instance.toDoubleArray(trainingData);
        
        weka.core.DenseInstance denseInstance = new weka.core.DenseInstance(1.0, values);
        denseInstance.setDataset(trainingData);
        
        double prediction = classifier.classifyInstance(denseInstance);
        return trainingData.classAttribute().value((int) prediction);
    }
    
    public double predictProbability(InstanceData instance, String classLabel) 
            throws Exception {
        
        double[] values = instance.toDoubleArray(trainingData);
        
        weka.core.DenseInstance denseInstance = new weka.core.DenseInstance(1.0, values);
        denseInstance.setDataset(trainingData);
        
        int classIndex = trainingData.classAttribute().indexOfValue(classLabel);
        double[] distribution = classifier.distributionForInstance(denseInstance);
        
        return distribution[classIndex];
    }
    
    public Evaluation evaluate(int folds) throws Exception {
        Evaluation evaluation = new Evaluation(trainingData);
        evaluation.crossValidateModel(classifier, trainingData, folds, new Random(42));
        return evaluation;
    }
    
    public String getModelSummary() throws Exception {
        Evaluation eval = evaluate(10);
        StringBuilder summary = new StringBuilder();
        summary.append("=== Model Evaluation ===\n");
        summary.append("Correct: ").append(eval.correct()).append("\n");
        summary.append("Incorrect: ").append(eval.incorrect()).append("\n");
        summary.append("Percent correct: ").append(String.format("%.2f%%", eval.pctCorrect())).append("\n");
        summary.append("Error rate: ").append(String.format("%.2f%%", eval.pctIncorrect())).append("\n");
        summary.append("\n=== Confusion Matrix ===\n");
        summary.append(eval.toMatrixString());
        summary.append("\n=== Detailed Accuracy ===\n");
        summary.append(eval.toClassDetailsString());
        
        return summary.toString();
    }
    
    public void saveModel(String modelPath) throws Exception {
        weka.core.SerializationHelper.write(modelPath, classifier);
    }
    
    public void loadModel(String modelPath) throws Exception {
        this.classifier = (Classifier) weka.core.SerializationHelper.read(modelPath);
    }
}

// InstanceData.java
package com.learning.ml.classifier;

import java.util.Map;

public class InstanceData {
    private Map<String, Object> attributes;
    
    public InstanceData(Map<String, Object> attributes) {
        this.attributes = attributes;
    }
    
    public double[] toDoubleArray(weka.core.Instances dataset) throws Exception {
        double[] values = new double[dataset.numAttributes()];
        
        for (int i = 0; i < dataset.numAttributes() - 1; i++) {
            String attrName = dataset.attribute(i).name();
            Object value = attributes.get(attrName);
            
            if (value == null) {
                values[i] = 0;
            } else if (value instanceof Number) {
                values[i] = ((Number) value).doubleValue();
            } else if (value instanceof String) {
                int index = dataset.attribute(i).indexOfValue((String) value);
                values[i] = index >= 0 ? index : 0;
            } else {
                values[i] = 0;
            }
        }
        
        return values;
    }
}

// DataPreprocessor.java
package com.learning.ml.preprocessing;

import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Normalize;
import weka.filters.unsupervised.attribute.Standardize;
import weka.filters.unsupervised.attribute.Discretize;
import weka.filters.unsupervised.attribute.Remove;

import java.util.List;

public class DataPreprocessor {
    
    public Instances normalize(Instances data) throws Exception {
        Normalize normalize = new Normalize();
        normalize.setInputFormat(data);
        return Filter.useFilter(data, normalize);
    }
    
    public Instances standardize(Instances data) throws Exception {
        Standardize standardize = new Standardize();
        standardize.setInputFormat(data);
        return Filter.useFilter(data, standardize);
    }
    
    public Instances discretize(Instances data, int bins) throws Exception {
        Discretize discretize = new Discretize();
        discretize.setOptions(new String[]{"-B", String.valueOf(bins)});
        discretize.setInputFormat(data);
        return Filter.useFilter(data, discretize);
    }
    
    public Instances removeAttributes(Instances data, int[] indices) throws Exception {
        Remove remove = new Remove();
        remove.setAttributeIndicesArray(indices);
        remove.setInputFormat(data);
        return Filter.useFilter(data, remove);
    }
    
    public Instances removeMissingValues(Instances data) {
        return new Instances(data);
    }
    
    public void handleMissingValues(Instances data) {
        for (int i = 0; i < data.numAttributes(); i++) {
            if (data.attribute(i).isNumeric()) {
                double mean = calculateMean(data, i);
                for (int j = 0; j < data.numInstances(); j++) {
                    if (data.instance(j).isMissing(i)) {
                        data.instance(j).setValue(i, mean);
                    }
                }
            }
        }
    }
    
    private double calculateMean(Instances data, int attributeIndex) {
        double sum = 0;
        int count = 0;
        
        for (int i = 0; i < data.numInstances(); i++) {
            if (!data.instance(i).isMissing(attributeIndex)) {
                sum += data.instance(i).value(attributeIndex);
                count++;
            }
        }
        
        return count > 0 ? sum / count : 0;
    }
}

// ModelEvaluator.java
package com.learning.ml.evaluation;

import weka.classifiers.Evaluation;
import weka.classifiers.Classifier;
import weka.core.Instances;

import java.util.HashMap;
import java.util.Map;

public class ModelEvaluator {
    
    public Map<String, Double> evaluate(Classifier classifier, Instances data) 
            throws Exception {
        
        Evaluation evaluation = new Evaluation(data);
        evaluation.crossValidateModel(classifier, data, 10, new java.util.Random(42));
        
        Map<String, Double> metrics = new HashMap<>();
        
        metrics.put("accuracy", evaluation.pctCorrect());
        metrics.put("errorRate", evaluation.pctIncorrect());
        metrics.put("precision", calculateMacroPrecision(evaluation));
        metrics.put("recall", calculateMacroRecall(evaluation));
        metrics.put("fMeasure", calculateMacroF1(evaluation));
        metrics.put("mae", evaluation.meanAbsoluteError());
        metrics.put("rmse", Math.sqrt(evaluation.rootMeanSquaredError()));
        
        return metrics;
    }
    
    private double calculateMacroPrecision(Evaluation evaluation) {
        return evaluation.precision(0);
    }
    
    private double calculateMacroRecall(Evaluation evaluation) {
        return evaluation.recall(0);
    }
    
    private double calculateMacroF1(Evaluation evaluation) {
        return evaluation.fMeasure(0);
    }
    
    public void printEvaluationSummary(Evaluation evaluation) {
        System.out.println("=== Evaluation Summary ===");
        System.out.println(evaluation.toSummaryString());
        System.out.println("\n=== Class Statistics ===");
        System.out.println(evaluation.toClassDetailsString());
        System.out.println("\n=== Confusion Matrix ===");
        System.out.println(evaluation.toMatrixString());
    }
}
```

### Build and Run
```bash
# Build
mvn clean compile

# Create training data
cat > data/training-data.csv << 'EOF'
price,category,rating,sales,success
high,electronics,4.5,1000,yes
medium,clothing,3.8,500,yes
low,electronics,2.5,200,no
high,clothing,4.2,800,yes
medium,electronics,3.5,600,yes
low,clothing,2.8,150,no
high,electronics,4.8,1200,yes
medium,clothing,3.2,400,yes
low,electronics,2.0,100,no
high,clothing,4.5,900,yes
EOF

# Run classification
java -cp target/classes com.learning.ml.WekaDemoApplication data/training-data.csv model.model
```

---

## Real-World Project: Predictive Maintenance System (8+ hours)

### Overview
Build a complete predictive maintenance system using Apache Commons Math for statistical analysis and Weka for machine learning to predict equipment failures.

### Project Structure
```
predictive-maintenance/
├── src/main/java/com/learning/maint/
│   ├── PredictiveMaintenanceApp.java
│   ├── service/DataCollector.java
│   ├── service/FeatureExtractor.java
│   ├── service/FailurePredictor.java
│   ├── model/SensorReading.java
│   ├── model/MaintenancePrediction.java
│   └── analysis/StatisticalAnalyzer.java
├── models/
├── data/
├── pom.xml
└── Dockerfile
```

### Implementation
```java
// pom.xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <groupId>com.learning</groupId>
    <artifactId>predictive-maintenance</artifactId>
    <version>1.0.0</version>
    
    <properties>
        <java.version>17</java.version>
    </properties>
    
    <dependencies>
        <dependency>
            <groupId>nz.ac.waikato.cms.weka</groupId>
            <artifactId>weka-stable</artifactId>
            <version>3.8.6</version>
        </dependency>
        <dependency>
            <groupId>org.apache.commons</groupId>
            <artifactId>commons-math3</artifactId>
            <version>3.6.1</version>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
            <version>3.2.0</version>
        </dependency>
    </dependencies>
</project>

// SensorReading.java
package com.learning.maint.model;

import java.time.LocalDateTime;

public class SensorReading {
    private String equipmentId;
    private LocalDateTime timestamp;
    private double temperature;
    private double vibration;
    private double pressure;
    private double rotationSpeed;
    private double powerConsumption;
    private boolean failure;
    
    public SensorReading(String equipmentId, LocalDateTime timestamp, 
                         double temperature, double vibration, double pressure,
                         double rotationSpeed, double powerConsumption, boolean failure) {
        this.equipmentId = equipmentId;
        this.timestamp = timestamp;
        this.temperature = temperature;
        this.vibration = vibration;
        this.pressure = pressure;
        this.rotationSpeed = rotationSpeed;
        this.powerConsumption = powerConsumption;
        this.failure = failure;
    }
    
    public String getEquipmentId() { return equipmentId; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public double getTemperature() { return temperature; }
    public double getVibration() { return vibration; }
    public double getPressure() { return pressure; }
    public double getRotationSpeed() { return rotationSpeed; }
    public double getPowerConsumption() { return powerConsumption; }
    public boolean isFailure() { return failure; }
}

// FeatureExtractor.java
package com.learning.maint.service;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import com.learning.maint.model.SensorReading;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

public class FeatureExtractor {
    
    public List<FeatureVector> extractFeatures(List<SensorReading> readings) {
        Map<String, List<SensorReading>> byEquipment = readings.stream()
            .collect(Collectors.groupingBy(SensorReading::getEquipmentId));
        
        List<FeatureVector> featureVectors = new ArrayList<>();
        
        for (Map.Entry<String, List<SensorReading>> entry : byEquipment.entrySet()) {
            List<SensorReading> sorted = entry.getValue().stream()
                .sorted(Comparator.comparing(SensorReading::getTimestamp))
                .toList();
            
            List<FeatureVector> vectors = createFeatureVectors(sorted);
            featureVectors.addAll(vectors);
        }
        
        return featureVectors;
    }
    
    private List<FeatureVector> createFeatureVectors(List<SensorReading> readings) {
        List<FeatureVector> vectors = new ArrayList<>();
        
        for (int i = 0; i < readings.size(); i++) {
            List<SensorReading> window = readings.subList(
                Math.max(0, i - 10), Math.min(readings.size(), i + 1));
            
            if (window.size() >= 5) {
                FeatureVector fv = calculateFeatures(window);
                fv.setLabel(readings.get(i).isFailure() ? "FAILURE" : "NORMAL");
                vectors.add(fv);
            }
        }
        
        return vectors;
    }
    
    private FeatureVector calculateFeatures(List<SensorReading> window) {
        FeatureVector fv = new FeatureVector();
        
        double[] temps = window.stream().mapToDouble(SensorReading::getTemperature).toArray();
        double[] vibes = window.stream().mapToDouble(SensorReading::getVibration).toArray();
        double[] pressures = window.stream().mapToDouble(SensorReading::getPressure).toArray();
        double[] speeds = window.stream().mapToDouble(SensorReading::getRotationSpeed).toArray();
        
        DescriptiveStatistics tempStats = new DescriptiveStatistics(temps);
        DescriptiveStatistics vibeStats = new DescriptiveStatistics(vibes);
        DescriptiveStatistics pressureStats = new DescriptiveStatistics(pressures);
        DescriptiveStatistics speedStats = new DescriptiveStatistics(speeds);
        
        fv.setMeanTemperature(tempStats.getMean());
        fv.setStdTemperature(tempStats.getStandardDeviation());
        fv.setMinTemperature(tempStats.getMin());
        fv.setMaxTemperature(tempStats.getMax());
        fv.setMeanVibration(vibeStats.getMean());
        fv.setStdVibration(vibeStats.getStandardDeviation());
        fv.setMeanPressure(pressureStats.getMean());
        fv.setStdPressure(pressureStats.getStandardDeviation());
        fv.setMeanSpeed(speedStats.getMean());
        fv.setStdSpeed(speedStats.getStandardDeviation());
        fv.setTrendTemperature(calculateTrend(temps));
        fv.setTrendVibration(calculateTrend(vibes));
        
        return fv;
    }
    
    private double calculateTrend(double[] values) {
        if (values.length < 2) return 0;
        
        double sumX = 0, sumY = 0, sumXY = 0, sumX2 = 0;
        int n = values.length;
        
        for (int i = 0; i < n; i++) {
            sumX += i;
            sumY += values[i];
            sumXY += i * values[i];
            sumX2 += i * i;
        }
        
        double slope = (n * sumXY - sumX * sumY) / (n * sumX2 - sumX * sumX);
        return slope;
    }
}

// FeatureVector.java
package com.learning.maint.service;

public class FeatureVector {
    private double meanTemperature, stdTemperature, minTemperature, maxTemperature;
    private double meanVibration, stdVibration;
    private double meanPressure, stdPressure;
    private double meanSpeed, stdSpeed;
    private double trendTemperature, trendVibration;
    private String label;
    
    public double[] toArray() {
        return new double[]{meanTemperature, stdTemperature, minTemperature, maxTemperature,
            meanVibration, stdVibration, meanPressure, stdPressure, meanSpeed, stdSpeed,
            trendTemperature, trendVibration};
    }
    
    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }
    public void setMeanTemperature(double v) { meanTemperature = v; }
    public void setStdTemperature(double v) { stdTemperature = v; }
    public void setMinTemperature(double v) { minTemperature = v; }
    public void setMaxTemperature(double v) { maxTemperature = v; }
    public void setMeanVibration(double v) { meanVibration = v; }
    public void setStdVibration(double v) { stdVibration = v; }
    public void setMeanPressure(double v) { meanPressure = v; }
    public void setStdPressure(double v) { stdPressure = v; }
    public void setMeanSpeed(double v) { meanSpeed = v; }
    public void setStdSpeed(double v) { stdSpeed = v; }
    public void setTrendTemperature(double v) { trendTemperature = v; }
    public void setTrendVibration(double v) { trendVibration = v; }
}

// FailurePredictor.java
package com.learning.maint.service;

import weka.classifiers.Classifier;
import weka.classifiers.trees.RandomForest;
import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.classifiers.Evaluation;

import java.io.File;
import java.util.List;

public class FailurePredictor {
    
    private Classifier classifier;
    private Instances trainingData;
    
    public void train(List<FeatureVector> features) throws Exception {
        Instances data = createInstances(features);
        this.trainingData = data;
        
        classifier = new RandomForest();
        classifier.setNumTrees(100);
        classifier.buildClassifier(data);
        
        Evaluation eval = new Evaluation(data);
        eval.crossValidateModel(classifier, data, 10, new java.util.Random(42));
        
        System.out.println("Model trained. Accuracy: " + eval.pctCorrect() + "%");
    }
    
    public double predictProbability(FeatureVector fv) throws Exception {
        weka.core.DenseInstance instance = new weka.core.DenseInstance(1.0, fv.toArray());
        instance.setDataset(trainingData);
        
        double[] distribution = classifier.distributionForInstance(instance);
        return distribution[trainingData.classAttribute().indexOfValue("FAILURE")];
    }
    
    public String predict(FeatureVector fv) throws Exception {
        double probability = predictProbability(fv);
        return probability > 0.5 ? "FAILURE" : "NORMAL";
    }
    
    private Instances createInstances(List<FeatureVector> features) {
        weka.core.Attribute attrMeanTemp = new weka.core.Attribute("meanTemperature");
        weka.core.Attribute attrStdTemp = new weka.core.Attribute("stdTemperature");
        weka.core.Attribute attrMinTemp = new weka.core.Attribute("minTemperature");
        weka.core.Attribute attrMaxTemp = new weka.core.Attribute("maxTemperature");
        weka.core.Attribute attrMeanVib = new weka.core.Attribute("meanVibration");
        weka.core.Attribute attrStdVib = new weka.core.Attribute("stdVibration");
        weka.core.Attribute attrMeanPres = new weka.core.Attribute("meanPressure");
        weka.core.Attribute attrStdPres = new weka.core.Attribute("stdPressure");
        weka.core.Attribute attrMeanSpeed = new weka.core.Attribute("meanSpeed");
        weka.core.Attribute attrStdSpeed = new weka.core.Attribute("stdSpeed");
        weka.core.Attribute attrTrendTemp = new weka.core.Attribute("trendTemperature");
        weka.core.Attribute attrTrendVib = new weka.core.Attribute("trendVibration");
        weka.core.Attribute attrLabel = new weka.core.Attribute("label", 
            java.util.Arrays.asList("NORMAL", "FAILURE"));
        
        java.util.ArrayList<weka.core.Attribute> attrs = new java.util.ArrayList<>();
        attrs.addAll(java.util.Arrays.asList(
            attrMeanTemp, attrStdTemp, attrMinTemp, attrMaxTemp,
            attrMeanVib, attrStdVib, attrMeanPres, attrStdPres,
            attrMeanSpeed, attrStdSpeed, attrTrendTemp, attrTrendVib, attrLabel
        ));
        
        Instances data = new Instances("MaintenanceData", attrs, features.size());
        data.setClass(attrLabel);
        
        for (FeatureVector fv : features) {
            double[] values = fv.toArray();
            double[] extended = new double[values.length + 1];
            System.arraycopy(values, 0, extended, 0, values.length);
            
            int labelIndex = fv.getLabel().equals("FAILURE") ? 1 : 0;
            extended[extended.length - 1] = labelIndex;
            
            data.add(new weka.core.DenseInstance(1.0, extended));
        }
        
        return data;
    }
}

// MaintenancePrediction.java
package com.learning.maint.model;

import java.time.LocalDateTime;

public class MaintenancePrediction {
    private String equipmentId;
    private String prediction;
    private double confidence;
    private LocalDateTime predictedTime;
    private String recommendedAction;
    
    public MaintenancePrediction(String equipmentId, String prediction, 
                                 double confidence, LocalDateTime predictedTime,
                                 String recommendedAction) {
        this.equipmentId = equipmentId;
        this.prediction = prediction;
        this.confidence = confidence;
        this.predictedTime = predictedTime;
        this.recommendedAction = recommendedAction;
    }
    
    public String getEquipmentId() { return equipmentId; }
    public String getPrediction() { return prediction; }
    public double getConfidence() { return confidence; }
    public LocalDateTime getPredictedTime() { return predictedTime; }
    public String getRecommendedAction() { return recommendedAction; }
}

// StatisticalAnalyzer.java
package com.learning.maint.analysis;

import org.apache.commons.math3.stat.inference.TTest;
import org.apache.commons.math3.stat.regression.SimpleRegression;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import java.util.List;

public class StatisticalAnalyzer {
    
    public double[] calculateDescriptiveStatistics(List<Double> values) {
        DescriptiveStatistics stats = new DescriptiveStatistics();
        values.forEach(stats::addValue);
        
        return new double[]{
            stats.getMean(),
            stats.getStandardDeviation(),
            stats.getMin(),
            stats.getMax(),
            stats.getMedian(),
            stats.getPercentile(25),
            stats.getPercentile(75)
        };
    }
    
    public boolean isAnomaly(double value, double mean, double stdDev, double threshold) {
        double zScore = Math.abs((value - mean) / stdDev);
        return zScore > threshold;
    }
    
    public SimpleRegression performRegression(List<Double> x, List<Double> y) {
        SimpleRegression regression = new SimpleRegression();
        
        for (int i = 0; i < x.size() && i < y.size(); i++) {
            regression.addData(x.get(i), y.get(i));
        }
        
        return regression;
    }
    
    public double tTest(double[] sample1, double[] sample2) {
        TTest tTest = new TTest();
        return tTest.t(sample1, sample2);
    }
    
    public double[] calculateMovingAverage(List<Double> values, int windowSize) {
        double[] result = new double[values.size()];
        
        for (int i = 0; i < values.size(); i++) {
            int start = Math.max(0, i - windowSize + 1);
            double sum = 0;
            for (int j = start; j <= i; j++) {
                sum += values.get(j);
            }
            result[i] = sum / (i - start + 1);
        }
        
        return result;
    }
}
```

### Build and Run
```bash
# Build the application
mvn clean package

# Run the application
java -jar target/predictive-maintenance-1.0.0.jar

# Run tests
mvn test

# Generate ARFF file
java -cp target/classes com.learning.maint.DataExporter data/sensor-readings.csv

# Evaluate model performance
java -cp target/classes com.learning.maint.ModelEvaluator models/failure-predictor.model
```

### Learning Outcomes
- Implement Weka classifiers in Java
- Use Apache Commons Math for statistical analysis
- Build feature extraction pipelines
- Evaluate model performance
- Implement anomaly detection
- Create predictive maintenance models