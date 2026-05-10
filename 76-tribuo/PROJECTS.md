# Tribuo Projects - Module 76

This module covers Microsoft's Tribuo ML library with support for TensorFlow, ONNX, and XGBoost integration.

## Mini-Project: Multi-Framework ML Integration (2-4 hours)

### Overview
Build a Java application using Tribuo to create machine learning models with multiple frameworks (Tribuo native, TensorFlow, ONNX). This project demonstrates Tribuo's unified API for different ML backends.

### Project Structure
```
tribuo-mini/
├── pom.xml
├── src/main/java/com/learning/tribuo/
│   ├── MiniProjectApplication.java
│   ├── classification/
│   │   └── ClassificationService.java
│   ├── regression/
│   │   └── RegressionService.java
│   └── evaluation/
│       └── EvaluationService.java
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
    <artifactId>tribuo-mini</artifactId>
    <version>1.0.0</version>
    <name>Tribuo Mini Project</name>
    
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.0</version>
    </parent>
    
    <properties>
        <java.version>21</java.version>
        <tribuo.version>4.3.0</tribuo.version>
    </properties>
    
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter</artifactId>
        </dependency>
        <dependency>
            <groupId>org.tribuo</groupId>
            <artifactId>tribuo-core</artifactId>
            <version>${tribuo.version}</version>
        </dependency>
        <dependency>
            <groupId>org.tribuo</groupId>
            <artifactId>tribuo-classification-trees</artifactId>
            <version>${tribuo.version}</version>
        </dependency>
    </dependencies>
</project>
```

#### Classification Service
```java
package com.learning.tribuo.classification;

import org.tribuo.*;
import org.tribuo.classification.*;
import org.tribuo.classification.evaluation.*;
import org.tribuo.classification.oracle.Oracle;
import org.tribuo.data.DataSplitter;
import org.tribuo.data.RandomDataSplitter;
import org.tribuo.evaluation.Evaluator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.*;

@Service
public class ClassificationService {
    
    private static final Logger logger = LoggerFactory.getLogger(ClassificationService.class);
    
    private Classifier<String> trainedModel;
    private LabelConverter labelConverter;
    
    public void train(Dataset<String> trainData) {
        logger.info("Training classification model...");
        
        labelConverter = new LabelConverter(trainData.getOutputs());
        
        RandomForestTrainer trainer = new RandomForestTrainer(100);
        
        trainedModel = trainer.train(trainData);
        
        logger.info("Training complete");
    }
    
    public ClassificationResult predict(Example<String> example) {
        if (trainedModel == null) {
            throw new IllegalStateException("Model not trained");
        }
        
        Output<String> output = trainedModel.predict(example);
        
        Map<String, Double> probabilities = new HashMap<>();
        
        if (output instanceof Label) {
            Label label = (Label) output;
            
            for (Label possibleLabel : trainedModel.getOutputInfo().getDomain()) {
                probabilities.put(possibleLabel.getLabel(), 
                    trainedModel.predict(examples).asMap().getOrDefault(possibleLabel, 0.0));
            }
        }
        
        return new ClassificationResult(output.toString(), output.getLabel(), probabilities);
    }
    
    public Evaluation<Label> evaluate(Dataset<String> testData) {
        if (trainedModel == null) {
            throw new IllegalStateException("Model not trained");
        }
        
        LabelEvaluator evaluator = new LabelEvaluator();
        
        return evaluator.evaluate(trainedModel, testData);
    }
    
    public void saveModel(String path) throws Exception {
        if (trainedModel == null) {
            throw new IllegalStateException("No model to save");
        }
        
        Model.save(trainedModel, Path.of(path));
        
        logger.info("Model saved to {}", path);
    }
    
    public void loadModel(String path) throws Exception {
        trainedModel = Model.load(Path.of(path));
        
        logger.info("Model loaded from {}", path);
    }
}

class ClassificationResult {
    private final String predictedLabel;
    private final double confidence;
    private final Map<String, Double> probabilities;
    
    public ClassificationResult(String predictedLabel, double confidence, 
                               Map<String, Double> probabilities) {
        this.predictedLabel = predictedLabel;
        this.confidence = confidence;
        this.probabilities = probabilities;
    }
    
    public String getPredictedLabel() { return predictedLabel; }
    public double getConfidence() { return confidence; }
    public Map<String, Double> getProbabilities() { return probabilities; }
}
```

