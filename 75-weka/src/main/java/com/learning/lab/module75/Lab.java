package com.learning.lab.module75;

import java.util.*;

public class Lab {
    public static void main(String[] args) {
        System.out.println("=".repeat(60));
        System.out.println("Module 75: Weka - Machine Learning Algorithms");
        System.out.println("=".repeat(60));

        wekaOverview();
        dataLoading();
        attributeHandling();
        classification();
        clustering();
        regression();
        associationRules();
        ensembleMethods();
        featureSelection();
        modelEvaluation();
        preprocessing();
        modelPersistence();

        System.out.println("\n" + "=".repeat(60));
        System.out.println("Module 75 Lab Complete!");
        System.out.println("=".repeat(60));
    }

    static void wekaOverview() {
        System.out.println("\n--- Weka Overview ---");
        System.out.println("Weka is a collection of ML algorithms for data mining tasks");

        System.out.println("\nKey Features:");
        System.out.println("   - GUI, CLI, and Java API");
        System.out.println("   - Preprocessing tools");
        System.out.println("   - Classification algorithms");
        System.out.println("   - Clustering algorithms");
        System.out.println("   - Regression methods");
        System.out.println("   - Feature selection");
        System.out.println("   - Visualization");

        System.out.println("\nAlgorithm Categories:");
        String[] categories = {
            "Bayesian: Naive Bayes, Bayesian Network",
            "Trees: Decision Tree, Random Forest",
            "Rules: OneR, ZeroR, PART",
            "Meta: Boosting, Bagging, Stacking",
            "Lazy: k-NN, IBk",
            "Linear: Logistic Regression, SVM"
        };
        for (String cat : categories) {
            System.out.println("   - " + cat);
        }

        System.out.println("\nWeka Data Format (ARFF):");
        String arffExample = "@relation weather\n\n" +
            "@attribute outlook {sunny, overcast, rainy}\n" +
            "@attribute temperature numeric\n" +
            "@attribute humidity numeric\n" +
            "@attribute play {yes, no}\n\n" +
            "@data\nsunny,85,80,no\novercast,83,86,yes...";
        System.out.println("   " + arffExample.replace("\n", " | "));
    }

    static void dataLoading() {
        System.out.println("\n--- Data Loading ---");
        System.out.println("Loading datasets into Weka");

        System.out.println("\nLoading from File:");
        System.out.println("   ArffLoader loader = new ArffLoader();");
        System.out.println("   loader.setFile(new File(\"data/weather.arff\"));");
        System.out.println("   Instances data = loader.getDataSet();");

        System.out.println("\nLoading from URL:");
        System.out.println("   URL url = new URL(\"https://example.com/data.arff\");");
        System.out.println("   ArffLoader loader = new ArffLoader();");
        System.out.println("   loader.setURL(url);");
        System.out.println("   Instances data = loader.getDataSet();");

        System.out.println("\nLoading CSV:");
        System.out.println("   CSVLoader loader = new CSVLoader();");
        System.out.println("   loader.setSource(new File(\"data.csv\"));");
        System.out.println("   Instances data = loader.getDataSet();");

        System.out.println("\nDataset Operations:");
        System.out.println("   data.setClassIndex(data.numAttributes() - 1);");
        System.out.println("   int numInstances = data.numInstances();");
        System.out.println("   int numAttributes = data.numAttributes();");
        System.out.println("   System.out.println(data.sumWeights());");

        System.out.println("\nInstance Access:");
        System.out.println("   Instance instance = data.instance(0);");
        System.out.println("   double value = instance.value(0);");
        System.out.println("   String label = instance.stringValue(classIndex);");
    }

    static void attributeHandling() {
        System.out.println("\n--- Attribute Handling ---");
        System.out.println("Working with dataset attributes");

        System.out.println("\nAttribute Types:");
        System.out.println("   - NUMERIC: Continuous values");
        System.out.println("   - NOMINAL: Categorical values");
        System.out.println("   - STRING: Text data");
        System.out.println("   - DATE: Temporal data");
        System.out.println("   - RELATIONAL: Multi-instance");

        System.out.println("\nAttribute Information:");
        System.out.println("   Attribute attr = data.attribute(\"temperature\");");
        System.out.println("   int index = attr.index();");
        System.out.println("   String name = attr.name();");
        System.out.println("   String type = Attribute.typeToString(attr.type());");

        System.out.println("\nNominal Attributes:");
        System.out.println("   Attribute outlook = new Attribute(\"outlook\",");
        System.out.println("       Arrays.asList(\"sunny\", \"overcast\", \"rainy\"));");
        System.out.println("   int sunnyIndex = outlook.indexOf(\"sunny\");");

        System.out.println("\nIterating Attributes:");
        for (int i = 0; i < 4; i++) {
            System.out.printf("   [%d] %s (type: %s)%n", i,
                "attribute_" + i, i < 2 ? "numeric" : "nominal");
        }

        System.out.println("\nClass Attribute:");
        System.out.println("   int classIndex = data.classIndex();");
        System.out.println("   Attribute classAttr = data.classAttribute();");
        System.out.println("   data.setClassIndex(4);");
    }

