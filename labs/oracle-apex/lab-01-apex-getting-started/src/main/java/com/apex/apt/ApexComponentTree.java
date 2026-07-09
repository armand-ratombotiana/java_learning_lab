package com.apex.apt;

import java.util.*;

public class ApexComponentTree {
    public enum ComponentType { PAGE, REGION, ITEM, BUTTON, DYNAMIC_ACTION, PROCESS, BRANCH, VALIDATION }
    
    public record Component(String id, ComponentType type, String name, String parentId, Map<String, String> attributes) {}
    
    private final Map<String, Component> components = new LinkedHashMap<>();

    public void addComponent(Component c) { components.put(c.id(), c); }
    
    public Component getComponent(String id) { return components.get(id); }
    
    public List<Component> getChildren(String parentId) {
        return components.values().stream()
            .filter(c -> parentId.equals(c.parentId()))
            .toList();
    }
    
    public List<Component> getByType(ComponentType type) {
        return components.values().stream()
            .filter(c -> c.type() == type)
            .toList();
    }
    
    public Map<String, Object> buildTree() {
        var tree = new HashMap<String, Object>();
        var pages = getByType(ComponentType.PAGE);
        for (var page : pages) {
            tree.put(page.id(), buildSubtree(page.id()));
        }
        return tree;
    }
    
    private Map<String, Object> buildSubtree(String parentId) {
        var node = new LinkedHashMap<String, Object>();
        var children = getChildren(parentId);
        for (var child : children) {
            node.put(child.id(), buildSubtree(child.id()));
        }
        return node;
    }
    
    public static ApexComponentTree createSampleTree() {
        var tree = new ApexComponentTree();
        tree.addComponent(new Component("p1", ComponentType.PAGE, "Dashboard", null, Map.of("theme", "42")));
        tree.addComponent(new Component("r1", ComponentType.REGION, "Employees Report", "p1", Map.of("type", "IR", "source", "SELECT * FROM emp")));
        tree.addComponent(new Component("r2", ComponentType.REGION, "Chart", "p1", Map.of("type", "CHART", "source", "SELECT dept,COUNT(*) FROM emp GROUP BY dept")));
        tree.addComponent(new Component("i1", ComponentType.ITEM, "P1_DEPT_ID", "r1", Map.of("type", "NUMBER", "default", "10")));
        tree.addComponent(new Component("b1", ComponentType.BUTTON, "SUBMIT", "r1", Map.of("action", "SUBMIT", "label", "Go")));
        tree.addComponent(new Component("da1", ComponentType.DYNAMIC_ACTION, "Refresh Emp", "b1", Map.of("event", "click", "true_action", "REFRESH")));
        return tree;
    }
    
    public int size() { return components.size(); }
    public boolean isEmpty() { return components.isEmpty(); }
    public void clear() { components.clear(); }
}