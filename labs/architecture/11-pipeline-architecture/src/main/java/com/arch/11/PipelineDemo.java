package com.arch.pipeline;

public class PipelineDemo {
    public static void main(String[] args) {
        Pipeline<String, String> pipeline = new Pipeline<>();

        pipeline
            .addStage((Stage<String, String>) input -> input.trim())
            .addStage((Stage<String, String>) input -> input.toUpperCase())
            .addStage((Stage<String, String>) input -> "[" + input + "]");

        String result = pipeline.execute("  hello pipeline  ");
        System.out.println("Pipeline result: " + result);
    }
}