    static void classification() {
        System.out.println("\n--- Classification ---");
        System.out.println("Supervised learning for categorical predictions");

        System.out.println("\nWe are exploring several classification algorithms:");

        System.out.println("\n1. Decision Tree (J48):");
        System.out.println("   J48 tree = new J48();");
        System.out.println("   tree.buildClassifier(data);");
        System.out.println("   - Uses information gain for splitting");
        System.out.println("   - Prunes to avoid overfitting");
        double[] j48Accuracy = {94.5};
        System.out.printf("   Accuracy: %.1f%%%n", j48Accuracy[0]);

        System.out.println("\n2. Naive Bayes:");
        System.out.println("   NaiveBayes nb = new NaiveBayes();");
        System.out.println("   nb.buildClassifier(data);");
        System.out.println("   - Probabilistic classifier");
        System.out.println("   - Assumes feature independence");
        double[] nbAccuracy = {91.2};
        System.out.printf("   Accuracy: %.1f%%%n", nbAccuracy[0]);

        System.out.println("\n3. k-Nearest Neighbors (IBk):");
        System.out.println("   IBk knn = new IBk(5);");
        System.out.println("   knn.buildClassifier(data);");
        System.out.println("   - Instance-based learning");
        System.out.println("   - k = number of neighbors");
        double[] knnAccuracy = {89.7};
        System.out.printf("   Accuracy: %.1f%%%n", knnAccuracy[0]);

        System.out.println("\n4. Support Vector Machine (SMO):");
        System.out.println("   SMO svm = new SMO();");
        System.out.println("   svm.buildClassifier(data);");
        System.out.println("   - Finds optimal hyperplane");
        System.out.println("   - Handles linear and non-linear");
        double[] svmAccuracy = {95.3};
        System.out.printf("   Accuracy: %.1f%%%n", svmAccuracy[0]);

        System.out.println("\nPrediction:");
        System.out.println("   Instance test = new DenseInstance(4);");
        System.out.println("   test.setValue(0, \"sunny\");");
        System.out.println("   test.setValue(1, 85.0);");
        System.out.println("   test.setValue(2, 80.0);");
        System.out.println("   double prediction = classifier.classifyInstance(test);");
    }

    static void clustering() {
        System.out.println("\n--- Clustering ---");
        System.out.println("Unsupervised learning to find natural groups");

        System.out.println("\nClustering Algorithms:");

        System.out.println("\n1. K-Means:");
        System.out.println("   SimpleKMeans kmeans = new SimpleKMeans();");
        System.out.println("   kmeans.setNumClusters(3);");
        System.out.println("   kmeans.buildClusterer(data);");
        System.out.println("   - Partitions data into k clusters");
        System.out.println("   - Minimizes within-cluster variance");

        double[] sampleData = {1.0, 2.0, 3.0, 10.0, 11.0, 12.0, 20.0, 21.0, 22.0};
        int[] kMeansAssignments = kMeansSimple(sampleData, 3);
        System.out.println("   Assignments: " + Arrays.toString(kMeansAssignments));
        System.out.println("   Clusters: 3");

        System.out.println("\n2. EM (Expectation-Maximization):");
        System.out.println("   EM em = new EM();");
        System.out.println("   em.setNumClusters(3);");
        System.out.println("   em.buildClusterer(data);");
        System.out.println("   - Probabilistic clustering");
        System.out.println("   - Handles overlapping clusters");

        System.out.println("\n3. Hierarchical Clustering:");
        System.out.println("   HierarchicalClusterer hierarchical = new HierarchicalClusterer();");
        System.out.println("   hierarchical.buildClusterer(data);");
        System.out.println("   - Creates dendrogram");
        System.out.println("   - No need to specify k");

        System.out.println("\nCluster Analysis:");
        System.out.println("   int cluster = kmeans.clusterInstance(instance);");
        System.out.println("   double[] distribution = kmeans.distributionForInstance(instance);");
        System.out.println("   int[] clusterSizes = kmeans.getClusterSizes();");
    }

