package com.devops.cicd;

import java.util.ArrayList;
import java.util.List;

public class PipelineStages {
    private final List<Stage> stages = new ArrayList<>();

    public PipelineStages addStage(String name, Runnable action) {
        stages.add(new Stage(name, action));
        return this;
    }

    public void execute() {
        for (Stage stage : stages) {
            System.out.println("=== Stage: " + stage.name + " ===");
            try {
                stage.action.run();
                System.out.println("Stage '" + stage.name + "' PASSED\n");
            } catch (Exception e) {
                System.err.println("Stage '" + stage.name + "' FAILED: " + e.getMessage());
                break;
            }
        }
    }

    private static class Stage {
        final String name;
        final Runnable action;

        Stage(String name, Runnable action) {
            this.name = name;
            this.action = action;
        }
    }

    public static void main(String[] args) {
        new PipelineStages()
            .addStage("Build", () -> System.out.println("Compiling source code..."))
            .addStage("Test", () -> System.out.println("Running unit tests..."))
            .addStage("Package", () -> System.out.println("Creating artifact..."))
            .addStage("Deploy", () -> System.out.println("Deploying to staging..."))
            .execute();
    }
}
