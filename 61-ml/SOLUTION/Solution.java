package com.learning.ml;

import weka.core.*;
import weka.classifiers.*;
import weka.classifiers.trees.*;
import weka.filters.*;
import weka.filters.unsupervised.attribute.*;

import java.util.*;

public class MLSolution {

    public Instances loadDataset(String filePath) throws Exception {
        java.io.Reader reader = new java.io.FileReader(filePath);
        return new Instances(reader);
    }

    public void setClassIndex(Instances data, int index) {
        data.setClassIndex(index);
    }

    public Instances normalizeData(Instances data) throws Exception {
        Normalize normalize = new Normalize();
        normalize.setInputFormat(data);
        return Filter.useFilter(data, normalize);
    }

    public Instances standardizeData(Instances data) throws Exception {
        Standardize standardize = new Standardize();
        standardize.setInputFormat(data);
        return Filter.useFilter(data, standardize);
    }

    public DecisionTree buildDecisionTree(Instances data) throws Exception {
        J48 tree = new J48();
        tree.buildClassifier(data);
        return new DecisionTree(tree);
    }

    public static class DecisionTree {
        private final J48 tree;

        public DecisionTree(J48 tree) {
            this.tree = tree;
        }

        public double classify(Instance instance) throws Exception {
            return tree.classifyInstance(instance);
        }

        public String getModel() throws Exception {
            return tree.toString();
        }
    }

    public LinearRegression buildLinearRegression(Instances data) throws Exception {
        weka.classifiers.functions.LinearRegression lr = new weka.classifiers.functions.LinearRegression();
        lr.buildClassifier(data);
        return new LinearRegression(lr);
    }

    public static class LinearRegression {
        private final weka.classifiers.functions.LinearRegression model;

        public LinearRegression(weka.classifiers.functions.LinearRegression model) {
            this.model = model;
        }

        public double predict(Instance instance) throws Exception {
            return model.classifyInstance(instance);
        }

        public double[] getCoefficients() {
            return model.coefficients();
        }
    }

    public KMeansClustering buildKMeans(Instances data, int clusters) throws Exception {
        weka.clusterers.SimpleKMeans kmeans = new weka.clusterers.SimpleKMeans();
        kmeans.setNumClusters(clusters);
        kmeans.buildClusterer(data);
        return new KMeansClustering(kmeans, data);
    }

    public static class KMeansClustering {
        private final weka.clusterers.SimpleKMeans kmeans;
        private final Instances data;

        public KMeansClustering(weka.clusterers.SimpleKMeans kmeans, Instances data) {
            this.kmeans = kmeans;
            this.data = data;
        }

        public int[] getClusterAssignments() throws Exception {
            return kmeans.getAssignments();
        }

        public double[][] getCentroids() {
            return kmeans.getClusterCenters();
        }
    }

    public RandomForest buildRandomForest(Instances data, int trees) throws Exception {
        RandomForest rf = new RandomForest();
        rf.setNumTrees(trees);
        rf.buildClassifier(data);
        return new RandomForestClassifier(rf);
    }

    public static class RandomForestClassifier {
        private final RandomForest rf;

        public RandomForestClassifier(RandomForest rf) {
            this.rf = rf;
        }

        public double classify(Instance instance) throws Exception {
            return rf.classifyInstance(instance);
        }

        public double[] getDistribution(Instance instance) throws Exception {
            return rf.distributionForInstance(instance);
        }
    }

    public double crossValidate(Classifier classifier, Instances data, int folds) throws Exception {
        weka.classifiers.Evaluation eval = new weka.classifiers.Evaluation(data);
        eval.crossValidateModel(classifier, data, folds, new Random(1));
        return eval.pctCorrect();
    }

    public Instances splitData(Instances data, double trainRatio) {
        int trainSize = (int) (data.numInstances() * trainRatio);
        Instances train = new Instances(data, 0, trainSize);
        Instances test = new Instances(data, trainSize, data.numInstances() - trainSize);
        return train;
    }

    public static class DataPreprocessor {
        private List<String> featureNames;
        private Map<String, Double> meanValues;
        private Map<String, Double> stdValues;

        public DataPreprocessor() {
            this.featureNames = new ArrayList<>();
            this.meanValues = new HashMap<>();
            this.stdValues = new HashMap<>();
        }

        public void fit(Instances data) {
            for (int i = 0; i < data.numAttributes(); i++) {
                featureNames.add(data.attribute(i).name());
            }
        }

        public Instance transform(Instance instance) {
            return instance;
        }
    }
}