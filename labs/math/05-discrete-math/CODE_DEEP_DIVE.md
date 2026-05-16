# Code Deep Dive: Discrete Math Implementations in Java

## 1. Propositional Logic

### 1.1 LogicEvaluator Class

```java
package com.mathacademy.discrete;

public class LogicEvaluator {
    
    public static boolean evaluateVariable(char variable, boolean p, boolean q, boolean r, boolean s) {
        switch (variable) {
            case 'p': return p;
            case 'q': return q;
            case 'r': return r;
            case 's': return s;
            default: throw new IllegalArgumentException("Unknown variable: " + variable);
        }
    }
    
    public static boolean NOT(boolean a) {
        return !a;
    }
    
    public static boolean AND(boolean a, boolean b) {
        return a && b;
    }
    
    public static boolean OR(boolean a, boolean b) {
        return a || b;
    }
    
    public static boolean XOR(boolean a, boolean b) {
        return a != b;
    }
    
    public static boolean NAND(boolean a, boolean b) {
        return !(a && b);
    }
    
    public static boolean NOR(boolean a, boolean b) {
        return !(a || b);
    }
    
    public static boolean IMPLIES(boolean a, boolean b) {
        return !a || b;
    }
    
    public static boolean IFF(boolean a, boolean b) {
        return a == b;
    }
    
    public static boolean evaluateExpression(String expr, boolean p, boolean q, boolean r) {
        expr = expr.replace(" ", "");
        
        for (int i = 0; i < expr.length(); i++) {
            char c = expr.charAt(i);
            if (c == 'p' || c == 'q' || c == 'r') {
                String var = String.valueOf(c);
                String replacement = evaluateVariable(c, p, q, r, false) ? "T" : "F";
                expr = expr.replaceFirst(var, replacement);
            }
        }
        
        return parseExpression(expr);
    }
    
    private static boolean parseExpression(String expr) {
        expr = expr.replace("T", "true").replace("F", "false");
        
        if (expr.contains("->")) {
            return evaluateImplication(expr);
        }
        
        if (expr.contains("<->")) {
            return evaluateIff(expr);
        }
        
        return evaluateBasic(expr);
    }
    
    private static boolean evaluateImplication(String expr) {
        String[] parts = expr.split("->");
        boolean antecedent = parseExpression(parts[0]);
        boolean consequent = parseExpression(parts[1]);
        return !antecedent || consequent;
    }
    
    private static boolean evaluateIff(String expr) {
        String[] parts = expr.split("<->");
        return parseExpression(parts[0]) == parseExpression(parts[1]);
    }
    
    private static boolean evaluateBasic(String expr) {
        if (expr.startsWith("!")) {
            return !parseExpression(expr.substring(1));
        }
        
        if (expr.contains("&&")) {
            String[] parts = expr.split("&&");
            boolean result = parseExpression(parts[0]);
            for (int i = 1; i < parts.length; i++) {
                result = result && parseExpression(parts[i]);
            }
            return result;
        }
        
        if (expr.contains("||")) {
            String[] parts = expr.split("\\|\\|");
            boolean result = parseExpression(parts[0]);
            for (int i = 1; i < parts.length; i++) {
                result = result || parseExpression(parts[i]);
            }
            return result;
        }
        
        return Boolean.parseBoolean(expr);
    }
}
```

### 1.2 TruthTableGenerator Class

```java
package com.mathacademy.discrete;

import java.util.ArrayList;
import java.util.List;

public class TruthTableGenerator {
    
    public static class TruthAssignment {
        public boolean[] values;
        
        public TruthAssignment(boolean[] values) {
            this.values = values;
        }
    }
    
    public static List<TruthAssignment> generateAssignments(int numVars) {
        List<TruthAssignment> assignments = new ArrayList<>();
        int numCombinations = 1 << numVars;
        
        for (int i = 0; i < numCombinations; i++) {
            boolean[] values = new boolean[numVars];
            for (int j = 0; j < numVars; j++) {
                values[j] = (i & (1 << (numVars - 1 - j))) != 0;
            }
            assignments.add(new TruthAssignment(values));
        }
        
        return assignments;
    }
    
    public static void printTruthTable(String expression) {
        int numVars = countVariables(expression);
        List<TruthAssignment> assignments = generateAssignments(numVars);
        
        System.out.print("p ");
        if (numVars > 1) System.out.print("q ");
        if (numVars > 2) System.out.print("r ");
        System.out.println("| " + expression);
        
        System.out.println("-".repeat(20));
        
        for (TruthAssignment assignment : assignments) {
            for (boolean val : assignment.values) {
                System.out.print(val ? "T " : "F ");
            }
            System.out.print("| ");
            
            boolean result = evaluateWithAssignment(expression, assignment.values);
            System.out.println(result ? "T" : "F");
        }
    }
    
    private static int countVariables(String expr) {
        int maxVar = 0;
        for (char c : expr.toCharArray()) {
            if (c >= 'p' && c <= 'z') {
                maxVar = Math.max(maxVar, c - 'p' + 1);
            }
        }
        return Math.max(maxVar, 1);
    }
    
    private static boolean evaluateWithAssignment(String expr, boolean[] values) {
        return LogicEvaluator.evaluateExpression(expr, values[0], 
            values.length > 1 ? values[1] : false,
            values.length > 2 ? values[2] : false);
    }
}
```

