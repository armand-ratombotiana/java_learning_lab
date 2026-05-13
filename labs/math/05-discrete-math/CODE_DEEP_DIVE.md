# Code Deep Dive: Discrete Mathematics

```java
package com.mathacademy.discrete;

import java.util.*;

public class DiscreteMath {
    
    public static class Set<T> {
        private Set<T> delegate;
        public Set() { this.delegate = new java.util.HashSet<>(); }
        public Set(T... elements) { 
            this.delegate = new java.util.HashSet<>(Arrays.asList(elements));
        }
        public boolean add(T e) { return delegate.add(e); }
        public boolean contains(Object o) { return delegate.contains(o); }
        public Set<T> union(Set<T> other) {
            Set<T> result = new Set<>();
            result.delegate.addAll(this.delegate);
            result.delegate.addAll(other.delegate);
            return result;
        }
        public Set<T> intersection(Set<T> other) {
            Set<T> result = new Set<>();
            for (T e : this.delegate) {
                if (other.delegate.contains(e)) result.delegate.add(e);
            }
            return result;
        }
        public Set<T> difference(Set<T> other) {
            Set<T> result = new Set<>();
            for (T e : this.delegate) {
                if (!other.delegate.contains(e)) result.delegate.add(e);
            }
            return result;
        }
        public int size() { return delegate.size(); }
        public String toString() { return delegate.toString(); }
    }
    
    public static class Relation<T> {
        Set<T> domain;
        Set<T> codomain;
        Set<Pair<T,T>> pairs;
        
        public Relation(Set<T> domain, Set<T> codomain) {
            this.domain = domain; this.codomain = codomain;
            this.pairs = new Set<>();
        }
        
        public void add(T a, T b) { pairs.add(new Pair<>(a, b)); }
        
        public boolean isReflexive() {
            for (Object x : domain.delegate) {
                if (!pairs.contains(new Pair<>((T)x, (T)x))) return false;
            }
            return true;
        }
        
        public boolean isSymmetric() {
            for (Object p : pairs.delegate) {
                Pair<T,T> pair = (Pair<T,T>) p;
                if (!pairs.contains(new Pair<>(pair.second, pair.first))) return false;
            }
            return true;
        }
        
        public boolean isTransitive() {
            for (Object p1 : pairs.delegate) {
                for (Object p2 : pairs.delegate) {
                    Pair<T,T> pair1 = (Pair<T,T>) p1;
                    Pair<T,T> pair2 = (Pair<T,T>) p2;
                    if (pair1.second.equals(pair2.first)) {
                        if (!pairs.contains(new Pair<>(pair1.first, pair2.second))) return false;
                    }
                }
            }
            return true;
        }
        
        public boolean isEquivalence() { return isReflexive() && isSymmetric() && isTransitive(); }
        public boolean isPartialOrder() { return isReflexive() && isSymmetric() && !isSymmetric(); }
    }
    
    public static class Pair<T,U> {
        T first; U second;
        public Pair(T first, U second) { this.first = first; this.second = second; }
        public boolean equals(Object o) {
            if (o instanceof Pair) {
                Pair p = (Pair) o;
                return first.equals(p.first) && second.equals(p.second);
            }
            return false;
        }
        public int hashCode() { return Objects.hash(first, second); }
    }
    
    public static class PropositionalLogic {
        public static boolean evaluate(String expr, Map<String, Boolean> vars) {
            expr = expr.replace(" ", "");
            return parseExpr(expr, vars);
        }
        
        private static boolean parseExpr(String expr, Map<String, Boolean> vars) {
            if (expr.isEmpty()) return false;
            if (expr.startsWith("¬") || expr.startsWith("!")) {
                return !parseExpr(expr.substring(1), vars);
            }
            int parenDepth = 0;
            for (int i = expr.length() - 1; i >= 0; i--) {
                char c = expr.charAt(i);
                if (c == ')') parenDepth++;
                else if (c == '(') parenDepth--;
                else if ((c == '∧' || c == '&') && parenDepth == 0) {
                    return parseExpr(expr.substring(0, i), vars) && parseExpr(expr.substring(i+1), vars);
                }
            }
            for (int i = expr.length() - 1; i >= 0; i--) {
                char c = expr.charAt(i);
                if (c == ')' || c == '(') continue;
                if ((c == '∨' || c == '|') && parenDepth == 0) {
                    return parseExpr(expr.substring(0, i), vars) || parseExpr(expr.substring(i+1), vars);
                }
            }
            if (expr.startsWith("(") && expr.endsWith(")")) {
                return parseExpr(expr.substring(1, expr.length()-1), vars);
            }
            if (vars.containsKey(expr)) return vars.get(expr);
            return false;
        }
    }
    
    public static long permutations(int n, int r) {
        if (r > n) return 0;
        long result = 1;
        for (int i = 0; i < r; i++) result *= (n - i);
        return result;
    }
    
    public static long combinations(int n, int r) {
        if (r > n) return 0;
        if (r > n - r) r = n - r;
        long result = 1;
        for (int i = 0; i < r; i++) result = result * (n - i) / (i + 1);
        return result;
    }
    
    public static List<List<Integer>> powerSet(int[] set) {
        List<List<Integer>> result = new ArrayList<>();
        int n = set.length;
        for (int mask = 0; mask < (1 << n); mask++) {
            List<Integer> subset = new ArrayList<>();
            for (int i = 0; i < n; i++) {
                if ((mask & (1 << i)) != 0) subset.add(set[i]);
            }
            result.add(subset);
        }
        return result;
    }
}
```