    static int[] kMeansSimple(double[] data, int k) {
        double[] centroids = new double[k];
        for (int i = 0; i < k; i++) {
            centroids[i] = i * 10.0;
        }
        int[] assignments = new int[data.length];
        for (int i = 0; i < data.length; i++) {
            double minDist = Double.MAX_VALUE;
            int bestCluster = 0;
            for (int c = 0; c < k; c++) {
                double dist = Math.abs(data[i] - centroids[c]);
                if (dist < minDist) {
                    minDist = dist;
                    bestCluster = c;
                }
            }
            assignments[i] = bestCluster;
        }
        return assignments;
    }

    static void regression() {
        System.out.println("\n--- Regression ---");
        System.out.println("Predicting continuous numerical values");

        System.out.println("\nRegression Algorithms:");

        System.out.println("\n1. Linear Regression:");
        System.out.println("   LinearRegression model = new LinearRegression();");
        System.out.println("   model.buildClassifier(data);");
        System.out.println("   - Simple linear relationship");
        double[] lrCoeffs = {2.5, 0.8};
        System.out.printf("   Coefficients: intercept=%.2f, slope=%.2f%n", lrCoeffs[0], lrCoeffs[1]);

        double[] xValues = {1, 2, 3, 4, 5};
        for (double x : xValues) {
            double predicted = lrCoeffs[0] + lrCoeffs[1] * x;
            System.out.printf("   x=%.0f -> y=%.2f%n", x, predicted);
        }

        System.out.println("\n2. M5P (Decision Tree Regression):");
        System.out.println("   M5P m5p = new M5P();");
        System.out.println("   m5p.buildClassifier(data);");
        System.out.println("   - Tree-based regression");
        System.out.println("   - Handles non-linear relationships");

        System.out.println("\n3. k-Nearest Neighbors Regression:");
        System.out.println("   IBk knn = new IBk(3);");
        System.out.println("   knn.buildClassifier(data);");
        System.out.println("   - Instance-based regression");
        System.out.println("   - Smooth predictions");

        System.out.println("\n4. Gaussian Processes:");
        System.out.println("   GaussianProcesses gp = new GaussianProcesses();");
        System.out.println("   gp.buildClassifier(data);");
        System.out.println("   - Probabilistic approach");
        System.out.println("   - Uncertainty estimation");

        System.out.println("\nRegression Evaluation:");
        double mse = 2.35;
        double rmse = Math.sqrt(mse);
        double mae = 1.42;
        double r2 = 0.87;
        System.out.printf("   MSE: %.2f, RMSE: %.2f%n", mse, rmse);
        System.out.printf("   MAE: %.2f, R²: %.2f%n", mae, r2);
    }

    static void associationRules() {
        System.out.println("\n--- Association Rules ---");
        System.out.println("Finding patterns and relationships in data");

        System.out.println("\nApriori Algorithm:");
        System.out.println("   Apriori apriori = new Apriori();");
        System.out.println("   apriori.buildAssociations(data);");
        System.out.println("   - Finds frequent itemsets");
        System.out.println("   - Generates association rules");
        System.out.println("   - Uses minimum support threshold");

        System.out.println("\nParameters:");
        System.out.println("   - minSupport: Minimum occurrence (e.g., 0.1)");
        System.out.println("   - minConfidence: Minimum rule confidence (e.g., 0.8)");
        System.out.println("   - numRules: Number of rules to generate");

        System.out.println("\nExample Rules:");
        System.out.println("   {Bread} -> {Butter} (support: 0.15, confidence: 0.85)");
        System.out.println("   {Milk, Eggs} -> {Cheese} (support: 0.08, confidence: 0.92)");
        System.out.println("   {Diaper} -> {Beer} (support: 0.12, confidence: 0.78)");

        System.out.println("\nFrequent Itemsets:");
        String[] itemsets = {"{Bread, Butter}", "{Milk, Eggs}", "{Bread, Milk}", "{Diaper, Beer}"};
        int[] supports = {15, 10, 12, 8};
        for (int i = 0; i < itemsets.length; i++) {
            System.out.printf("   %s (support: %d%%)%n", itemsets[i], supports[i]);
        }

        System.out.println("\nMarket Basket Analysis:");
        System.out.println("   - Understand customer behavior");
        System.out.println("   - Product placement optimization");
        System.out.println("   - Promotional strategies");
    }