## 2. Set Theory

### 2.1 SetOperations Class

```java
package com.mathacademy.discrete;

import java.util.*;

public class SetOperations {
    
    public static <T> Set<T> union(Set<T> a, Set<T> b) {
        Set<T> result = new HashSet<>(a);
        result.addAll(b);
        return result;
    }
    
    public static <T> Set<T> intersection(Set<T> a, Set<T> b) {
        Set<T> result = new HashSet<>(a);
        result.retainAll(b);
        return result;
    }
    
    public static <T> Set<T> difference(Set<T> a, Set<T> b) {
        Set<T> result = new HashSet<>(a);
        result.removeAll(b);
        return result;
    }
    
    public static <T> Set<T> symmetricDifference(Set<T> a, Set<T> b) {
        Set<T> union = union(a, b);
        Set<T> intersection = intersection(a, b);
        return difference(union, intersection);
    }
    
    public static <T> Set<T> complement(Set<T> a, Set<T> universal) {
        return difference(universal, a);
    }
    
    public static <T> boolean isSubset(Set<T> a, Set<T> b) {
        return b.containsAll(a);
    }
    
    public static <T> boolean isProperSubset(Set<T> a, Set<T> b) {
        return a.size() < b.size() && isSubset(a, b);
    }
    
    public static <T> Set<List<T>> cartesianProduct(Set<T> a, Set<T> b) {
        Set<List<T>> result = new HashSet<>();
        for (T elemA : a) {
            for (T elemB : b) {
                List<T> pair = new ArrayList<>();
                pair.add(elemA);
                pair.add(elemB);
                result.add(pair);
            }
        }
        return result;
    }
    
    public static <T> Set<Set<T>> powerSet(Set<T> a) {
        Set<Set<T>> result = new HashSet<>();
        List<T> elements = new ArrayList<>(a);
        int n = elements.size();
        
        int numSubsets = 1 << n;
        for (int i = 0; i < numSubsets; i++) {
            Set<T> subset = new HashSet<>();
            for (int j = 0; j < n; j++) {
                if ((i & (1 << j)) != 0) {
                    subset.add(elements.get(j));
                }
            }
            result.add(subset);
        }
        
        return result;
    }
    
    public static <T> boolean areDisjoint(Set<T> a, Set<T> b) {
        return intersection(a, b).isEmpty();
    }
    
    public static <T> Set<T> unionAll(Set<Set<T>> collection) {
        Set<T> result = new HashSet<>();
        for (Set<T> s : collection) {
            result.addAll(s);
        }
        return result;
    }
    
    public static <T> Set<T> intersectionAll(Set<Set<T>> collection) {
        if (collection.isEmpty()) {
            return new HashSet<>();
        }
        
        Iterator<Set<T>> it = collection.iterator();
        Set<T> result = new HashSet<>(it.next());
        
        while (it.hasNext()) {
            result.retainAll(it.next());
        }
        
        return result;
    }
}
```

## 3. Relations

### 3.1 RelationProperties Class

