package com.learning.grafana;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.HashMap;

@Component
public class GrafanaSolution {

    public Map<String, Object> createDashboard(String title) {
        Map<String, Object> dashboard = new HashMap<>();
        dashboard.put("title", title);
        dashboard.put("panels", new Object[]{});
        dashboard.put("version", 1);
        return dashboard;
    }

    public Map<String, Object> createDataSource(String name, String type, String url) {
        Map<String, Object> ds = new HashMap<>();
        ds.put("name", name);
        ds.put("type", type);
        ds.put("url", url);
        ds.put("access", "proxy");
        ds.put("isDefault", false);
        return ds;
    }

    public Map<String, Object> createPanel(String title, String datasource, String query) {
        Map<String, Object> panel = new HashMap<>();
        panel.put("title", title);
        panel.put("type", "graph");
        panel.put("datasource", datasource);
        panel.put("targets", new Object[]{
            Map.of("expr", query)
        });
        return panel;
    }

    public Map<String, Object> createPrometheusQuery(String query, String legend) {
        Map<String, Object> target = new HashMap<>();
        target.put("expr", query);
        target.put("legendFormat", legend);
        target.put("refId", "A");
        return target;
    }

    public void configureAlerts(Map<String, Object> panel, String condition) {
        panel.put("alert", Map.of(
            "conditions", new Object[]{
                Map.of("type", "query", "reducer", Map.of("type", "avg"), "evaluator", Map.of("type", "gt", "params", 100))
            },
            "frequency", "1m",
            "handler", 1
        ));
    }
}