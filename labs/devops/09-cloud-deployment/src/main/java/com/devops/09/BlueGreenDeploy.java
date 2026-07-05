package com.devops.clouddeploy;

public class BlueGreenDeploy {
    private boolean blueActive = true;

    public void switchTraffic() {
        blueActive = !blueActive;
        String active = blueActive ? "BLUE" : "GREEN";
        String standby = blueActive ? "GREEN" : "BLUE";
        System.out.println("Switching traffic to: " + active);
        System.out.println("Standby environment: " + standby);
    }

    public void deployNewVersion() {
        String target = blueActive ? "GREEN" : "BLUE";
        System.out.println("Deploying new version to " + target + " environment");
    }

    public void rollback() {
        switchTraffic();
        System.out.println("Rollback complete - reverted to previous version");
    }

    public static void main(String[] args) {
        BlueGreenDeploy bg = new BlueGreenDeploy();
        bg.deployNewVersion();
        bg.switchTraffic();
        System.out.println("New version is live");

        bg.rollback();
    }
}
