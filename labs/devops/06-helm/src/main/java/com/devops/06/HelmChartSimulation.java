package com.devops.helm;

import java.util.Map;

public class HelmChartSimulation {
    private String name;
    private String version;
    private Map<String, Object> values;

    public HelmChartSimulation(String name, String version) {
        this.name = name;
        this.version = version;
    }

    public void setValues(Map<String, Object> values) {
        this.values = values;
    }

    public String render() {
        StringBuilder sb = new StringBuilder();
        sb.append("---\n");
        sb.append("apiVersion: v2\n");
        sb.append("name: ").append(name).append("\n");
        sb.append("version: ").append(version).append("\n");
        sb.append("---\n");
        if (values != null) {
            values.forEach((k, v) -> sb.append(k).append(": ").append(v).append("\n"));
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        HelmChartSimulation chart = new HelmChartSimulation("my-app", "1.0.0");
        chart.setValues(Map.of(
            "replicaCount", 3,
            "image.repository", "myapp",
            "image.tag", "latest",
            "service.port", 8080
        ));
        System.out.println(chart.render());
    }
}
