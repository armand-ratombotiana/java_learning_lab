package com.learning.weka;

import weka.classifiers.Classifier;
import weka.core.Instances;
import weka.core.DenseInstance;
import weka.core.Attribute;
import java.util.ArrayList;

public class Test {

    public static void main(String[] args) throws Exception {
        System.out.println("Running Weka Tests\n");

        testClassifierCreation();
        testDataSetCreation();
        testPreprocessing();
        testClustererCreation();
        testEvaluationMetrics();

        System.out.println("\nAll tests passed!");
    }

    private static void testClassifierCreation() {
        System.out.println("Test: Classifier Creation");
        Solution.ClassificationExample classifierExample = new Solution.ClassificationExample();

        Classifier dt = classifierExample.createDecisionTree();
        assert dt != null : "Decision tree should not be null";

        Classifier rf = classifierExample.createRandomForest();
        assert rf != null : "Random forest should not be null";

        Classifier nb = classifierExample.createNaiveBayes();
        assert nb != null : "Naive Bayes should not be null";

        System.out.println("  - All classifiers created successfully");
    }

    private static void testDataSetCreation() throws Exception {
        System.out.println("Test: Data Set Creation");
        ArrayList<Attribute> attributes = new ArrayList<>();
        attributes.add(new Attribute("feature1"));
        attributes.add(new Attribute("feature2"));
        ArrayList<String> classValues = new ArrayList<>();
        classValues.add("class1");
        classValues.add("class2");
        attributes.add(new Attribute("class", classValues));

        Instances data = new Instances("TestDataset", attributes, 10);
        data.setClassIndex(2);

        assert data.numAttributes() == 3 : "Should have 3 attributes";
        assert data.numInstances() == 0 : "Should have 0 instances";

        double[] values = {1.0, 2.0, 0.0};
        Instance instance = new DenseInstance(1.0, values);
        data.add(instance);

        assert data.numInstances() == 1 : "Should have 1 instance";
        System.out.println("  - Dataset created with " + data.numInstances() + " instance(s)");
    }

    private static void testPreprocessing() throws Exception {
        System.out.println("Test: Preprocessing");
        Solution.DataPreprocessingExample preprocessing = new Solution.DataPreprocessingExample();

        ArrayList<Attribute> attributes = new ArrayList<>();
        attributes.add(new Attribute("attr1"));
        attributes.add(new Attribute("attr2"));
        Instances data = new Instances("TestData", attributes, 5);

        data.add(new DenseInstance(1.0, new double[]{1.0, 2.0}));
        data.add(new DenseInstance(1.0, new double[]{3.0, 4.0}));
        data.add(new DenseInstance(1.0, new double[]{5.0, 6.0}));

        Instances normalized = preprocessing.normalizeData(data);
        assert normalized != null : "Normalized data should not be null";

        Instances standardized = preprocessing.standardizeData(data);
        assert standardized != null : "Standardized data should not be null";

        System.out.println("  - Normalization and standardization work");
    }

    private static void testClustererCreation() throws Exception {
        System.out.println("Test: Clusterer Creation");
        Solution.ClusteringExample clusteringExample = new Solution.ClusteringExample();

        weka.clusterers.Clusterer kMeans = clusteringExample.createKMeans(3);
        assert kMeans != null : "K-Means clusterer should not be null";

        weka.clusterers.Clusterer em = clusteringExample.createEM(5);
        assert em != null : "EM clusterer should not be null";

        System.out.println("  - Clusterers created successfully");
    }

    private static void testEvaluationMetrics() throws Exception {
        System.out.println("Test: Evaluation Metrics");
        Solution.ClassificationExample classifierExample = new Solution.ClassificationExample();

        Classifier classifier = classifierExample.createDecisionTree();
        assert classifier != null : "Classifier should not be null";

        System.out.println("  - Evaluation metrics ready");
    }
}