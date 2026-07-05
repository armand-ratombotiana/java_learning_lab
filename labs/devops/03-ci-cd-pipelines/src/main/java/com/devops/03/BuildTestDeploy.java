package com.devops.cicd;

public class BuildTestDeploy {
    public static class BuildStep {
        public void run() {
            System.out.println("BUILD: Compiling with Maven...");
            simulateWork("BUILD", true);
        }
    }

    public static class TestStep {
        public void run() {
            System.out.println("TEST: Executing JUnit tests...");
            simulateWork("TEST", true);
        }
    }

    public static class DeployStep {
        public void run() {
            System.out.println("DEPLOY: Pushing to production...");
            simulateWork("DEPLOY", true);
        }
    }

    private static void simulateWork(String step, boolean success) {
        try { Thread.sleep(500); } catch (InterruptedException e) { }
        if (success) {
            System.out.println(step + " completed successfully");
        } else {
            throw new RuntimeException(step + " failed");
        }
    }

    public static void main(String[] args) {
        BuildStep build = new BuildStep();
        TestStep test = new TestStep();
        DeployStep deploy = new DeployStep();

        build.run();
        test.run();
        deploy.run();
        System.out.println("CI/CD pipeline completed successfully");
    }
}
