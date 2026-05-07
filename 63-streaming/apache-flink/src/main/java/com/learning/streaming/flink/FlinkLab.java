package com.learning.streaming.flink;

public class FlinkLab {

    public static void main(String[] args) {
        System.out.println("=== Apache Flink Lab ===\n");

        System.out.println("1. Flink Streaming Example:");
        System.out.println("   StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();");
        System.out.println("   DataStream<String> stream = env.socketTextStream(\"localhost\", 9999);");
        System.out.println("   DataStream<WordCount> counts = stream");
        System.out.println("       .flatMap(new Tokenizer())");
        System.out.println("       .keyBy(value -> value.f0)");
        System.out.println("       .window(TumblingProcessingTimeWindows.of(Time.seconds(5)))");
        System.out.println("       .sum(1);");

        System.out.println("\n2. Window Types:");
        System.out.println("   - Tumbling Window: Fixed size, non-overlapping");
        System.out.println("   - Sliding Window: Fixed size, overlapping");
        System.out.println("   - Session Window: Activity gap based");
        System.out.println("   - Global Window: All elements, needs trigger");

        System.out.println("\n=== Apache Flink Lab Complete ===");
    }
}