package com.javalab.03;

import java.util.HashMap;
import java.util.Map;

public class MainImplementation {
    
    private final Map<String, String> storage = new HashMap<>();
    
    public String put(String key, String value) { return storage.put(key, value); }
    public String get(String key) { return storage.get(key); }
    public String remove(String key) { return storage.remove(key); }
    public int size() { return storage.size(); }
    public void clear() { storage.clear(); }
    public boolean containsKey(String key) { return storage.containsKey(key); }
}
