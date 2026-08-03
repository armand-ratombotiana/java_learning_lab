# Problem Walkthrough: Decision Trees & Random Forests

## Problem 1: Airbnb Booking-Conversion Tree — Company: Airbnb

### Interview Scenario
"You're at Airbnb. The growth team wants a transparent model explaining why some
listings convert to bookings and others don't. They need something they can read
aloud in a review — 'listings with low price tier and wifi get booked' — before
any black-box ranking model. You have four categorical signals per listing:
price tier, distance from downtown, wifi, and pool."

### The Problem
Build an ID3 decision tree over the categorical listing features. It must:
(1) Compute entropy and information gain per feature, (2) Choose the split with
maximum gain recursively, (3) Stop at pure nodes and fall back to majority class,
(4) Predict for a brand-new listing, (5) Fix the lab demo's known predictor
bug — its `predict` reads `sample[0]` for every split — so predictions actually
follow the tree structure.

### Solution Walkthrough
- Step 1: Encode 14 labeled listings as `String[][] data` with `featNames`
  {"price_tier", "distance", "wifi", "pool"} and binary labels — the play-tennis
  layout renamed to Airbnb features.
- Step 2: `entropy(labels)` — the lab's base-2 entropy — gives the parent
  impurity before any split.
- Step 3: `infoGain(data, labels, feat)` for each unused feature; `buildTree`
  picks the max-gain feature. Root: `price_tier`.
- Step 4: Recurse per feature value with a fresh `used.clone()` mask; pure nodes
  become leaves, exhausted features fall back to `majority(labels)`.
- Step 5: Fix prediction — the lab's `predict` uses `sample[0]` as the value for
  every node; build a `Map<String, Integer> col` from feature name to column and
  route with `sample[col.get(node.label)]`.
- Step 6: Score the training set (14/14) and predict the new listing.

