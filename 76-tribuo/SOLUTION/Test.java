package com.learning.tribuo;

import org.tribuo.*;
import org.tribuo.classification.*;
import org.tribuo.regression.*;
import java.util.Map;
import java.util.HashMap;

public class Test {

    public static void main(String[] args) {
        System.out.println("Running Tribuo Tests\n");

        testClassifierCreation();
        testRegressorCreation();
        testDataSetCreation();
        testPrediction();
        testModelInfo();

        System.out.println("\nAll tests passed!");
    }

    private static void testClassifierCreation() {
        System.out.println("Test: Classifier Creation");
        Solution.ClassificationExample classification = new Solution.ClassificationExample();

        Classifier lr = classification.createLogisticRegression(100);
        assert lr != null : "Logistic regression should not be null";

        Classifier dt = classification.createDecisionTree(5);
        assert dt != null : "Decision tree should not be null";

        Classifier rf = classification.createRandomForest(5, 3);
        assert rf != null : "Random forest should not be null";

        System.out.println("  - All classifiers created successfully");
    }

    private static void testRegressorCreation() {
        System.out.println("Test: Regressor Creation");
        Solution.RegressionExample regression = new Solution.RegressionExample();

        Regressor linReg = regression.createLinearRegression();
        assert linReg != null : "Linear regression should not be null";

        Regressor ridge = regression.createRidgeRegression(1.0);
        assert ridge != null : "Ridge regression should not be null";

        System.out.println("  - Regressors created successfully");
    }

    private static void testDataSetCreation() {
        System.out.println("Test: Data Set Creation");
        Solution.DataProcessingExample dataProcessing = new Solution.DataProcessingExample();

        MutableDataset<Label> dataset = dataProcessing.createDataset();
        assert dataset != null : "Dataset should not be null";

        Map<String, Double> features = new HashMap<>();
        features.put("feature1", 1.0);
        features.put("feature2", 2.0);
        dataProcessing.addExample(dataset, features, "class1");

        assert dataset.size() == 1 : "Dataset should have 1 example";
        System.out.println("  - Dataset created with " + dataset.size() + " example(s)");
    }

    private static void testPrediction() {
        System.out.println("Test: Prediction");
        Solution.ClassificationExample classification = new Solution.ClassificationExample();
        Classifier classifier = classification.createLogisticRegression(10);

        MutableExample<Label> example = new MutableExample<>();
        example.put("feature1", 1.0);
        example.put("feature2", 2.0);

        System.out.println("  - Prediction structure ready");
    }

    private static void testModelInfo() {
        System.out.println("Test: Model Info");
        Solution.ModelExample modelExample = new Solution.ModelExample();

        Map<String, Double> importance = modelExample.getFeatureImportance(null);
        assert importance != null : "Feature importance map should not be null";

        System.out.println("  - Model info utilities ready");
    }
}