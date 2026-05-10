package com.learning.lab.module28;

public class Lab {
    public static void main(String[] args) {
        System.out.println("=== Module 28: CI/CD Pipeline Examples ===");

        pipelineStagesDemo();
        jenkinsfileDemo();
        githubActionsDemo();
        gradleCIConfig();
        dockerRegistryDemo();
    }

    static void pipelineStagesDemo() {
        System.out.println("\n--- CI/CD Pipeline Stages ---");
        System.out.println("1. Source - Commit code to VCS");
        System.out.println("2. Build - Compile source code");
        System.out.println("3. Test - Run unit/integration tests");
        System.out.println("4. Analyze - Static code analysis");
        System.out.println("5. Package - Create JAR/Docker image");
        System.out.println("6. Deploy - Deploy to environment");
        System.out.println("7. Monitor - Health checks & logs");
    }

    static void jenkinsfileDemo() {
        System.out.println("\n--- Jenkinsfile Example ---");
        String jenkinsfile = "pipeline {\n" +
                "    agent any\n" +
                "    stages {\n" +
                "        stage('Build') {\n" +
                "            steps {\n" +
                "                sh 'mvn clean package'\n" +
                "            }\n" +
                "        }\n" +
                "        stage('Test') {\n" +
                "            steps {\n" +
                "                sh 'mvn test'\n" +
                "            }\n" +
                "        }\n" +
                "        stage('Deploy') {\n" +
                "            when { branch 'main' }\n" +
                "            steps {\n" +
                "                sh 'kubectl apply -f deployment.yaml'\n" +
                "            }\n" +
                "        }\n" +
                "    }\n" +
                "    post {\n" +
                "        always { cleanWs() }\n" +
                "        success { echo 'Pipeline succeeded!' }\n" +
                "        failure { echo 'Pipeline failed!' }\n" +
                "    }\n" +
                "}";
        System.out.println(jenkinsfile);
    }

    static void githubActionsDemo() {
        System.out.println("\n--- GitHub Actions Example ---");
        String workflow = "name: Java CI\n" +
                "on: [push, pull_request]\n" +
                "jobs:\n" +
                "  build:\n" +
                "    runs-on: ubuntu-latest\n" +
                "    steps:\n" +
                "      - uses: actions/checkout@v3\n" +
                "      - name: Set up JDK 17\n" +
                "        uses: actions/setup-java@v3\n" +
                "        with:\n" +
                "          java-version: '17'\n" +
                "      - name: Build with Maven\n" +
                "        run: mvn clean package\n" +
                "      - name: Run tests\n" +
                "        run: mvn test\n" +
                "      - name: Upload artifacts\n" +
                "        uses: actions/upload-artifact@v3\n" +
                "        with:\n" +
                "          name: target\n" +
                "          path: target/*.jar";
        System.out.println(workflow);
    }

    static void gradleCIConfig() {
        System.out.println("\n--- Gradle CI Configuration ---");
        System.out.println("tasks.withType(Test) {");
        System.out.println("    testLogging {");
        System.out.println("        events 'passed', 'skipped', 'failed'");
        System.out.println("        showStandardStreams = true");
        System.out.println("    }");
        System.out.println("    maxParallelForks = 4");
        System.out.println("}");
        System.out.println("\njacocoTestReport {");
        System.out.println("    executionData file('jacoco.exec')");
        System.out.println("}");
    }

    static void dockerRegistryDemo() {
        System.out.println("\n--- Docker Registry Integration ---");
        System.out.println("Build: docker build -t myapp:latest .");
        System.out.println("Tag: docker tag myapp:latest registry.com/myapp:latest");
        System.out.println("Push: docker push registry.com/myapp:latest");
        System.out.println("Pull: docker pull registry.com/myapp:latest");
    }
}