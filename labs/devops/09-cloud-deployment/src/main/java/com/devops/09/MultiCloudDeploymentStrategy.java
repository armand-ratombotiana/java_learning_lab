package com.devops.clouddeploy;

public class MultiCloudDeploymentStrategy {
    public enum CloudProvider { AWS, AZURE, GCP }

    private final CloudProvider primary;
    private final CloudProvider secondary;

    public MultiCloudDeploymentStrategy(CloudProvider primary, CloudProvider secondary) {
        this.primary = primary;
        this.secondary = secondary;
    }

    public void deploy() {
        System.out.println("Deploying to primary: " + primary);
        System.out.println("Deploying to secondary: " + secondary);
        System.out.println("Configuring DNS failover between clouds");
        System.out.println("Multi-cloud deployment complete");
    }

    public void failover() {
        System.out.println("FAILOVER: Switching traffic from " + primary + " to " + secondary);
    }

    public static void main(String[] args) {
        MultiCloudDeploymentStrategy strategy = new MultiCloudDeploymentStrategy(
            CloudProvider.AWS, CloudProvider.AZURE
        );
        strategy.deploy();
        strategy.failover();
    }
}
