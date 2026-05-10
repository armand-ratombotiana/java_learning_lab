package com.learning.weka;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.RandomForest;
import weka.classifiers.lazy.IBk;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.functions.Logistic;
import weka.classifiers.functions.LinearRegression;
import weka.classifiers.functions.SMO;
import weka.classifiers.meta.AdaBoost;
import weka.classifiers.rules.JRip;
import weka.clusterers.Clusterer;
import weka.clusterers.SimpleKMeans;
import weka.clusterers.EM;
import weka.clusterers.DBScan;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.converters.ArffLoader;
import weka.core.converters.ArffSaver;
import weka.core.converters.CSVLoader;
import weka.core.converters.ConverterUtils;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Normalize;
import weka.filters.unsupervised.attribute.Standardize;
import weka.filters.unsupervised.attribute.Discretize;
import weka.filters.unsupervised.attribute.Remove;

import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Solution {

    public static class ClassificationExample {

        public Classifier createDecisionTree() {
            return new J48();
        }

        public Classifier createRandomForest() {
            return new RandomForest();
        }

        public Classifier createNaiveBayes() {
            return new NaiveBayes();
        }

        public Classifier createKNN(int k) {
            return new IBk(k);
        }

        public Classifier createLogisticRegression() {
            return new Logistic();
        }

        public Classifier createSVM() {
            return new SMO();
        }

        public Classifier createAdaBoost() {
            return new AdaBoost();
        }

        public Classifier createRuleClassifier() {
            return new JRip();
        }

        public void trainClassifier(Classifier classifier, Instances trainingData) throws Exception {
            classifier.buildClassifier(trainingData);
        }

        public double[] predict(Classifier classifier, Instances testData) throws Exception {
            double[] predictions = new double[testData.numInstances()];
            for (int i = 0; i < testData.numInstances(); i++) {
                predictions[i] = classifier.classifyInstance(testData.instance(i));
            }
            return predictions;
        }

        public Evaluation evaluateClassifier(Classifier classifier, Instances data, int folds) throws Exception {
            Evaluation evaluation = new Evaluation(data);
            evaluation.crossValidateModel(classifier, data, folds, new Random(1));
            return evaluation;
        }

        public double getAccuracy(Evaluation evaluation) {
            return evaluation.pctCorrect();
        }

        public double getPrecision(Evaluation evaluation, int classIndex) {
            return evaluation.precision(classIndex);
        }

        public double getRecall(Evaluation evaluation, int classIndex) {
            return evaluation.recall(classIndex);
        }

        public double getF1Score(Evaluation evaluation, int classIndex) {
            return evaluation.fMeasure(classIndex);
        }

        public String getConfusionMatrix(Evaluation evaluation) {
            return evaluation.toMatrixString();
        }
    }

    public static class RegressionExample {

        public Classifier createLinearRegression() {
            return new LinearRegression();
        }

        public double predictValue(Classifier model, Instance instance) throws Exception {
            return model.classifyInstance(instance);
        }

        public double calculateRMSE(Evaluation evaluation) {
            return Math.sqrt(evaluation.rootMeanSquaredError());
        }

        public double calculateMAE(Evaluation evaluation) {
            return evaluation.meanAbsoluteError();
        }

        public double calculateRSquared(Evaluation evaluation) {
            return evaluation.correlationCoefficient();
        }
    }

    public static class ClusteringExample {

        public Clusterer createKMeans(int numClusters) throws Exception {
            SimpleKMeans kMeans = new SimpleKMeans();
            kMeans.setNumClusters(numClusters);
            kMeans.setSeed(10);
            return kMeans;
        }

        public Clusterer createEM(int maxClusters) throws Exception {
            EM em = new EM();
            em.setMaxClusters(maxClusters);
            return em;
        }

        public void trainClusterer(Clusterer clusterer, Instances data) throws Exception {
            clusterer.buildClusterer(data);
        }

        public int[] getClusterAssignments(Clusterer clusterer, Instances data) throws Exception {
            return clusterer.clusterInstances(data);
        }

        public int getClusterForInstance(Clusterer clusterer, Instance instance) throws Exception {
            return clusterer.clusterInstance(instance);
        }

        public double getClusterDistance(Clusterer clusterer, Instance instance, int clusterIndex) throws Exception {
            return clusterer.clusterMembership(instance)[clusterIndex];
        }

        public int getNumClusters(Clusterer clusterer) {
            return clusterer.numberOfClusters();
        }
    }

    public static class DataPreprocessingExample {

        public Instances loadARFF(String filePath) throws Exception {
            BufferedReader reader = new BufferedReader(new FileReader(filePath));
            Instances data = new Instances(reader);
            reader.close();
            data.setClassIndex(data.numAttributes() - 1);
            return data;
        }

        public Instances loadCSV(String filePath) throws Exception {
            CSVLoader loader = new CSVLoader();
            loader.setSource(new File(filePath));
            return loader.getDataSet();
        }

        public void saveARFF(Instances data, String filePath) throws Exception {
            ArffSaver saver = new ArffSaver();
            saver.setInstances(data);
            saver.setFile(new File(filePath));
            saver.writeBatch();
        }

        public Instances normalizeData(Instances data) throws Exception {
            Normalize filter = new Normalize();
            filter.setInputFormat(data);
            return Filter.useFilter(data, filter);
        }

        public Instances standardizeData(Instances data) throws Exception {
            Standardize filter = new Standardize();
            filter.setInputFormat(data);
            return Filter.useFilter(data, filter);
        }

        public Instances discretizeData(Instances data, int bins) throws Exception {
            Discretize filter = new Discretize();
            filter.setBins(bins);
            filter.setInputFormat(data);
            return Filter.useFilter(data, filter);
        }

        public Instances removeAttribute(Instances data, int attributeIndex) throws Exception {
            Remove filter = new Remove();
            filter.setAttributeIndicesArray(new int[]{attributeIndex});
            filter.setInputFormat(data);
            return Filter.useFilter(data, filter);
        }

        public Instances splitData(Instances data, double trainPercentage) {
            int trainSize = (int) (data.numInstances() * trainPercentage / 100);
            int testSize = data.numInstances() - trainSize;

            Instances train = new Instances(data, 0, trainSize);
            Instances test = new Instances(data, trainSize, testSize);

            return train;
        }
    }

    public static class FeatureSelectionExample {

        public Instances selectFeaturesByVariance(Instances data, double threshold) {
            return data;
        }

        public Instances selectTopNFeatures(Instances data, int n) {
            return data;
        }
    }

    public static class ModelPersistenceExample {

        public void saveModel(Classifier classifier, String filePath) throws Exception {
            weka.core.SerializationHelper.write(filePath, classifier);
        }

        public Classifier loadModel(String filePath) throws Exception {
            return (Classifier) weka.core.SerializationHelper.read(filePath);
        }

        public void saveClusterer(Clusterer clusterer, String filePath) throws Exception {
            weka.core.SerializationHelper.write(filePath, clusterer);
        }

        public Clusterer loadClusterer(String filePath) throws Exception {
            return (Clusterer) weka.core.SerializationHelper.read(filePath);
        }
    }

    public static class VisualizationExample {

        public String getAttributeSummary(Instances data) {
            StringBuilder summary = new StringBuilder();
            summary.append("Dataset: ").append(data.relationName()).append("\n");
            summary.append("Instances: ").append(data.numInstances()).append("\n");
            summary.append("Attributes: ").append(data.numAttributes()).append("\n");
            summary.append("\nAttributes:\n");

            for (int i = 0; i < data.numAttributes(); i++) {
                Attribute attr = data.attribute(i);
                summary.append(i + 1).append(". ").append(attr.name())
                    .append(" (").append(attr.typeString()).append(")\n");
            }
            return summary.toString();
        }

        public String getClassDistribution(Instances data) {
            Attribute classAttr = data.classAttribute();
            int[] counts = new int[classAttr.numValues()];

            for (Instance instance : data) {
                counts[(int) instance.classValue()]++;
            }

            StringBuilder distribution = new StringBuilder();
            distribution.append("Class Distribution:\n");
            for (int i = 0; i < classAttr.numValues(); i++) {
                distribution.append(classAttr.value(i)).append(": ")
                    .append(counts[i]).append(" (")
                    .append(String.format("%.1f", (counts[i] * 100.0 / data.numInstances())))
                    .append("%)\n");
            }
            return distribution.toString();
        }
    }

    public static void main(String[] args) throws Exception {
        System.out.println("Weka Machine Learning Solutions");
        System.out.println("================================");

        ClassificationExample classifierExample = new ClassificationExample();
        Classifier dt = classifierExample.createDecisionTree();
        System.out.println("Decision Tree classifier created");

        Classifier rf = classifierExample.createRandomForest();
        System.out.println("Random Forest classifier created");

        Classifier nb = classifierExample.createNaiveBayes();
        System.out.println("Naive Bayes classifier created");

        RegressionExample regressionExample = new RegressionExample();
        Classifier lr = regressionExample.createLinearRegression();
        System.out.println("Linear Regression model created");

        ClusteringExample clusteringExample = new ClusteringExample();
        Clusterer kMeans = clusteringExample.createKMeans(3);
        System.out.println("K-Means clustering ready");

        DataPreprocessingExample preprocessingExample = new DataPreprocessingExample();
        System.out.println("Data preprocessing utilities available");

        ModelPersistenceExample persistenceExample = new ModelPersistenceExample();
        System.out.println("Model persistence utilities ready");

        VisualizationExample visualization = new VisualizationExample();
        System.out.println("Visualization utilities available");
    }
}