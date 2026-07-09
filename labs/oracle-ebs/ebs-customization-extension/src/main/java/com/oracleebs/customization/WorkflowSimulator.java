package com.oracleebs.customization;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class WorkflowSimulator {
    public enum WorkflowStatus { INITIATED, IN_PROGRESS, COMPLETED, ERROR, SUSPENDED }

    public static class WorkflowItem {
        private final String itemKey;
        private final String workflowName;
        private WorkflowStatus status;
        private final String initiator;
        private final Map<String, Object> attributes;
        private final List<String> activityLog;
        private String currentActivity;
        private String errorMessage;

        public WorkflowItem(String itemKey, String name, String initiator) {
            this.itemKey = itemKey;
            this.workflowName = name;
            this.initiator = initiator;
            this.status = WorkflowStatus.INITIATED;
            this.attributes = new ConcurrentHashMap<>();
            this.activityLog = new ArrayList<>();
            this.currentActivity = "START";
        }

        public void setAttribute(String name, Object val) { attributes.put(name, val); }
        public void logActivity(String activity) { activityLog.add(activity); }
        public String getItemKey() { return itemKey; }
        public String getWorkflowName() { return workflowName; }
        public WorkflowStatus getStatus() { return status; }
        public String getInitiator() { return initiator; }
        public Map<String, Object> getAttributes() { return Collections.unmodifiableMap(attributes); }
        public List<String> getActivityLog() { return Collections.unmodifiableList(activityLog); }
        public String getCurrentActivity() { return currentActivity; }
        public String getErrorMessage() { return errorMessage; }
        public void setStatus(WorkflowStatus s) { status = s; }
        public void setCurrentActivity(String a) { currentActivity = a; }
        public void setErrorMessage(String m) { errorMessage = m; }
    }

    public static class WorkflowDefinition {
        private final String name;
        private final List<String> activities;
        private final Map<String, List<String>> transitions;

        public WorkflowDefinition(String name) {
            this.name = name;
            this.activities = new ArrayList<>();
            this.transitions = new ConcurrentHashMap<>();
        }

        public void addActivity(String act) { activities.add(act); }
        public void addTransition(String from, String to) {
            transitions.computeIfAbsent(from, k -> new ArrayList<>()).add(to);
        }
        public String getName() { return name; }
        public List<String> getActivities() { return Collections.unmodifiableList(activities); }
        public Map<String, List<String>> getTransitions() { return Collections.unmodifiableMap(transitions); }
    }

    private final Map<String, WorkflowDefinition> definitions;
    private final Map<String, WorkflowItem> items;

    public WorkflowSimulator() {
        this.definitions = new ConcurrentHashMap<>();
        this.items = new ConcurrentHashMap<>();
    }

    public void defineWorkflow(WorkflowDefinition def) {
        definitions.put(def.getName(), def);
    }

    public WorkflowItem startWorkflow(String itemKey, String workflowName, String initiator) {
        WorkflowDefinition def = definitions.get(workflowName);
        if (def == null) throw new IllegalArgumentException("Workflow not found: " + workflowName);
        WorkflowItem item = new WorkflowItem(itemKey, workflowName, initiator);
        item.setStatus(WorkflowStatus.IN_PROGRESS);
        item.logActivity("Started workflow: " + workflowName);
        items.put(itemKey, item);
        return item;
    }

    public boolean advanceWorkflow(String itemKey, String activity) {
        WorkflowItem item = items.get(itemKey);
        if (item == null) return false;
        item.setCurrentActivity(activity);
        item.logActivity("Advanced to: " + activity);

        WorkflowDefinition def = definitions.get(item.getWorkflowName());
        if (def != null) {
            var nextActivities = def.getTransitions().get(activity);
            if (nextActivities == null || nextActivities.isEmpty()) {
                item.setStatus(WorkflowStatus.COMPLETED);
                item.logActivity("Workflow completed");
            }
        }
        return true;
    }

    public boolean suspendWorkflow(String itemKey) {
        WorkflowItem item = items.get(itemKey);
        if (item == null) return false;
        item.setStatus(WorkflowStatus.SUSPENDED);
        item.logActivity("Workflow suspended");
        return true;
    }

    public Optional<WorkflowItem> getItem(String itemKey) {
        return Optional.ofNullable(items.get(itemKey));
    }

    public static WorkflowSimulator createDefault() {
        WorkflowSimulator sim = new WorkflowSimulator();
        WorkflowDefinition def = new WorkflowDefinition("PO_APPROVAL");
        def.addActivity("START");
        def.addActivity("MANAGER_APPROVAL");
        def.addActivity("BUDGET_CHECK");
        def.addActivity("FINANCE_APPROVAL");
        def.addActivity("END");
        def.addTransition("START", "MANAGER_APPROVAL");
        def.addTransition("MANAGER_APPROVAL", "BUDGET_CHECK");
        def.addTransition("BUDGET_CHECK", "FINANCE_APPROVAL");
        def.addTransition("FINANCE_APPROVAL", "END");
        sim.defineWorkflow(def);
        return sim;
    }
}