```java
package com.mathacademy.discrete;

import java.util.*;

public class RelationProperties {
    
    private Set<int[]> relation;
    private int n;
    
    public RelationProperties(Set<int[]> relation, int n) {
        this.relation = relation;
        this.n = n;
    }
    
    public boolean isReflexive() {
        for (int i = 0; i < n; i++) {
            if (!contains(i, i)) {
                return false;
            }
        }
        return true;
    }
    
    public boolean isIrreflexive() {
        for (int[] pair : relation) {
            if (pair[0] == pair[1]) {
                return false;
            }
        }
        return true;
    }
    
    public boolean isSymmetric() {
        for (int[] pair : relation) {
            if (!contains(pair[1], pair[0])) {
                return false;
            }
        }
        return true;
    }
    
    public boolean isAntisymmetric() {
        for (int[] pair : relation) {
            if (pair[0] != pair[1] && contains(pair[1], pair[0])) {
                return false;
            }
        }
        return true;
    }
    
    public boolean isTransitive() {
        for (int[] pair1 : relation) {
            for (int[] pair2 : relation) {
                if (pair1[1] == pair2[0]) {
                    if (!contains(pair1[0], pair2[1])) {
                        return false;
                    }
                }
            }
        }
        return true;
    }
    
    public boolean isEquivalence() {
        return isReflexive() && isSymmetric() && isTransitive();
    }
    
    public boolean isPartialOrder() {
        return isReflexive() && isAntisymmetric() && isTransitive();
    }
    
    public boolean isTotalOrder() {
        return isPartialOrder() && isTotal();
    }
    
    public boolean isTotal() {
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (i != j && !contains(i, j) && !contains(j, i)) {
                    return false;
                }
            }
        }
        return true;
    }
    
    private boolean contains(int a, int b) {
        for (int[] pair : relation) {
            if (pair[0] == a && pair[1] == b) {
                return true;
            }
        }
        return false;
    }
    
    public Set<int[]> getReflexiveClosure() {
        Set<int[]> closure = new HashSet<>(relation);
        for (int i = 0; i < n; i++) {
            closure.add(new int[]{i, i});
        }
        return closure;
    }
    
    public Set<int[]> getSymmetricClosure() {
        Set<int[]> closure = new HashSet<>(relation);
        for (int[] pair : relation) {
            closure.add(new int[]{pair[1], pair[0]});
        }
        return closure;
    }
    
    public Set<int[]> getTransitiveClosure() {
        Set<int[]> closure = new HashSet<>(relation);
        boolean changed = true;
        
        while (changed) {
            changed = false;
            Set<int[]> newPairs = new HashSet<>();
            
            for (int[] pair1 : closure) {
                for (int[] pair2 : closure) {
                    if (pair1[1] == pair2[0]) {
                        int[] newPair = new int[]{pair1[0], pair2[1]};
                        if (!closure.contains(newPair)) {
                            newPairs.add(newPair);
                            changed = true;
                        }
                    }
                }
            }
            
            closure.addAll(newPairs);
        }
        
        return closure;
    }
}
```

### 3.2 EquivalenceRelation Class

```java
package com.mathacademy.discrete;

import java.util.*;

public class EquivalenceRelation {
    
    private Set<int[]> relation;
    private int n;
    
    public EquivalenceRelation(Set<int[]> relation, int n) {
        this.relation = relation;
        this.n = n;
    }
    
    public List<Set<Integer>> getEquivalenceClasses() {
        List<Set<Integer>> classes = new ArrayList<>();
        Set<Integer> unassigned = new HashSet<>();
        
        for (int i = 0; i < n; i++) {
            unassigned.add(i);
        }
        
        while (!unassigned.isEmpty()) {
            Iterator<Integer> it = unassigned.iterator();
            int representative = it.next();
            it.remove();
            
            Set<Integer> equivalenceClass = new HashSet<>();
            equivalenceClass.add(representative);
            
            Queue<Integer> queue = new LinkedList<>();
            queue.add(representative);
            
            while (!queue.isEmpty()) {
                int current = queue.poll();
                
                for (int[] pair : relation) {
                    if (pair[0] == current) {
                        int related = pair[1];
                        if (unassigned.contains(related)) {
                            unassigned.remove(related);
                            equivalenceClass.add(related);
                            queue.add(related);
                        }
                    }
                }
            }
            
            classes.add(equivalenceClass);
        }
        
        return classes;
    }
    
    public int getNumberOfEquivalenceClasses() {
        return getEquivalenceClasses().size();
    }
    
    public static Set<int[]> createModuloRelation(int mod) {
        Set<int[]> relation = new HashSet<>();
        
        for (int i = 0; i < mod * 2; i++) {
            for (int j = 0; j < mod * 2; j++) {
                if ((i - j) % mod == 0) {
                    relation.add(new int[]{i, j});
                }
            }
        }
        
        return relation;
    }
}
```

## 4. Functions

### 4.1 FunctionProperties Class

