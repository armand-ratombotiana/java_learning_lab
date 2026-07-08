package com.devops.fourteen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HelmChartBuilder {
    private final String name;
    private final String version;
    private final String description;
    private final List<HelmDependency> dependencies = new ArrayList<>();
    private final Map<String, Object> values = new HashMap<>();
    private final List<String> templates = new ArrayList<>();

    public HelmChartBuilder(String name, String version, String description) {
        this.name = name;
        this.version = version;
        this.description = description;
    }

    public void addDependency(String name, String version, String repository) {
        dependencies.add(new HelmDependency(name, version, repository));
    }

    public void setValue(String key, Object value) { values.put(key, value); }

    public void addTemplate(String templateContent) { templates.add(templateContent); }

    public String renderChartYaml() {
        StringBuilder sb = new StringBuilder();
        sb.append("apiVersion: v2\n");
        sb.append("name: ").append(name).append("\n");
        sb.append("version: ").append(version).append("\n");
        sb.append("description: ").append(description).append("\n");
        sb.append("type: application\n");
        sb.append("dependencies:\n");
        for (HelmDependency dep : dependencies) {
            sb.append("  - name: ").append(dep.name).append("\n");
            sb.append("    version: ").append(dep.version).append("\n");
            sb.append("    repository: ").append(dep.repository).append("\n");
        }
        return sb.toString();
    }

    public String renderValuesYaml() {
        StringBuilder sb = new StringBuilder();
        renderMap(sb, values, 0);
        return sb.toString();
    }

    private void renderMap(StringBuilder sb, Map<String, Object> map, int indent) {
        String prefix = "  ".repeat(indent);
        for (var entry : map.entrySet()) {
            if (entry.getValue() instanceof Map) {
                sb.append(prefix).append(entry.getKey()).append(":\n");
                renderMap(sb, (Map<String, Object>) entry.getValue(), indent + 1);
            } else {
                sb.append(prefix).append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
            }
        }
    }

    public String renderDeploymentTemplate() {
        return """
apiVersion: apps/v1
kind: Deployment
metadata:
  name: {{ include \"%s.fullname\" . }}
  labels:
    app.kubernetes.io/name: {{ include \"%s.name\" . }}
    app.kubernetes.io/instance: {{ .Release.Name }}
spec:
  replicas: {{ .Values.replicaCount }}
  selector:
    matchLabels:
      app.kubernetes.io/name: {{ include \"%s.name\" . }}
      app.kubernetes.io/instance: {{ .Release.Name }}
  template:
    metadata:
      labels:
        app.kubernetes.io/name: {{ include \"%s.name\" . }}
        app.kubernetes.io/instance: {{ .Release.Name }}
    spec:
      containers:
        - name: {{ .Chart.Name }}
          image: "{{ .Values.image.repository }}:{{ .Values.image.tag }}"
          imagePullPolicy: {{ .Values.image.pullPolicy }}
          ports:
            - containerPort: {{ .Values.service.port }}
          resources:
            {{- toYaml .Values.resources | nindent 12 }}
""".formatted(name, name, name, name);
    }

    public String renderServiceTemplate() {
        return """
apiVersion: v1
kind: Service
metadata:
  name: {{ include \"%s.fullname\" . }}
  labels:
    app.kubernetes.io/name: {{ include \"%s.name\" . }}
    app.kubernetes.io/instance: {{ .Release.Name }}
spec:
  type: {{ .Values.service.type }}
  ports:
    - port: {{ .Values.service.port }}
      targetPort: http
      protocol: TCP
      name: http
  selector:
    app.kubernetes.io/name: {{ include \"%s.name\" . }}
    app.kubernetes.io/instance: {{ .Release.Name }}
""".formatted(name, name, name);
    }

    public List<HelmDependency> getDependencies() { return dependencies; }

    public record HelmDependency(String name, String version, String repository) {}

    public static void main(String[] args) {
        HelmChartBuilder chart = new HelmChartBuilder("myapp", "1.0.0", "A sample Helm chart");
        chart.addDependency("postgresql", "12.x", "https://charts.bitnami.com/bitnami");
        chart.addDependency("redis", "17.x", "https://charts.bitnami.com/bitnami");
        chart.setValue("replicaCount", 3);
        chart.setValue("image", Map.of("repository", "myapp", "tag", "latest", "pullPolicy", "Always"));
        chart.setValue("service", Map.of("type", "ClusterIP", "port", 8080));
        chart.setValue("resources", Map.of("requests", Map.of("cpu", "100m", "memory", "128Mi"),
            "limits", Map.of("cpu", "500m", "memory", "512Mi")));
        System.out.println("=== Chart.yaml ===");
        System.out.println(chart.renderChartYaml());
        System.out.println("=== values.yaml ===");
        System.out.println(chart.renderValuesYaml());
        System.out.println("=== Deployment Template ===");
        System.out.println(chart.renderDeploymentTemplate());
        System.out.println("=== Service Template ===");
        System.out.println(chart.renderServiceTemplate());
    }
}
