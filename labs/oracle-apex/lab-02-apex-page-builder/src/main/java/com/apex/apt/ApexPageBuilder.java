package com.apex.apt;

import java.util.*;

public class ApexPageBuilder {
    public record PageProcess(String name, String type, String point, String source) {}
    public record DynamicAction(String name, String event, String selectionType, String trueAction, String falseAction) {}
    public record Branch(String name, String point, String target, boolean condition) {}
    public record Validation(String name, String type, String message, String condition) {}
    public record Computation(String name, String point, String item, String expression) {}

    private final List<PageProcess> processes = new ArrayList<>();
    private final List<DynamicAction> actions = new ArrayList<>();
    private final List<Branch> branches = new ArrayList<>();
    private final List<Validation> validations = new ArrayList<>();
    private final List<Computation> computations = new ArrayList<>();

    public void addProcess(PageProcess p) { processes.add(p); }
    public void addAction(DynamicAction a) { actions.add(a); }
    public void addBranch(Branch b) { branches.add(b); }
    public void addValidation(Validation v) { validations.add(v); }
    public void addComputation(Computation c) { computations.add(c); }

    public List<String> executeProcesses() {
        var results = new ArrayList<String>();
        for (var p : processes) {
            results.add("Process '" + p.name() + "' executed at point '" + p.point() + "'");
        }
        return results;
    }

    public List<String> executeValidations(Map<String, String> items) {
        var results = new ArrayList<String>();
        for (var v : validations) {
            boolean valid = evaluateCondition(v.condition(), items);
            results.add("Validation '" + v.name() + "': " + (valid ? "PASS" : "FAIL - " + v.message()));
        }
        return results;
    }

    public List<String> executeComputations(Map<String, String> items) {
        var results = new ArrayList<String>();
        for (var c : computations) {
            String value = evaluateExpression(c.expression(), items);
            results.add("Computed " + c.item() + " = " + value + " at " + c.point());
        }
        return results;
    }

    public List<String> handleAction(String actionName, Map<String, String> context) {
        var results = new ArrayList<String>();
        for (var a : actions) {
            if (a.name().equals(actionName)) {
                results.add("True action: " + a.trueAction());
                if (a.falseAction() != null && !a.falseAction().isEmpty())
                    results.add("False action: " + a.falseAction());
            }
        }
        return results;
    }

    private boolean evaluateCondition(String condition, Map<String, String> items) {
        if (condition == null || condition.isEmpty()) return true;
        for (var entry : items.entrySet()) {
            if (condition.contains(":" + entry.getKey())) {
                return condition.contains(entry.getValue());
            }
        }
        return true;
    }

    private String evaluateExpression(String expr, Map<String, String> items) {
        if (expr == null) return "";
        String result = expr;
        for (var e : items.entrySet()) {
            result = result.replace(":" + e.getKey(), e.getValue());
        }
        return result;
    }

    public int getProcessCount() { return processes.size(); }
    public int getActionCount() { return actions.size(); }
    public int getBranchCount() { return branches.size(); }
    public int getValidationCount() { return validations.size(); }

    public static ApexPageBuilder createSample() {
        var pb = new ApexPageBuilder();
        pb.addProcess(new PageProcess("Save Employee", "PLSQL", "AFTER_SUBMIT", "UPDATE employees SET ..."));
        pb.addAction(new DynamicAction("Refresh Grid", "click", "REGION", "REFRESH", null));
        pb.addBranch(new Branch("Go to Report", "AFTER_PROCESS", "f?p=100:2", true));
        pb.addValidation(new Validation("Check Salary", "ITEM_NOT_NULL", "Salary is required", ":P1_SALARY is null"));
        pb.addComputation(new Computation("Set Default Dept", "BEFORE_HEADER", "P1_DEPT_ID", "10"));
        return pb;
    }
}