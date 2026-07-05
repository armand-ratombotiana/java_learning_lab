package com.devops.helm;

import java.util.Map;

public class TemplateRenderer {
    public String renderTemplate(String template, Map<String, Object> values) {
        String result = template;
        for (Map.Entry<String, Object> entry : values.entrySet()) {
            result = result.replace("{{ .Values." + entry.getKey() + " }}", String.valueOf(entry.getValue()));
        }
        return result;
    }

    public static void main(String[] args) {
        TemplateRenderer renderer = new TemplateRenderer();

        String template = """
            apiVersion: apps/v1
            kind: Deployment
            metadata:
              name: {{ .Values.name }}
            spec:
              replicas: {{ .Values.replicas }}
            """;

        Map<String, Object> values = Map.of(
            "name", "my-app",
            "replicas", 3
        );

        String rendered = renderer.renderTemplate(template, values);
        System.out.println(rendered);
    }
}