    static void ensembleMethods() {
        System.out.println("\n--- Ensemble Methods ---");
        System.out.println("Combining multiple models for better predictions");

        System.out.println("\nEnsemble Techniques:");

        System.out.println("\n1. Bagging (Bootstrap Aggregating):");
        System.out.println("   Bagging bagging = new Bagging();");
        System.out.println("   bagging.setClassifier(new J48());");
        System.out.println("   bagging.setNumIterations(10);");
        System.out.println("   bagging.buildClassifier(data);");
        System.out.println("   - Reduces variance");
        System.out.println("   - Parallel training");
        double[] baggingAccuracy = {96.2};
        System.out.printf("   Accuracy: %.1f%%%n", baggingAccuracy[0]);

        System.out.println("\n2. Boosting (AdaBoost):");
        System.out.println("   AdaBoostM1 boosting = new AdaBoostM1();");
        System.out.println("   boosting.setClassifier(new DecisionStump());");
        System.out.println("   boosting.setNumIterations(10);");
        System.out.println("   boosting.buildClassifier(data);");
        System.out.println("   - Reduces bias");
        System.out.println("   - Sequential training");
        double[] boostingAccuracy = {95.8};
        System.out.printf("   Accuracy: %.1f%%%n", boostingAccuracy[0]);

        System.out.println("\n3. Random Forest:");
        System.out.println("   RandomForest rf = new RandomForest();");
        System.out.println("   rf.setNumTrees(100);");
        System.out.println("   rf.buildClassifier(data);");
        System.out.println("   - Ensemble of decision trees");
        System.out.println("   - Feature randomness");
        double[] rfAccuracy = {97.1};
        System.out.printf("   Accuracy: %.1f%%%n", rfAccuracy[0]);

        System.out.println("\n4. Stacking:");
        System.out.println("   Stacking stacking = new Stacking();");
        System.out.println("   stacking.setClassifiers(new Classifier[]{nb, knn, rf});");
        System.out.println("   stacking.setMetaClassifier(new LogisticRegression());");
        System.out.println("   - Meta-learner approach");
        System.out.println("   - Learns combination");
    }

    static void featureSelection() {
        System.out.println("\n--- Feature Selection ---");
        System.out.println("Identifying the most relevant features for prediction");

        System.out.println("\nFeature Selection Methods:");

        System.out.println("\n1. Wrapper Methods:");
        System.out.println("   - Evaluates subsets with actual classifier");
        System.out.println("   - More accurate but slower");
        System.out.println("   - Greedy search (forward, backward)");
        System.out.println("   BestSubsetEvaluator");

        System.out.println("\n2. Filter Methods:");
        System.out.println("   - Independent of classifier");
        System.out.println("   - Faster, less overfitting");
        System.out.println("   - Correlation, chi-squared, info gain");

        System.out.println("\n3. Embedded Methods:");
        System.out.println("   - Built into classifiers");
        System.out.println("   - L1 regularization (Lasso)");
        System.out.println("   - Tree-based feature importance");

        System.out.println("\nExample Search Methods:");
        String[] searchMethods = {
            "GreedyStepwise: Forward/Backward search",
            "BestFirst: Explores search space",
            "Ranker: Ranks features individually",
            "Random: Random subset search"
        };
        for (String method : searchMethods) {
            System.out.println("   - " + method);
        }

        System.out.println("\nRanked Features:");
        Map<String, Double> featureScores = new LinkedHashMap<>();
        featureScores.put("Temperature", 0.95);
        featureScores.put("Humidity", 0.87);
        featureScores.put("Outlook", 0.72);
        featureScores.put("Wind", 0.45);
        System.out.println("   " + featureScores);
    }

