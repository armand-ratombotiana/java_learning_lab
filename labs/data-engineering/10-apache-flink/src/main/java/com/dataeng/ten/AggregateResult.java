package com.dataeng.ten;

public class AggregateResult {
    private String sensorId;
    private long windowEnd;
    private double avgValue;
    private double maxValue;
    private long count;

    public AggregateResult() {}

    public AggregateResult(String sensorId, long windowEnd, double avgValue, double maxValue, long count) {
        this.sensorId = sensorId;
        this.windowEnd = windowEnd;
        this.avgValue = avgValue;
        this.maxValue = maxValue;
        this.count = count;
    }

    public String getSensorId() { return sensorId; }
    public void setSensorId(String sensorId) { this.sensorId = sensorId; }
    public long getWindowEnd() { return windowEnd; }
    public void setWindowEnd(long windowEnd) { this.windowEnd = windowEnd; }
    public double getAvgValue() { return avgValue; }
    public void setAvgValue(double avgValue) { this.avgValue = avgValue; }
    public double getMaxValue() { return maxValue; }
    public void setMaxValue(double maxValue) { this.maxValue = maxValue; }
    public long getCount() { return count; }
    public void setCount(long count) { this.count = count; }
}
