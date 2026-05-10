package com.learning.lab.module70;

import java.util.*;

public class Lab {
    public static void main(String[] args) {
        System.out.println("=".repeat(60));
        System.out.println("Module 70: AI Fundamentals, Search Algorithms & ML Intro");
        System.out.println("=".repeat(60));

        aiOverview();
        searchAlgorithms();
        informedSearch();
        aStarSearch();
        machineLearningIntro();
        supervisedLearning();
        unsupervisedLearning();
        neuralNetworkBasics();
        modelEvaluation();
        featureEngineering();

        System.out.println("\n" + "=".repeat(60));
        System.out.println("Module 70 Lab Complete!");
        System.out.println("=".repeat(60));
    }

    static void aiOverview() {
        System.out.println("\n--- AI Overview ---");
        System.out.println("Artificial Intelligence (AI) enables machines to:");
        System.out.println("  - Learn from experience");
        System.out.println("  - Adapt to new inputs");
        System.out.println("  - Perform human-like tasks");

        System.out.println("\nAI Categories:");
        System.out.println("  1. Narrow AI: Specific tasks (current state)");
        System.out.println("  2. General AI: Human-level intelligence (theoretical)");
        System.out.println("  3. Superintelligent AI: Beyond human (hypothetical)");

        System.out.println("\nCore AI Techniques:");
        String[] techniques = {"Machine Learning", "Deep Learning", "NLP", "Computer Vision", "Reinforcement Learning"};
        for (String tech : techniques) {
            System.out.println("  - " + tech);
        }
    }

    static void searchAlgorithms() {
        System.out.println("\n--- Uninformed Search Algorithms ---");

        System.out.println("\n1. Breadth-First Search (BFS):");
        System.out.println("   - Explores all nodes at current depth first");
        System.out.println("   - Uses queue data structure");
        System.out.println("   - Guarantees shortest path in unweighted graphs");
        System.out.println("   - Time: O(V+E), Space: O(V)");

        Map<String, List<String>> graph = new HashMap<>();
        graph.put("A", Arrays.asList("B", "C"));
        graph.put("B", Arrays.asList("D", "E"));
        graph.put("C", Arrays.asList("F"));
        graph.put("D", Arrays.asList());
        graph.put("E", Arrays.asList("G"));
        graph.put("F", Arrays.asList());
        graph.put("G", Arrays.asList());

        List<String> bfsResult = bfs(graph, "A", "G");
        System.out.println("   BFS Path from A to G: " + bfsResult);

        System.out.println("\n2. Depth-First Search (DFS):");
        System.out.println("   - Explores as far as possible along each branch");
        System.out.println("   - Uses stack (or recursion)");
        System.out.println("   - Lower memory than BFS");
        System.out.println("   - Time: O(V+E), Space: O(V)");

        List<String> dfsResult = dfs(graph, "A", "G");
        System.out.println("   DFS Path from A to G: " + dfsResult);

        System.out.println("\n3. Uniform-Cost Search:");
        System.out.println("   - Expands lowest cost node first");
        System.out.println("   - Like Dijkstra's algorithm");
        System.out.println("   - Optimal for weighted graphs");
    }

