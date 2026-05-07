package com.learning.data.spark;

public class SparkLab {

    public static void main(String[] args) {
        System.out.println("=== Apache Spark Lab ===\n");

        System.out.println("1. Spark Transformations:");
        System.out.println("   map() - Transform each element");
        System.out.println("   filter() - Filter elements by condition");
        System.out.println("   flatMap() - One-to-many transformation");
        System.out.println("   reduce() - Aggregate elements");
        System.out.println("   groupByKey() - Group by key");
        System.out.println("   join() - Join two datasets");

        System.out.println("\n2. Spark Pipeline Example:");
        System.out.println("   SparkSession spark = SparkSession.builder().appName(\"Lab\").getOrCreate();");
        System.out.println("   Dataset<String> lines = spark.read().textFile(\"data.txt\");");
        System.out.println("   Dataset<Row> words = lines.flatMap((String line) -> Arrays.asList(line.split(\" \")));");
        System.out.println("   Dataset<Row> wordCounts = words.groupBy(\"value\").count();");

        System.out.println("\n=== Apache Spark Lab Complete ===");
    }
}