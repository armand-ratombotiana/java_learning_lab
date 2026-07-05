package com.devops.configmgmt;

import java.util.Map;

public class GitOpsPatternSimulation {
    private String currentCommit;
    private Map<String, String> desiredState;

    public void reconcile(Map<String, String> desiredState) {
        this.desiredState = desiredState;
        for (Map.Entry<String, String> entry : desiredState.entrySet()) {
            System.out.println("Reconciling " + entry.getKey() + " to " + entry.getValue());
        }
        System.out.println("System converged to desired state");
    }

    public void detectDrift(Map<String, String> actualState) {
        for (Map.Entry<String, String> desired : desiredState.entrySet()) {
            String actual = actualState.get(desired.getKey());
            if (!desired.getValue().equals(actual)) {
                System.out.println("DRIFT detected: " + desired.getKey()
                    + " expected=" + desired.getValue() + " actual=" + actual);
            }
        }
    }

    public static void main(String[] args) {
        GitOpsPatternSimulation gitops = new GitOpsPatternSimulation();

        gitops.reconcile(Map.of(
            "namespace", "production",
            "replicas", "5",
            "image", "myapp:2.0"
        ));

        gitops.detectDrift(Map.of(
            "namespace", "production",
            "replicas", "3",
            "image", "myapp:2.0"
        ));
    }
}