    static void modelEvaluation() {
        System.out.println("\n--- Model Evaluation ---");
        System.out.println("Assessing model performance and generalization");

        System.out.println("\nEvaluation Methods:");

        System.out.println("\n1. Train-Test Split:");
        System.out.println("   data.randomize(new Random(42));");
        System.out.println("   int trainSize = (int) Math.round(data.numInstances() * 0.8);");
        System.out.println("   int testSize = data.numInstances() - trainSize;");
        System.out.println("   Instances train = data.trainCV(5, 0);");
        System.out.println("   Instances test = data.testCV(5, 0);");

        System.out.println("\n2. Cross-Validation:");
        System.out.println("   Evaluation eval = new Evaluation(data);");
        System.out.println("   eval.crossValidateModel(classifier, data, 10, new Random(42));");
        System.out.println("   - 10-fold is standard");
        System.out.println("   - More reliable than single split");

        System.out.println("\nMetrics:");
        Map<String, Double> metrics = new LinkedHashMap<>();
        metrics.put("Accuracy", 0.925);
        metrics.put("Precision", 0.918);
        metrics.put("Recall", 0.912);
        metrics.put("F1-Score", 0.915);
        metrics.put("AUC", 0.961);
        System.out.println("   " + metrics);

        System.out.println("\nConfusion Matrix:");
        System.out.println("           Predicted");
        System.out.println("           Yes   No");
        System.out.println("   Actual Yes  85   7");
        System.out.println("          No   12  96");

        System.out.println("\nEvaluation Output:");
        System.out.println("   System.out.println(eval.toSummaryString());");
        System.out.println("   System.out.println(eval.toMatrixString());");
        System.out.println("   System.out.println(eval.toClassDetailsString());");
    }

    static void preprocessing() {
        System.out.println("\n--- Data Preprocessing ---");
        System.out.println("Transforming data for better model performance");

        System.out.println("\nPreprocessing Filters:");

        System.out.println("\n1. Discretization:");
        System.out.println("   Discretize filter = new Discretize();");
        System.out.println("   filter.setInputFormat(data);");
        System.out.println("   Instances filtered = Filter.useFilter(data, filter);");
        System.out.println("   - Continuous to categorical");
        System.out.println("   - Equal width or frequency");

        System.out.println("\n2. Normalization:");
        System.out.println("   Normalize filter = new Normalize();");
        System.out.println("   filter.setInputFormat(data);");
        System.out.println("   - Scale to [0,1] or z-scores");
        System.out.println("   - Important for distance-based models");

        System.out.println("\n3. Standardization:");
        System.out.println("   Standardize filter = new Standardize();");
        System.out.println("   filter.setInputFormat(data);");
        System.out.println("   - Zero mean, unit variance");
        System.out.println("   - Gaussian assumption");

        System.out.println("\n4. Attribute Selection:");
        System.out.println("   AttributeSelection filter = new AttributeSelection();");
        System.out.println("   filter.setEvaluator(new InfoGainAttributeEval());");
        System.out.println("   filter.setSearch(new Ranker());");
        System.out.println("   - Remove irrelevant attributes");
        System.out.println("   - Reduce dimensionality");

        System.out.println("\n5. Missing Value Handling:");
        System.out.println("   ReplaceMissingValues filter = new ReplaceMissingValues();");
        System.out.println("   filter.setInputFormat(data);");
        System.out.println("   - Mean/mode imputation");
        System.out.println("   - Delete incomplete instances");

        System.out.println("\n6. Resampling:");
        System.out.println("   Resample filter = new Resample();");
        System.out.println("   filter.setBiasToUniform(true);");
        System.out.println("   - Handle class imbalance");
        System.out.println("   - Random sampling with replacement");
    }

    static void modelPersistence() {
        System.out.println("\n--- Model Persistence ---");
        System.out.println("Saving and loading trained models");

        System.out.println("\nSerialization:");
        System.out.println("   // Save model");
        System.out.println("   SerializationHelper.write(\"model.model\", classifier);");
        System.out.println("   ");
        System.out.println("   // Load model");
        System.out.println("   Classifier loaded = (Classifier) SerializationHelper.read(\"model.model\");");

        System.out.println("\nModel Loading:");
        System.out.println("   // From file");
        System.out.println("   File modelFile = new File(\"j48-model.model\");");
        System.out.println("   J48 loadedTree = (J48) weka.core.SerializationHelper.read(modelFile);");
        System.out.println("   ");
        System.out.println("   // Make predictions");
        System.out.println("   double prediction = loadedTree.classifyInstance(testInstance);");

        System.out.println("\nModel Export:");
        System.out.println("   // Save as text");
        System.out.println("   String treeText = tree.toString();");
        System.out.println("   Files.writeString(Path.of(\"tree.txt\"), treeText);");

        System.out.println("\nVersion Compatibility:");
        System.out.println("   - Save Weka version with model");
        System.out.println("   - Check compatibility when loading");
        System.out.println("   - May need to retrain with new version");

        System.out.println("\nModel Deployment:");
        System.out.println("   - Load model in production");
        System.out.println("   - Preprocess new data");
        System.out.println("   - Classify and return predictions");
        System.out.println("   - Log predictions for monitoring");
    }
}