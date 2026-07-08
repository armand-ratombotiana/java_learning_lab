package com.arch.platform;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class GoldenPathTemplate {
    private final Map<String, TemplateDefinition> templates = new ConcurrentHashMap<>();

    public void registerTemplate(String name, TemplateDefinition template) {
        templates.put(name, template);
    }

    public TemplateDefinition getTemplate(String name) {
        TemplateDefinition template = templates.get(name);
        if (template == null) throw new IllegalArgumentException("Unknown template: " + name);
        return template;
    }

    public GeneratedProject generate(String templateName, Map<String, String> parameters) {
        TemplateDefinition template = getTemplate(templateName);
        Map<String, String> resolved = resolveParameters(template, parameters);
        return new GeneratedProject(templateName, resolved, template.steps());
    }

    private Map<String, String> resolveParameters(TemplateDefinition template, Map<String, String> provided) {
        Map<String, String> resolved = new HashMap<>(template.defaults());
        provided.forEach((k, v) -> {
            if (template.requiredParams().contains(k) || template.optionalParams().contains(k)) {
                resolved.put(k, v);
            }
        });
        List<String> missing = template.requiredParams().stream()
            .filter(p -> !resolved.containsKey(p) || resolved.get(p).isBlank())
            .toList();
        if (!missing.isEmpty()) {
            throw new IllegalArgumentException("Missing required parameters: " + missing);
        }
        return resolved;
    }

    public List<String> getTemplateNames() { return List.copyOf(templates.keySet()); }
}

record TemplateDefinition(String name, String description, List<String> requiredParams, List<String> optionalParams, Map<String, String> defaults, List<String> steps) {}
record GeneratedProject(String templateName, Map<String, String> parameters, List<String> steps) {}
