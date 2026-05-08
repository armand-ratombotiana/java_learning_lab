# Deep Dive: Weka - Machine Learning in Java

## Table of Contents
1. [Introduction to Weka](#introduction)
2. [Data Loading and Preprocessing](#data-preprocessing)
3. [Classification](#classification)
4. [Regression](#regression)
5. [Clustering](#clustering)
6. [Model Evaluation](#evaluation)
7. [Advanced Topics](#advanced)

---

## 1. Introduction to Weka

Weka (Waikato Environment for Knowledge Analysis) is a collection of machine learning algorithms for data mining tasks. It provides tools for classification, regression, clustering, association rule mining, and visualization.

### Key Features
- GUI, CLI, and Java API
- Preprocessing algorithms
- Classification: Decision trees, SVM, Neural networks, Ensemble methods
- Regression: Linear, tree-based
- Clustering: K-Means, hierarchical
- Built-in visualization

---

## 2. Data Loading and Preprocessing

### 2.1 Loading Data

```java
import weka.core.Instances;
import weka.core.converters.ArffLoader;
import java.io.File;

public class DataLoading {
    public static void main(String[] args) throws Exception {
        ArffLoader loader = new ArffLoader();
        loader.setSource(new File("data/weather.arff"));
        
        Instances data = loader.getDataSet();
        data.setClassIndex(data.numAttributes() - 1);
        
        System.out.println("Instances: " + data.numInstances());
        System.out.println("Attributes: " + data.numAttributes());
    }
}
```

### 2.2 Preprocessing Filters

```java
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Normalize;
import weka.filters.unsupervised.attribute.Discretize;

public class Preprocessing {
    
    public static Instances normalize(Instances data) throws Exception {
        Normalize normalize = new Normalize();
        normalize.setInputFormat(data);
        return Filter.useFilter(data, normalize);
    }
    
    public static Instances discretize(Instances data) throws Exception {
        Discretize discretize = new Discretize();
        discretize.setInputFormat(data);
        return Filter.useFilter(data, discretize);
    }
}
```

---

## 3. Classification

### 3.1 Decision Tree (J48)

```java
import weka.classifiers.trees.J48;
import weka.classifiers.Evaluation;

public class DecisionTreeExample {
    public static void main(String[] args) throws Exception {
        // Load data
        Instances data = loadData("data/iris.arff");
        
        // Build model
        J48 tree = new J48();
        tree.buildClassifier(data);
        
        // Evaluate
        Evaluation eval = new Evaluation(data);
        eval.crossValidateModel(tree, data, 10, new Random(1));
        
        System.out.println(eval.toSummaryString());
    }
}
```

### 3.2 Random Forest

```java
import weka.classifiers.trees.RandomForest;

public class RandomForestExample {
    public static void main(String[] args) throws Exception {
        Instances data = loadData("data/iris.arff");
        
        RandomForest rf = new RandomForest();
        rf.setNumTrees(100);
        rf.buildClassifier(data);
        
        // Predict
        double prediction = rf.classifyInstance(data.instance(0));
    }
}
```

### 3.3 Support Vector Machine

```java
import weka.classifiers.functions.SMO;

public class SVMExample {
    public static void main(String[] args) throws Exception {
        Instances data = loadData("data/iris.arff");
        
        SMO svm = new SMO();
        svm.buildClassifier(data);
    }
}
```

---

## 4. Regression

```java
import weka.classifiers.functions.LinearRegression;
import weka.classifiers.trees.M5P;

public class RegressionExample {
    
    public static void linearRegression(Instances data) throws Exception {
        LinearRegression model = new LinearRegression();
        model.buildClassifier(data);
    }
    
    public static void m5PRegression(Instances data) throws Exception {
        M5P model = new M5P();
        model.buildClassifier(data);
    }
}
```

---

## 5. Clustering

### 5.1 K-Means

```java
import weka.clusterers.SimpleKMeans;

public class ClusteringExample {
    public static void main(String[] args) throws Exception {
        Instances data = loadData("data/iris.arff");
        
        SimpleKMeans kmeans = new SimpleKMeans();
        kmeans.setNumClusters(3);
        kmeans.buildClusterer(data);
        
        // Assign cluster
        int cluster = kmeans.clusterInstance(data.instance(0));
    }
}
```

---

## 6. Model Evaluation

```java
import weka.classifiers.Evaluation;
import weka.core.Range;

public class ModelEvaluation {
    
    public static Evaluation evaluate(Instances data, Classifier model) 
            throws Exception {
        Evaluation eval = new Evaluation(data);
        eval.crossValidateModel(model, data, 10, new Random(1));
        
        return eval;
    }
    
    public static void printMetrics(Evaluation eval) {
        System.out.println("Accuracy: " + (eval.pctCorrect() * 100) + "%");
        System.out.println("Precision: " + eval.precision(1));
        System.out.println("Recall: " + eval.recall(1));
        System.out.println("F-Measure: " + eval.fMeasure(1));
    }
}
```

---

## 7. Advanced Topics

### 7.1 Attribute Selection

```java
import weka.attributeSelection.CfsSubsetEval;
import weka.attributeSelection.GreedyStepwise;

public class FeatureSelection {
    public static Instances selectFeatures(Instances data) throws Exception {
        CfsSubsetEval eval = new CfsSubsetEval();
        GreedyStepwise search = new GreedyStepwise();
        search.setSearchBackwards(true);
        
        return data;
    }
}
```

### 7.2 Ensemble Methods

```java
import weka.classifiers.ensemble.AdaBoost1;
import weka.classifiers.ensemble.Bagging;

public class EnsembleExample {
    
    public static void adaboost(Instances data) throws Exception {
        AdaBoost1 boost = new AdaBoost1();
        boost.buildClassifier(data);
    }
    
    public static void bagging(Instances data) throws Exception {
        Bagging bag = new Bagging();
        bag.buildClassifier(data);
    }
}
```

---

## Summary

Weka provides comprehensive ML capabilities in pure Java. Key classes include:
- Instances: Data container
- Classifier: Base classification interface
- Filter: Data preprocessing
- Evaluation: Model assessment

---

*Continue to QUIZZES.md and EDGE_CASES.md.*