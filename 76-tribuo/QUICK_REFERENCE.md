# Oracle Tribuo ML Quick Reference

## Classification
```java
LinearSGDModel<Label> model = LinearSGDModel.create();
Trainer<Label> trainer = new LinearSGDTrainer<>();
model = trainer.train(dataset);
```

## Dataset Loading
```java
DataSource<Label> source = new CSVDataSource(
    new File("data.csv"), new LabelFactory(),
    List.of("feature1", "feature2", "label"));
Dataset<Label> dataset = new MutableDataset<>(source);
```

## Evaluation
```java
Label[] predictions = model.predict(dataset);
ConfusionMatrix<Label> cm = new ConfusionMatrix<>(
    dataset.getOutputs(), predictions);
System.out.println("Accuracy: " + cm.accuracy());
System.out.println("F1: " + cm.f1());
```

## Regression
```java
LinearSGDModel<Regressor> regressor = LinearSGDModel.create();
Trainer<Regressor> regTrainer = new LinearSGDTrainer<>();
regressor = regTrainer.train(regDataset);
```

## Clustering
```java
KMeansTrainer kmeans = new KMeansTrainer(3, 100, 42);
KMeansModel clusterModel = kmeans.train(dataset);
int[] assignments = clusterModel.getAssignments();
```

## Feature Processing
```java
// Normalization
Normalizer normalizer = new StandardScaler();
normalizer.fit(dataset, dataset.getFeatureNames());
Dataset<Label> normalized = normalizer.transform(dataset);

// PCA
PCATransform pca = new PCATransform();
pca.fit(normalized);
Dataset<Label> reduced = pca.transform(normalized);
```

## Ensemble Methods
```java
RandomForestTrainer<Label> rf = new RandomForestTrainer<>();
rf.setNumTrees(100);
rf.setMaxDepth(10);
ClassificationModel<Label> forest = rf.train(dataset);
```

## Supported Features

| Category | Algorithms |
|----------|------------|
| Classification | SVM, Linear, CART, Random Forest |
| Regression | Linear, SVR, Regression Tree |
| Clustering | KMeans, DBSCAN |
| Feature Selection | Information Gain, Chi-Square |
| Anomaly Detection | LOF, Isolation Forest |
| Recommendation | Matrix Factorization |