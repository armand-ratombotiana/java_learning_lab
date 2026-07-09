package com.databases.plsql;

import java.util.*;

public class PlSqlSimulator {

    public record Variable(String name, String type, Object value) {}
    public record StackFrame(String blockName, Map<String, Variable> locals) {}

    private final Deque<StackFrame> callStack = new ArrayDeque<>();
    private final Map<String, Object> globalState = new HashMap<>();
    private final List<String> output = new ArrayList<>();

    public PlSqlSimulator() {
        callStack.push(new StackFrame("GLOBAL", new HashMap<>()));
    }

    public void declare(String name, String type, Object initial) {
        var frame = callStack.peek();
        frame.locals().put(name, new Variable(name, type, initial));
    }

    public void assign(String name, Object value) {
        var stackIter = callStack.descendingIterator();
        while (stackIter.hasNext()) {
            var frame = stackIter.next();
            if (frame.locals().containsKey(name)) {
                var old = frame.locals().get(name);
                frame.locals().put(name, new Variable(old.name(), old.type(), value));
                return;
            }
        }
        // not found locally, try global
        globalState.put(name, value);
    }

    public Object resolve(String name) {
        var stackIter = callStack.descendingIterator();
        while (stackIter.hasNext()) {
            var frame = stackIter.next();
            if (frame.locals().containsKey(name))
                return frame.locals().get(name).value();
        }
        return globalState.get(name);
    }

    public void pushFrame(String blockName) {
        callStack.push(new StackFrame(blockName, new HashMap<>()));
    }

    public void popFrame() {
        if (callStack.size() > 1) callStack.pop();
    }

    public void println(String msg) { output.add(msg); }
    public List<String> getOutput() { return List.copyOf(output); }
    public void clearOutput() { output.clear(); }

    public boolean isDeclared(String name) {
        return callStack.stream().anyMatch(f -> f.locals().containsKey(name))
            || globalState.containsKey(name);
    }

    public String getType(String name) {
        for (var frame : callStack) {
            if (frame.locals().containsKey(name))
                return frame.locals().get(name).type();
        }
        return null;
    }

    public void cleanState() {
        callStack.clear();
        callStack.push(new StackFrame("GLOBAL", new HashMap<>()));
        globalState.clear();
        output.clear();
    }

    public static PlSqlSimulator createWithSampleData() {
        var sim = new PlSqlSimulator();
        sim.declare("v_emp_id", "NUMBER", 100);
        sim.declare("v_ename", "VARCHAR2(100)", "KING");
        sim.declare("v_salary", "NUMBER(8,2)", 24000.0);
        sim.declare("v_dept_id", "NUMBER", 10);
        sim.declare("v_hire_date", "DATE", "17-NOV-2003");
        return sim;
    }

    public String simulateBlock(String blockText) {
        clearOutput();
        var lines = blockText.split("\n");
        pushFrame("ANONYMOUS");
        for (var line : lines) {
            String trimmed = line.trim().toUpperCase();
            if (trimmed.startsWith("DECLARE") || trimmed.startsWith("BEGIN")
                || trimmed.startsWith("END") || trimmed.startsWith("EXCEPTION")
                || trimmed.startsWith("--") || trimmed.isEmpty()) continue;
            if (trimmed.startsWith("DBMS_OUTPUT.PUT_LINE")) {
                int start = trimmed.indexOf('(');
                int end = trimmed.lastIndexOf(')');
                if (start >= 0 && end > start) {
                    String arg = trimmed.substring(start + 1, end);
                    if (arg.contains("||")) {
                        var parts = arg.split("\\|\\|");
                        var sb = new StringBuilder();
                        for (var part : parts) {
                            var p = part.trim();
                            if (p.startsWith("'") && p.endsWith("'"))
                                sb.append(p.substring(1, p.length() - 1));
                            else
                                sb.append(resolve(p.replace(";", "").trim()));
                        }
                        println(sb.toString());
                    } else {
                        println(String.valueOf(resolve(arg.replace(";", "").trim())));
                    }
                }
            }
        }
        popFrame();
        return String.join("\n", getOutput());
    }
}