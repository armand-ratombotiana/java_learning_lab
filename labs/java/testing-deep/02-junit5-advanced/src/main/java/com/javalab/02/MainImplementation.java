package com.javalab.02;

public class MainImplementation {
    
    public boolean isPalindrome(String s) {
        if (s == null) return false;
        String clean = s.replaceAll("[^a-zA-Z0-9]", "").toLowerCase();
        return clean.equals(new StringBuilder(clean).reverse().toString());
    }
    
    public int fibonacci(int n) {
        if (n < 0) throw new IllegalArgumentException("Negative input");
        if (n <= 1) return n;
        return fibonacci(n-1) + fibonacci(n-2);
    }
}