### Code
```java
package com.ml.lab03;

import java.util.*;

/**
 * Airbnb-style booking-conversion decision tree (ID3).
 * <p>
 * Builds the tree with entropy / information gain exactly like Lab 03,
 * but fixes the lab's simplified predict() — which always reads
 * sample[0] — by mapping each node's feature name to its column index.
 */
public class BookingTree {

    static class TreeNode {
        String label;
        Map<String, TreeNode> children = new HashMap<>();
        String leafClass;
        boolean isLeaf;

        TreeNode(String label) {
            this.label = label;
        }
    }

    public static double entropy(String[] labels) {
        Map<String, Integer> counts = new HashMap<>();
        for (String l : labels) counts.merge(l, 1, Integer::sum);
        double e = 0.0;
        int n = labels.length;
        for (int c : counts.values()) {
            double p = (double) c / n;
            if (p > 0) e -= p * (Math.log(p) / Math.log(2));
        }
        return e;
    }

    public static double infoGain(String[][] data, String[] labels, int feat) {
        Map<String, Integer> valCount = new HashMap<>();
        for (String[] row : data) valCount.merge(row[feat], 1, Integer::sum);

        double parentE = entropy(labels);
        double childE = 0.0;
        int n = labels.length;

        for (String val : valCount.keySet()) {
            List<String> subLabels = new ArrayList<>();
            for (int i = 0; i < data.length; i++) {
                if (data[i][feat].equals(val)) subLabels.add(labels[i]);
            }
            String[] subArr = subLabels.toArray(new String[0]);
            childE += (double) subArr.length / n * entropy(subArr);
        }
        return parentE - childE;
    }

    public static TreeNode buildTree(String[][] data, String[] labels,
                                     String[] featNames, boolean[] used) {
        Set<String> uniqueLabels = new HashSet<>(Arrays.asList(labels));
        if (uniqueLabels.size() == 1) {
            TreeNode leaf = new TreeNode("leaf");
            leaf.isLeaf = true;
            leaf.leafClass = labels[0];
            return leaf;
        }

        int bestFeat = -1;
        double bestGain = -1;
        for (int f = 0; f < featNames.length; f++) {
            if (used[f]) continue;
            double g = infoGain(data, labels, f);
            if (g > bestGain) { bestGain = g; bestFeat = f; }
        }

        if (bestFeat == -1) {
            TreeNode leaf = new TreeNode("leaf");
            leaf.isLeaf = true;
            leaf.leafClass = majority(labels);
            return leaf;
        }

        TreeNode node = new TreeNode(featNames[bestFeat]);
        used[bestFeat] = true;

        Map<String, List<Integer>> partitions = new HashMap<>();
        for (int i = 0; i < data.length; i++) {
            partitions.computeIfAbsent(data[i][bestFeat], k -> new ArrayList<>()).add(i);
        }

        for (String val : partitions.keySet()) {
            List<Integer> idx = partitions.get(val);
            String[][] subData = idx.stream().map(i -> data[i]).toArray(String[][]::new);
            String[] subLabs = idx.stream().map(i -> labels[i]).toArray(String[]::new);
            node.children.put(val, buildTree(subData, subLabs, featNames, used.clone()));
        }
        return node;
    }

    public static String majority(String[] labels) {
        Map<String, Integer> cnt = new HashMap<>();
        for (String l : labels) cnt.merge(l, 1, Integer::sum);
        return Collections.max(cnt.entrySet(), Map.Entry.comparingByValue()).getKey();
    }

    // Fixed predictor: node.label names the feature; look up its column.
    public static String predict(TreeNode node, String[] sample, Map<String, Integer> col) {
        if (node.isLeaf) return node.leafClass;
        String val = sample[col.get(node.label)];
        TreeNode child = node.children.get(val);
        if (child == null) return "unknown";
        return predict(child, sample, col);
    }

    public static void main(String[] args) {
        System.out.println("=== Airbnb Booking-Conversion Tree ===");

        String[] featNames = {"price_tier", "distance", "wifi", "pool"};
        String[][] data = {
            {"low",   "near", "yes",  "yes"},
            {"low",   "near", "yes",  "no"},
            {"mid",   "near", "yes",  "yes"},
            {"high",  "mid",  "yes",  "yes"},
            {"high",  "far",  "no",   "yes"},
            {"high",  "far",  "no",   "no"},
            {"mid",   "far",  "no",   "no"},
            {"low",   "mid",  "yes",  "yes"},
            {"low",   "far",  "no",   "yes"},
            {"high",  "mid",  "no",   "yes"},
            {"low",   "mid",  "no",   "no"},
            {"mid",   "mid",  "yes",  "no"},
            {"mid",   "near", "no",   "yes"},
            {"high",  "mid",  "yes",  "no"}
        };
        String[] labels = {
            "no", "no", "yes", "yes", "yes", "no", "yes",
            "no", "yes", "yes", "yes", "yes", "yes", "no"
        };

        TreeNode root = buildTree(data, labels, featNames, new boolean[featNames.length]);
        System.out.println("Tree built. Root splits on: " + root.label);
        System.out.println("Children keys: " + root.children.keySet());

        Map<String, Integer> col = new HashMap<>();
        for (int i = 0; i < featNames.length; i++) col.put(featNames[i], i);

        int correct = 0;
        for (int i = 0; i < data.length; i++) {
            String pred = predict(root, data[i], col);
            if (pred.equals(labels[i])) correct++;
        }
        System.out.printf("Training accuracy = %d/%d = %.2f%n",
                correct, data.length, (double) correct / data.length);

        // New listing: low price tier, near, wifi yes, no pool
        String[] listing = {"low", "near", "yes", "no"};
        System.out.println("New listing (low/near/wifi=yes/pool=no) -> "
                + predict(root, listing, col));
    }
}
```

### Expected Output
```
=== Airbnb Booking-Conversion Tree ===
Tree built. Root splits on: price_tier
Children keys: [high, low, mid]
Training accuracy = 14/14 = 1.00
New listing (low/near/wifi=yes/pool=no) -> no
```

---

## Problem 2: Netflix Subscriber Churn Tree — Company: Netflix

### Interview Scenario
"You're at Netflix. The retention team wants to know which subscriber segments are
churn risks so the CRM can target saves. They have three categorical features —
plan tier, tenure length, and support-ticket volume — and want a rule set they can
hand to the campaign team."