#### Regression Service
```java
package com.learning.tribuo.regression;

import org.tribuo.*;
import org.tribuo.regression.*;
import org.tribuo.regression.evaluation.*;
import org.tribuo.regression.trainer.RandomForest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class RegressionService {
    
    private static final Logger logger = LoggerFactory.getLogger(RegressionService.class);
    
    private Regressor trainedModel;
    
    public void train(Dataset<Double> trainData) {
        logger.info("Training regression model...");
        
        RandomForest trainer = new RandomForest();
        
        trainedModel = trainer.train(trainData);
        
        logger.info("Training complete");
    }
    
    public double predict(double[] features) {
        if (trainedModel == null) {
            throw new IllegalStateException("Model not trained");
        }
        
        DenseFeatureMap featureMap = new DenseFeatureMap(features.length);
        
        for (int i = 0; i < features.length; i++) {
            featureMap.add(new Feature("f" + i, features[i]));
        }
        
        Example example = new Example(featureMap);
        
        Output output = trainedModel.predict(example);
        
        if (output instanceof RegressionOutput) {
            return ((RegressionOutput) output).getValues()[0];
        }
        
        return 0.0;
    }
    
    public RegressionEvaluation evaluate(Dataset<Double> testData) {
        if (trainedModel == null) {
            throw new IllegalStateException("Model not trained");
        }
        
        RegressionEvaluator evaluator = new RegressionEvaluator();
        
        return evaluator.evaluate(trainedModel, testData);
    }
}
```

### Build and Run

```bash
mvn clean compile
java -cp target/classes com.learning.tribuo.MiniProjectApplication
```

### Extension Ideas
1. Add TensorFlow model integration
2. Add ONNX model support
3. Implement XGBoost integration
4. Add model comparison
5. Implement ensemble methods

---

## Real-World Project: Production ML System with Multi-Framework Support (8+ hours)

### Project Structure
```
tribuo-production/
├── pom.xml
├── src/main/java/com/learning/tribuo/
│   ├── ProductionApplication.java
│   ├── config/
│   │   └── TribuoConfig.java
│   ├── service/
│   │   ├── ModelTrainingService.java
│   │   ├── ModelServingService.java
│   │   └── ONNXService.java
│   ├── controller/
│   │   └── PredictionController.java
│   └── dto/
│       └── PredictionRequest.java
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
    <artifactId>tribuo-production</artifactId>
    <version>1.0.0</version>
    <name>Tribuo Production</name>
    
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.0</version>
    </parent>
    
    <properties>
        <java.version>21</java.version>
        <tribuo.version>4.3.0</tribuo.version>
    </properties>
    
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.tribuo</groupId>
            <artifactId>tribuo-core</artifactId>
            <version>${tribuo.version}</version>
        </dependency>
    </dependencies>
</project>
```

#### Model Training Service
```java
package com.learning.tribuo.service;

import org.tribuo.*;
import org.tribuo.classification.*;
import org.tribuo.data.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ModelTrainingService {
    
    private static final Logger logger = LoggerFactory.getLogger(ModelTrainingService.class);
    
    public Dataset<String> loadClassificationData(String csvPath) {
        logger.info("Loading classification data from {}", csvPath);
        
        MutableDataset<String> dataset = new MutableDataset<>();
        
        return dataset;
    }
    
    public Dataset<Double> loadRegressionData(String csvPath) {
        logger.info("Loading regression data from {}", csvPath);
        
        MutableDataset<Double> dataset = new MutableDataset<>();
        
        return dataset;
    }
    
    public void trainModel(Dataset<String> data, String algorithm) {
        Classifier<String> model;
        
        switch (algorithm.toLowerCase()) {
            case "random-forest" -> {
                RandomForestTrainer trainer = new RandomForestTrainer();
                model = trainer.train(data);
            }
            case "xgboost" -> {
                throw new UnsupportedOperationException("XGBoost not configured");
            }
            default -> throw new IllegalArgumentException("Unknown algorithm: " + algorithm);
        }
        
        logger.info("Model trained with {}", algorithm);
    }
}
```

#### Model Serving Service
```java
package com.learning.tribuo.service;

import org.tribuo.*;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

@Service
public class ModelServingService {
    
    private static final Logger logger = LoggerFactory.getLogger(ModelServingService.class);
    
    private Map<String, Model> loadedModels;
    
    public ModelServingService() {
        this.loadedModels = new HashMap<>();
    }
    
    public void registerModel(String name, Model model) {
        loadedModels.put(name, model);
        logger.info("Registered model: {}", name);
    }
    
    public Object predict(String modelName, double[] features) {
        Model model = loadedModels.get(modelName);
        
        if (model == null) {
            throw new IllegalArgumentException("Model not found: " + modelName);
        }
        
        return model.predict(features);
    }
}
```

### Build and Run

```bash
mvn clean package
java -jar target/tribuo-production-1.0.0.jar
```

### Extension Ideas
1. Add TensorFlow Lite integration
2. Implement model versioning
3. Add A/B testing
4. Implement model monitoring
5. Add real-time training