```java
package com.mathacademy.discrete;

import java.util.*;

public class FunctionProperties {
    
    public static boolean isFunction(Map<Integer, Integer> f, int domainSize, int codomainSize) {
        Set<Integer> usedDomain = new HashSet<>();
        
        for (Map.Entry<Integer, Integer> entry : f.entrySet()) {
            int x = entry.getKey();
            int y = entry.getValue();
            
            if (x < 0 || x >= domainSize) {
                return false;
            }
            if (y < 0 || y >= codomainSize) {
                return false;
            }
            
            if (usedDomain.contains(x)) {
                return false;
            }
            usedDomain.add(x);
        }
        
        return usedDomain.size() == domainSize;
    }
    
    public static boolean isInjective(Map<Integer, Integer> f) {
        Set<Integer> values = new HashSet<>();
        
        for (int y : f.values()) {
            if (values.contains(y)) {
                return false;
            }
            values.add(y);
        }
        
        return true;
    }
    
    public static boolean isSurjective(Map<Integer, Integer> f, int codomainSize) {
        Set<Integer> image = new HashSet<>(f.values());
        
        return image.size() == codomainSize;
    }
    
    public static boolean isBijective(Map<Integer, Integer> f, int domainSize, int codomainSize) {
        return domainSize == codomainSize && isInjective(f) && isSurjective(f, codomainSize);
    }
    
    public static Map<Integer, Integer> compose(Map<Integer, Integer> f, Map<Integer, Integer> g) {
        Map<Integer, Integer> result = new HashMap<>();
        
        for (Map.Entry<Integer, Integer> entry : f.entrySet()) {
            int x = entry.getKey();
            int y = entry.getValue();
            
            if (g.containsKey(y)) {
                result.put(x, g.get(y));
            }
        }
        
        return result;
    }
    
    public static Map<Integer, Integer> inverse(Map<Integer, Integer> f) {
        Map<Integer, Integer> inverse = new HashMap<>();
        
        for (Map.Entry<Integer, Integer> entry : f.entrySet()) {
            inverse.put(entry.getValue(), entry.getKey());
        }
        
        return inverse;
    }
}
```

## 5. Testing and Validation

### 5.1 DiscreteMathTest Class

```java
package com.mathacademy.discrete;

import java.util.*;

public class DiscreteMathTest {
    
    public static void main(String[] args) {
        testLogic();
        testSets();
        testRelations();
        testFunctions();
    }
    
    private static void testLogic() {
        System.out.println("=== Propositional Logic ===");
        
        boolean p = true, q = true, r = false;
        
        boolean result = LogicEvaluator.evaluateExpression("p && q || !r", p, q, r);
        System.out.printf("p && q || !r = %b (p=T, q=T, r=F)%n", result);
        
        result = LogicEvaluator.IMPLIES(p, q);
        System.out.printf("p -> q = %b%n", result);
        
        result = LogicEvaluator.IFF(p, q);
        System.out.printf("p <-> q = %b%n", result);
    }
    
    private static void testSets() {
        System.out.println("\n=== Set Operations ===");
        
        Set<Integer> A = new HashSet<>(Arrays.asList(1, 2, 3, 4));
        Set<Integer> B = new HashSet<>(Arrays.asList(3, 4, 5, 6));
        Set<Integer> Universal = new HashSet<>(Arrays.asList(1, 2, 3, 4, 5, 6));
        
        System.out.println("A = " + A);
        System.out.println("B = " + B);
        
        System.out.println("A ∪ B = " + SetOperations.union(A, B));
        System.out.println("A ∩ B = " + SetOperations.intersection(A, B));
        System.out.println("A - B = " + SetOperations.difference(A, B));
        System.out.println("A Δ B = " + SetOperations.symmetricDifference(A, B));
        
        System.out.println("A' = " + SetOperations.complement(A, Universal));
        System.out.println("P(A) size = " + SetOperations.powerSet(A).size());
    }
    
    private static void testRelations() {
        System.out.println("\n=== Relation Properties ===");
        
        Set<int[]> reflexive = new HashSet<>();
        reflexive.add(new int[]{1, 1});
        reflexive.add(new int[]{2, 2});
        reflexive.add(new int[]{1, 2});
        
        RelationProperties rp = new RelationProperties(reflexive, 3);
        System.out.println("Reflexive: " + rp.isReflexive());
        System.out.println("Symmetric: " + rp.isSymmetric());
        System.out.println("Transitive: " + rp.isTransitive());
        
        Set<int[]> equivalence = new HashSet<>();
        equivalence.add(new int[]{1, 1});
        equivalence.add(new int[]{2, 2});
        equivalence.add(new int[]{3, 3});
        equivalence.add(new int[]{1, 2});
        equivalence.add(new int[]{2, 1});
        
        RelationProperties eq = new RelationProperties(equivalence, 3);
        System.out.println("Equivalence: " + eq.isEquivalence());
        
        EquivalenceRelation er = new EquivalenceRelation(equivalence, 3);
        System.out.println("Equivalence classes: " + er.getEquivalenceClasses());
    }
    
    private static void testFunctions() {
        System.out.println("\n=== Function Properties ===");
        
        Map<Integer, Integer> f = new HashMap<>();
        f.put(0, 1);
        f.put(1, 2);
        f.put(2, 0);
        
        System.out.println("f: " + f);
        System.out.println("Injective: " + FunctionProperties.isInjective(f));
        System.out.println("Bijective: " + FunctionProperties.isBijective(f, 3, 3));
        
        Map<Integer, Integer> fInv = FunctionProperties.inverse(f);
        System.out.println("f^-1: " + fInv);
    }
}
```