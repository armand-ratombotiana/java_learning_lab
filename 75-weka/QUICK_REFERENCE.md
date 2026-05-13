# Weka ML Quick Reference

## Data Loading
```java
DataSource source = new DataSource("weather.arff");
Instances data = source.getDataSet();
data.setClassIndex(data.numAttributes() - 1);
```

## Classifiers

| Classifier | Class | Use Case |
|------------|-------|----------|
| Decision Tree | J48 | Interpretable classification |
| Random Forest | RandomForest | Ensemble learning |
| SVM | SMO | High-dimensional data |
| Naive Bayes | NaiveBayes | Fast baseline |
| K-Nearest Neighbors | IBk | Instance-based learning |

```java
Classifier j48 = new J48();
j48.buildClassifier(data);

// Random Forest
RandomForest rf = new RandomForest();
rf.setNumIterations(100);
rf.buildClassifier(data);

// SVM (SMO)
SMO smo = new SMO();
smo.setC(1.0);
smo.buildClassifier(data);
```

## Cross-Validation
```java
Evaluation eval = new Evaluation(data);
eval.crossValidateModel(classifier, data, 10, new Random(1));
System.out.println("Accuracy: " + eval.pctCorrect());
System.out.println("Precision: " + eval.weightedPrecision());
System.out.println("Recall: " + eval.weightedRecall());
System.out.println("F-Measure: " + eval.weightedFMeasure());
double[][] matrix = eval.confusionMatrix();
```

## Preprocessing Filters
```java
// Normalize
Normalize normalize = new Normalize();
normalize.setInputFormat(data);
Instances normalized = Filter.useFilter(data, normalize);

// Discretize
Discretize discretize = new Discretize();
discretize.setBins(10);
Instances discretized = Filter.useFilter(data, discretize);

// Remove attributes
Remove remove = new Remove();
remove.setAttributeIndices("1,3");
```

## Clustering
```java
SimpleKMeans kmeans = new SimpleKMeans();
kmeans.setNumClusters(3);
kmeans.setSeed(42);
kmeans.buildClusterer(data);
int[] assignments = kmeans.getAssignments();
```

## Association Rules
```java
Apriori apriori = new Apriori();
apriori.setMinMetric(0.8);
apriori.buildAssociations(data);
```

## Attribute Selection
```java
AttributeSelection selector = new AttributeSelection();
CfsSubsetEval eval = new CfsSubsetEval();
GreedyStepwise search = new GreedyStepwise();
selector.setEvaluator(eval);
selector.setSearch(search);
selector.SelectAttributes(data);
int[] selected = selector.selectedAttributes();
```