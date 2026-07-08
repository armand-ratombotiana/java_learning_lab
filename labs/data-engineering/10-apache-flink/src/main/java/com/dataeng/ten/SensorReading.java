package com.dataeng.ten;

import java.util.Objects;

public class SensorReading {
    private String sensorId;
    private double value;
    private long timestamp;
    private String unit;

    public SensorReading() {}

    public SensorReading(String sensorId, double value, long timestamp, String unit) {
        this.sensorId = sensorId;
        this.value = value;
        this.timestamp = timestamp;
        this.unit = unit;
    }

    public String getSensorId() { return sensorId; }
    public void setSensorId(String sensorId) { this.sensorId = sensorId; }
    public double getValue() { return value; }
    public void setValue(double value) { this.value = value; }
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SensorReading that)) return false;
        return Double.compare(that.value, value) == 0 && timestamp == that.timestamp
            && Objects.equals(sensorId, that.sensorId) && Objects.equals(unit, that.unit);
    }

    @Override
    public int hashCode() { return Objects.hash(sensorId, value, timestamp, unit); }

    @Override
    public String toString() {
        return "SensorReading{sensorId='" + sensorId + "', value=" + value
            + ", timestamp=" + timestamp + ", unit='" + unit + "'}";
    }
}