    static List<String> bfs(Map<String, List<String>> graph, String start, String goal) {
        Queue<String> queue = new LinkedList<>();
        Set<String> visited = new HashSet<>();
        Map<String, String> parent = new HashMap<>();

        queue.add(start);
        visited.add(start);
        parent.put(start, null);

        while (!queue.isEmpty()) {
            String current = queue.poll();
            if (current.equals(goal)) {
                return reconstructPath(parent, goal);
            }

            for (String neighbor : graph.getOrDefault(current, Collections.emptyList())) {
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    parent.put(neighbor, current);
                    queue.add(neighbor);
                }
            }
        }
        return Collections.emptyList();
    }

    static List<String> dfs(Map<String, List<String>> graph, String start, String goal) {
        Set<String> visited = new HashSet<>();
        Map<String, String> parent = new HashMap<>();
        boolean[] found = {false};

        dfsHelper(graph, start, goal, visited, parent, found);
        return found[0] ? reconstructPath(parent, goal) : Collections.emptyList();
    }

    static void dfsHelper(Map<String, List<String>> graph, String current, String goal,
                          Set<String> visited, Map<String, String> parent, boolean[] found) {
        if (found[0] || visited.contains(current)) return;

        visited.add(current);
        if (current.equals(goal)) {
            found[0] = true;
            return;
        }

        for (String neighbor : graph.getOrDefault(current, Collections.emptyList())) {
            if (!visited.contains(neighbor)) {
                parent.put(neighbor, current);
                dfsHelper(graph, neighbor, goal, visited, parent, found);
            }
        }
    }

    static List<String> reconstructPath(Map<String, String> parent, String goal) {
        List<String> path = new ArrayList<>();
        String current = goal;
        while (current != null) {
            path.add(0, current);
            current = parent.get(current);
        }
        return path;
    }

    static void informedSearch() {
        System.out.println("\n--- Informed Search Algorithms ---");

        System.out.println("\n1. Greedy Best-First Search:");
        System.out.println("   - Expands node closest to goal (heuristic)");
        System.out.println("   - Not guaranteed optimal");
        System.out.println("   - Fast but may miss optimal solution");

        System.out.println("\n2. Heuristic Function h(n):");
        System.out.println("   - Estimates cost from node n to goal");
        System.out.println("   - Must be admissible (never overestimate)");
        System.out.println("   - Example: Straight-line distance");

        System.out.println("\nHeuristic Properties:");
        System.out.println("   - Admissible: h(n) <= actual cost");
        System.out.println("   - Consistent: h(n) <= cost(n->n') + h(n')");
    }

    static void aStarSearch() {
        System.out.println("\n--- A* Search Algorithm ---");
        System.out.println("A* combines Dijkstra and Greedy Best-First:");
        System.out.println("   f(n) = g(n) + h(n)");
        System.out.println("   g(n) = cost from start to n");
        System.out.println("   h(n) = heuristic estimate to goal");

        System.out.println("\nA* Properties:");
        System.out.println("   - Optimal if h(n) is admissible");
        System.out.println("   - Complete (finds solution if exists)");
        System.out.println("   - Time: O(b^d) where b=branch factor, d=depth");

        Map<String, Map<String, Integer>> graph = new HashMap<>();
        graph.put("A", Map.of("B", 4, "C", 3));
        graph.put("B", Map.of("D", 5, "E", 2));
        graph.put("C", Map.of("D", 2, "F", 4));
        graph.put("D", Map.of("G", 3));
        graph.put("E", Map.of("G", 5));
        graph.put("F", Map.of("G", 4));
        graph.put("G", Collections.emptyMap());

        Map<String, Integer> heuristics = Map.of("A", 6, "B", 4, "C", 5, "D", 3, "E", 3, "F", 2, "G", 0);

        List<String> aStarResult = aStar(graph, "A", "G", heuristics);
        System.out.println("   A* Path from A to G: " + aStarResult);
    }

    static List<String> aStar(Map<String, Map<String, Integer>> graph, String start, String goal,
                               Map<String, Integer> heuristics) {
        PriorityQueue<AStarNode> openSet = new PriorityQueue<>(Comparator.comparingInt(n -> n.f));
        Set<String> closedSet = new HashSet<>();
        Map<String, Integer> gScore = new HashMap<>();
        Map<String, String> parent = new HashMap<>();

        gScore.put(start, 0);
        openSet.add(new AStarNode(start, heuristics.getOrDefault(start, 0), heuristics.getOrDefault(start, 0)));

        while (!openSet.isEmpty()) {
            AStarNode current = openSet.poll();
            if (current.name.equals(goal)) {
                return reconstructPath(parent, goal);
            }

            closedSet.add(current.name);

            for (Map.Entry<String, Integer> neighbor : graph.getOrDefault(current.name, Collections.emptyMap()).entrySet()) {
                if (closedSet.contains(neighbor.getKey())) continue;

                int tentativeG = gScore.get(current.name) + neighbor.getValue();
                if (tentativeG < gScore.getOrDefault(neighbor.getKey(), Integer.MAX_VALUE)) {
                    parent.put(neighbor.getKey(), current.name);
                    gScore.put(neighbor.getKey(), tentativeG);
                    int h = heuristics.getOrDefault(neighbor.getKey(), 0);
                    openSet.add(new AStarNode(neighbor.getKey(), tentativeG, tentativeG + h));
                }
            }
        }
        return Collections.emptyList();
    }

    static class AStarNode {
        String name;
        int g;
        int f;
        AStarNode(String name, int g, int f) {
            this.name = name;
            this.g = g;
            this.f = f;
        }
    }

    static void machineLearningIntro() {
        System.out.println("\n--- Machine Learning Introduction ---");
        System.out.println("ML enables computers to learn from data without explicit programming");

        System.out.println("\nML Categories:");
        System.out.println("1. Supervised Learning: Learn from labeled data");
        System.out.println("2. Unsupervised Learning: Find patterns in unlabeled data");
        System.out.println("3. Reinforcement Learning: Learn through rewards/penalties");
        System.out.println("4. Semi-supervised Learning: Mix of labeled and unlabeled");
        System.out.println("5. Self-supervised Learning: Generate labels from data");

        System.out.println("\nCommon ML Workflow:");
        String[] steps = {
            "1. Data Collection", "2. Data Preprocessing", "3. Feature Engineering",
            "4. Model Selection", "5. Training", "6. Evaluation", "7. Hyperparameter Tuning",
            "8. Deployment"
        };
        for (String step : steps) {
            System.out.println("   " + step);
        }
    }

    static void supervisedLearning() {
        System.out.println("\n--- Supervised Learning ---");
        System.out.println("Learning from labeled training data to make predictions");

        System.out.println("\nClassification (Discrete labels):");
        System.out.println("   - Binary Classification: Spam detection, fraud detection");
        System.out.println("   - Multi-class Classification: Image recognition, document categorization");
        System.out.println("   - Algorithms: Logistic Regression, Decision Trees, SVM, Neural Networks");

        System.out.println("\nRegression (Continuous values):");
        System.out.println("   - Price prediction, temperature forecasting");
        System.out.println("   - Algorithms: Linear Regression, Random Forest, Gradient Boosting");

        System.out.println("\nExample: Simple Linear Regression");
        double[] x = {1, 2, 3, 4, 5};
        double[] y = {2, 4, 5, 4, 5};
        double[] coeffs = linearRegression(x, y);
        System.out.printf("   y = %.2f + %.2f * x%n", coeffs[0], coeffs[1]);
        System.out.printf("   Prediction for x=6: %.2f%n", coeffs[0] + coeffs[1] * 6);
    }

    static double[] linearRegression(double[] x, double[] y) {
        int n = x.length;
        double sumX = 0, sumY = 0, sumXY = 0, sumX2 = 0;
        for (int i = 0; i < n; i++) {
            sumX += x[i];
            sumY += y[i];
            sumXY += x[i] * y[i];
            sumX2 += x[i] * x[i];
        }
        double slope = (n * sumXY - sumX * sumY) / (n * sumX2 - sumX * sumX);
        double intercept = (sumY - slope * sumX) / n;
        return new double[]{intercept, slope};
    }

    static void unsupervisedLearning() {
        System.out.println("\n--- Unsupervised Learning ---");
        System.out.println("Finding hidden patterns in unlabeled data");

        System.out.println("\nClustering (Group similar data):");
        System.out.println("   - K-Means: Partition into k clusters");
        System.out.println("   - Hierarchical: Build tree of clusters");
        System.out.println("   - DBSCAN: Density-based clustering");

        System.out.println("\nDimensionality Reduction:");
        System.out.println("   - PCA: Principal Component Analysis");
        System.out.println("   - t-SNE: Non-linear embedding");
        System.out.println("   - UMAP: Uniform Manifold Approximation");

        System.out.println("\nAssociation Rules:");
        System.out.println("   - Market basket analysis");
        System.out.println("   - Apriori algorithm, FP-Growth");

        System.out.println("\nExample: K-Means Clustering Simulation");
        double[][] points = {{1,1}, {1.5,2}, {3,3}, {5,5}, {5.5,5.5}, {8,8}};
        int[][] assignments = kMeansSimple(points, 2, 10);
        System.out.println("   Cluster assignments: " + Arrays.deepToString(assignments));
    }

    static int[][] kMeansSimple(double[][] points, int k, int maxIterations) {
        double[] centroids = new double[k * 2];
        centroids[0] = points[0][0];
        centroids[1] = points[0][1];
        centroids[2] = points[points.length-1][0];
        centroids[3] = points[points.length-1][1];

        int[] assignments = new int[points.length];
        for (int iter = 0; iter < maxIterations; iter++) {
            for (int i = 0; i < points.length; i++) {
                double minDist = Double.MAX_VALUE;
                for (int c = 0; c < k; c++) {
                    double dist = Math.sqrt(Math.pow(points[i][0] - centroids[c*2], 2) +
                                           Math.pow(points[i][1] - centroids[c*2+1], 2));
                    if (dist < minDist) {
                        minDist = dist;
                        assignments[i] = c;
                    }
                }
            }
        }
        return new int[][]{assignments, new double[]{centroids[0], centroids[1], centroids[2], centroids[3]}};
    }

    static void neuralNetworkBasics() {
        System.out.println("\n--- Neural Network Basics ---");
        System.out.println("Inspired by biological neural networks in the brain");

        System.out.println("\nKey Components:");
        System.out.println("   - Neurons: Basic computational units");
        System.out.println("   - Weights: Connection strengths between neurons");
        System.out.println("   - Biases: Additional parameters for learning");
        System.out.println("   - Activation Functions: Introduce non-linearity");

        System.out.println("\nActivation Functions:");
        System.out.println("   - Sigmoid: σ(x) = 1/(1+e^-x) - outputs 0-1");
        System.out.println("   - ReLU: max(0,x) - most popular, fast");
        System.out.println("   - Tanh: (e^x - e^-x)/(e^x + e^-x) - outputs -1 to 1");
        System.out.println("   - Softmax: exp(x_i)/Σexp(x_j) - for multi-class");

        System.out.println("\nSimple Perceptron (Single Neuron):");
        System.out.println("   output = activation(Σ(w_i * x_i) + b)");
        double[] weights = {0.5, 0.3};
        double bias = 0.1;
        double[] inputs = {1.0, 0.5};
        double sum = 0;
        for (int i = 0; i < weights.length; i++) {
            sum += weights[i] * inputs[i];
        }
        double output = sigmoid(sum + bias);
        System.out.printf("   Input: %s, Output (sigmoid): %.4f%n", Arrays.toString(inputs), output);
    }

    static double sigmoid(double x) {
        return 1.0 / (1.0 + Math.exp(-x));
    }

    static void modelEvaluation() {
        System.out.println("\n--- Model Evaluation Metrics ---");

        System.out.println("\nClassification Metrics:");
        System.out.println("   - Accuracy: (TP + TN) / (TP + TN + FP + FN)");
        System.out.println("   - Precision: TP / (TP + FP)");
        System.out.println("   - Recall: TP / (TP + FN)");
        System.out.println("   - F1 Score: 2 * (Precision * Recall) / (Precision + Recall)");

        int tp = 85, tn = 90, fp = 10, fn = 15;
        double accuracy = (tp + tn) / (double)(tp + tn + fp + fn);
        double precision = tp / (double)(tp + fp);
        double recall = tp / (double)(tp + fn);
        double f1 = 2 * precision * recall / (precision + recall);

        System.out.printf("   Example (TP=%d, TN=%d, FP=%d, FN=%d)%n", tp, tn, fp, fn);
        System.out.printf("   Accuracy: %.2f, Precision: %.2f, Recall: %.2f, F1: %.2f%n",
                         accuracy, precision, recall, f1);

        System.out.println("\nRegression Metrics:");
        System.out.println("   - MSE: Mean Squared Error");
        System.out.println("   - RMSE: Root Mean Squared Error");
        System.out.println("   - MAE: Mean Absolute Error");
        System.out.println("   - R² Score: Coefficient of determination");
    }

    static void featureEngineering() {
        System.out.println("\n--- Feature Engineering ---");
        System.out.println("Transforming raw data into model-friendly features");

        System.out.println("\nFeature Types:");
        System.out.println("   - Numerical: Continuous or discrete values");
        System.out.println("   - Categorical: Finite set of categories");
        System.out.println("   - Temporal: Time-based features");
        System.out.println("   - Text: Natural language data");

        System.out.println("\nCommon Techniques:");
        System.out.println("   - Scaling: Normalization, Standardization");
        System.out.println("   - Encoding: One-hot, Label encoding, Target encoding");
        System.out.println("   - Binning: Convert continuous to discrete");
        System.out.println("   - Feature Selection: Remove irrelevant features");
        System.out.println("   - Feature Extraction: PCA, Autoencoders");

        System.out.println("\nExample: Standardization");
        double[] values = {10, 20, 30, 40, 50};
        double mean = Arrays.stream(values).average().orElse(0);
        double std = Math.sqrt(Arrays.stream(values).map(v -> Math.pow(v - mean, 2)).average().orElse(0));
        double[] standardized = Arrays.stream(values).map(v -> (v - mean) / std).toArray();
        System.out.printf("   Original: %s%n", Arrays.toString(values));
        System.out.printf("   Standardized: %.2f%n", standardized);
    }
}