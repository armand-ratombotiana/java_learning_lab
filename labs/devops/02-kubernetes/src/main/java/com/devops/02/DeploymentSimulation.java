package com.devops.kubernetes;

public class DeploymentSimulation {
    private int replicas;
    private int available;
    private int ready;

    public DeploymentSimulation(int replicas) {
        this.replicas = replicas;
        this.ready = 0;
    }

    public void scale(int newReplicas) {
        System.out.println("Scaling from " + replicas + " to " + newReplicas);
        replicas = newReplicas;
        ready = Math.min(ready, replicas);
    }

    public void rollUpdate() {
        System.out.println("Starting rolling update...");
        for (int i = 0; i < replicas; i++) {
            ready = i;
            System.out.println("  Pod " + (i + 1) + "/" + replicas + " updated, ready: " + ready);
        }
        ready = replicas;
        System.out.println("Rolling update complete. Ready: " + ready + "/" + replicas);
    }

    public boolean isReady() {
        return ready == replicas;
    }

    public static void main(String[] args) {
        DeploymentSimulation deploy = new DeploymentSimulation(3);
        deploy.rollUpdate();
        deploy.scale(5);
        deploy.rollUpdate();
        System.out.println("Deployment ready: " + deploy.isReady());
    }
}
