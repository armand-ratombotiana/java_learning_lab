package com.javaacademy.lab40.bestpractices;

public enum EnumSingleton {
    INSTANCE;

    private String configValue;
    private long startTime;

    EnumSingleton() {
        startTime = System.currentTimeMillis();
        configValue = "default-config";
    }

    public String getConfigValue() { return configValue; }
    public void setConfigValue(String configValue) { this.configValue = configValue; }
    public long getStartTime() { return startTime; }

    public String getGreeting(String name) {
        return "Hello, " + name + "! (Singleton started at " + startTime + ")";
    }

    public static EnumSingleton getInstance() {
        return INSTANCE;
    }
}
