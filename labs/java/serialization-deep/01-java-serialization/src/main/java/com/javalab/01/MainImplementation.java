package com.javalab.01;

import java.io.*;
import java.util.Objects;

public class MainImplementation implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private int id;
    private String name;
    private transient String cachedValue;
    
    public MainImplementation() {}
    
    public MainImplementation(int id, String name) {
        this.id = id;
        this.name = name;
        this.cachedValue = computeCachedValue();
    }
    
    private String computeCachedValue() {
        return "cached-" + name + "-" + id;
    }
    
    private void writeObject(ObjectOutputStream oos) throws IOException {
        oos.defaultWriteObject();
        oos.writeObject(computeCachedValue());
    }
    
    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        ois.defaultReadObject();
        this.cachedValue = (String) ois.readObject();
        if (id <= 0) throw new InvalidObjectException("id must be positive");
        if (name == null || name.isEmpty()) throw new InvalidObjectException("name must not be empty");
    }
    
    private Object readResolve() throws ObjectStreamException {
        return this;
    }
    
    public int getId() { return id; }
    public String getName() { return name; }
    public String getCachedValue() { return cachedValue; }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MainImplementation)) return false;
        MainImplementation that = (MainImplementation) o;
        return id == that.id && Objects.equals(name, that.name);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
    
    @Override
    public String toString() {
        return "Person{id=" + id + ", name='" + name + "'}";
    }
}