### The Problem
Build a churn tree and: (1) Pick the root split by information gain, (2) Train
correctly with the feature-column predictor, (3) Report training accuracy,
(4) Classify a new subscriber with a short-tenure std plan.

### Solution Walkthrough
- Step 1: Data — 12 subscribers over `plan`, `tenure`, `tickets`, labeled
  churn yes/no.
- Step 2: `buildTree` with the lab's entropy / information-gain machinery;
  the root is `plan` because it delivers the biggest entropy drop.
- Step 3: Predict with the fixed column-map predictor.
- Step 4: Print training accuracy and the new subscriber's verdict.

### Code
```java
String[] featNames = {"plan", "tenure", "tickets"};
String[][] data = {
    {"basic", "short", "few"},  {"basic", "short", "many"},
    {"basic", "med",   "few"},  {"basic", "med",   "many"},
    {"std",   "short", "few"},  {"std",   "short", "many"},
    {"std",   "long",  "few"},  {"std",   "long",  "many"},
    {"prem",  "short", "few"},  {"prem",  "med",   "many"},
    {"prem",  "long",  "few"},  {"prem",  "long",  "many"}
};
String[] labels = {"yes", "yes", "yes", "yes", "yes", "no",
                   "yes", "no", "no", "no", "no", "no"};

BookingTree.TreeNode root = BookingTree.buildTree(
        data, labels, featNames, new boolean[featNames.length]);
System.out.println("Root splits on: " + root.label);

Map<String, Integer> col = new HashMap<>();
for (int i = 0; i < featNames.length; i++) col.put(featNames[i], i);

int correct = 0;
for (int i = 0; i < data.length; i++) {
    if (BookingTree.predict(root, data[i], col).equals(labels[i])) correct++;
}
System.out.printf("Training accuracy = %d/%d = %.2f%n",
        correct, data.length, (double) correct / data.length);

String[] sub = {"std", "short", "many"};
System.out.println("New subscriber (std/short/many tickets) -> "
        + BookingTree.predict(root, sub, col));
```

### Expected Output
```
Root splits on: plan
Training accuracy = 12/12 = 1.00
New subscriber (std/short/many tickets) -> no
```

---

## Problem 3: Query-Intent Feature Selection — Company: Google

### Interview Scenario
"You're at Google Search. Before building the intent classifier, you want to
verify that a candidate feature — whether the query contains a site name — beats
query length as a signal for navigational vs informational intent. Show the
entropy math."

### The Problem
Compute the impurity math and: (1) Report parent entropy, (2) Report information
gain for each candidate feature, (3) Confirm which feature ID3 would split on.

### Solution Walkthrough
- Step 1: 6 labeled queries over `query_len` and `has_site`.
- Step 2: `entropy(labels)` for the parent — 1.0000 for a 3/3 split.
- Step 3: `infoGain(data, labels, 0)` and `infoGain(data, labels, 1)` — the
  weighted child-entropy subtraction.
- Step 4: `has_site` wins with 0.4591 vs 0.0817, so the tree splits there first.

### Code
```java
String[] featNames = {"query_len", "has_site"};
String[][] data = {
    {"short", "yes"}, {"short", "no"}, {"long", "yes"},
    {"long", "no"},   {"short", "yes"}, {"long", "yes"}
};
String[] labels = {"nav", "info", "info", "info", "nav", "nav"};

double parent = BookingTree.entropy(labels);
System.out.printf("Parent entropy = %.4f%n", parent);
for (int f = 0; f < featNames.length; f++) {
    System.out.printf("InfoGain(%s) = %.4f%n",
            featNames[f], BookingTree.infoGain(data, labels, f));
}
String winner = BookingTree.infoGain(data, labels, 0)
        >= BookingTree.infoGain(data, labels, 1) ? "query_len" : "has_site";
System.out.println("ID3 picks: " + winner);
```

### Expected Output
```
Parent entropy = 1.0000
InfoGain(query_len) = 0.0817
InfoGain(has_site) = 0.4591
ID3 picks: has_site
```
