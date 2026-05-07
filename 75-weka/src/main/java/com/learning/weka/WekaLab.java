package com.learning.weka;

public class WekaLab {

    public static void main(String[] args) throws Exception {
        System.out.println("=== Weka Machine Learning Lab ===\n");

        System.out.println("1. Loading Data:");
        System.out.println("   DataSource source = new DataSource(\"weather.arff\");");
        System.out.println("   Instances data = source.getDataSet();");
        System.out.println("   data.setClassIndex(data.numAttributes() - 1);");
        System.out.println("   System.out.println(\"Instances: \" + data.numInstances());");
        System.out.println("   System.out.println(\"Attributes: \" + data.numAttributes());");

        System.out.println("\n2. Classifiers:");
        System.out.println("   // J48 Decision Tree");
        System.out.println("   Classifier j48 = new J48();");
        System.out.println("   j48.buildClassifier(data);");
        System.out.println("   System.out.println(j48);");
        System.out.println("");
        System.out.println("   // Random Forest");
        System.out.println("   RandomForest rf = new RandomForest();");
        System.out.println("   rf.setNumIterations(100);");
        System.out.println("   rf.buildClassifier(data);");
        System.out.println("");
        System.out.println("   // SVM (SMO)");
        System.out.println("   SMO smo = new SMO();");
        System.out.println("   smo.setC(1.0);");
        System.out.println("   smo.buildClassifier(data);");

        System.out.println("\n3. Cross-Validation:");
        System.out.println("   Evaluation eval = new Evaluation(data);");
        System.out.println("   eval.crossValidateModel(classifier, data, 10, new Random(1));");
        System.out.println("   System.out.println(\"Accuracy: \" + eval.pctCorrect());");
        System.out.println("   System.out.println(\"Precision: \" + eval.weightedPrecision());");
        System.out.println("   System.out.println(\"Recall: \" + eval.weightedRecall());");
        System.out.println("   System.out.println(\"F-Measure: \" + eval.weightedFMeasure());");
        System.out.println("   System.out.println(\"Confusion Matrix:\");");
        System.out.println("   double[][] matrix = eval.confusionMatrix();");

        System.out.println("\n4. Preprocessing (Filters):");
        System.out.println("   // Normalize data");
        System.out.println("   Normalize normalize = new Normalize();");
        System.out.println("   normalize.setInputFormat(data);");
        System.out.println("   Instances normalized = Filter.useFilter(data, normalize);");
        System.out.println("");
        System.out.println("   // Discretize numeric attributes");
        System.out.println("   Discretize discretize = new Discretize();");
        System.out.println("   discretize.setBins(10);");
        System.out.println("   discretize.setInputFormat(data);");
        System.out.println("   Instances discretized = Filter.useFilter(data, discretize);");
        System.out.println("");
        System.out.println("   // Remove attributes");
        System.out.println("   Remove remove = new Remove();");
        System.out.println("   remove.setAttributeIndices(\"1,3\");");

        System.out.println("\n5. Clustering:");
        System.out.println("   SimpleKMeans kmeans = new SimpleKMeans();");
        System.out.println("   kmeans.setNumClusters(3);");
        System.out.println("   kmeans.setSeed(42);");
        System.out.println("   kmeans.buildClusterer(data);");
        System.out.println("   int[] assignments = kmeans.getAssignments();");

        System.out.println("\n6. Association Rules:");
        System.out.println("   Apriori apriori = new Apriori();");
        System.out.println("   apriori.setMinMetric(0.8);");
        System.out.println("   apriori.buildAssociations(data);");

        System.out.println("\n7. Attribute Selection:");
        System.out.println("   AttributeSelection selector = new AttributeSelection();");
        System.out.println("   CfsSubsetEval eval = new CfsSubsetEval();");
        System.out.println("   GreedyStepwise search = new GreedyStepwise();");
        System.out.println("   selector.setEvaluator(eval);");
        System.out.println("   selector.setSearch(search);");
        System.out.println("   selector.SelectAttributes(data);");
        System.out.println("   int[] selected = selector.selectedAttributes();");

        System.out.println("\n=== Weka Lab Complete ===");
    }
}