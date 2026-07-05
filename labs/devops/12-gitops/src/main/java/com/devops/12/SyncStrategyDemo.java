package com.devops.gitops;

public class SyncStrategyDemo {
    public enum Strategy { APPLY, PRUNE, SYNC_WITH_DEPENDENCY }

    public void execute(Strategy strategy, String appName) {
        switch (strategy) {
            case APPLY:
                System.out.println("Applying manifests for " + appName);
                break;
            case PRUNE:
                System.out.println("Pruning resources not in Git for " + appName);
                break;
            case SYNC_WITH_DEPENDENCY:
                System.out.println("Syncing " + appName + " with dependency ordering");
                break;
        }
        System.out.println("Sync strategy '" + strategy + "' completed");
    }

    public static void main(String[] args) {
        SyncStrategyDemo demo = new SyncStrategyDemo();
        demo.execute(SyncStrategyDemo.Strategy.APPLY, "frontend");
        demo.execute(SyncStrategyDemo.Strategy.PRUNE, "backend");
        demo.execute(SyncStrategyDemo.Strategy.SYNC_WITH_DEPENDENCY, "database");
    }
}
