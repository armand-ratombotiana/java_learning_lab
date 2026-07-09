package com.apex.apt;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ApexPageRenderer {
    public record Page(int id, String name, String theme, Map<String, Region> regions) {}
    public record Region(String name, String type, String sourceType, String source) {}
    public record RenderedPage(String html, int pageId, long renderTimeMs) {}

    private final Map<Integer, Page> pageCache = new ConcurrentHashMap<>();
    private final List<String> renderLog = new ArrayList<>();

    public void registerPage(Page p) { pageCache.put(p.id(), p); }

    public RenderedPage renderPage(int pageId, Map<String, String> sessionState) {
        long start = System.currentTimeMillis();
        var page = pageCache.get(pageId);
        if (page == null) throw new IllegalArgumentException("Page not found: " + pageId);

        var sb = new StringBuilder();
        sb.append("<!DOCTYPE html><html><head><title>").append(page.name()).append("</title>");
        sb.append("<link rel=\"stylesheet\" href=\"#").append(page.theme()).append("/css\"></head><body>");

        for (var region : page.regions().values()) {
            sb.append("<div class=\"region-").append(region.name()).append("\">");
            sb.append(renderRegion(region, sessionState));
            sb.append("</div>");
        }
        sb.append("</body></html>");

        long elapsed = System.currentTimeMillis() - start;
        renderLog.add("Page " + pageId + " rendered in " + elapsed + "ms");
        return new RenderedPage(sb.toString(), pageId, elapsed);
    }

    private String renderRegion(Region region, Map<String, String> sessionState) {
        return switch (region.type()) {
            case "STATIC" -> "<div class=\"static-content\">" + region.source() + "</div>";
            case "REPORT" -> "<table class=\"apex-report\"><tr><td>Report: " + region.source() + "</td></tr></table>";
            case "CHART" -> "<div class=\"apex-chart\" data-series=\"" + region.source() + "\"></div>";
            case "PLSQL" -> "<div class=\"plsql-region\">Processed: " + region.source() + "</div>";
            case "FORM" -> "<form>" + region.source() + "</form>";
            default -> "<div class=\"unknown-region\">" + region.type() + "</div>";
        };
    }

    public Page createPage(int id, String name, String theme) {
        return new Page(id, name, theme, new LinkedHashMap<>());
    }

    public Region createRegion(String name, String type, String sourceType, String source) {
        return new Region(name, type, sourceType, source);
    }

    public List<String> getRenderLog() { return List.copyOf(renderLog); }
    public void clearLog() { renderLog.clear(); }

    public static ApexPageRenderer createSamplePage() {
        var renderer = new ApexPageRenderer();
        var page = renderer.createPage(1, "Dashboard", "Universal Theme 42");
        page.regions().put("emp_region", renderer.createRegion("Employees", "REPORT", "SQL", "SELECT * FROM employees"));
        page.regions().put("chart_region", renderer.createRegion("Sales Chart", "CHART", "SQL", "SELECT region,SUM(sales) FROM sales GROUP BY region"));
        page.regions().put("header", renderer.createRegion("Page Header", "STATIC", "STATIC", "<h1>Dashboard</h1>"));
        renderer.registerPage(page);
        return renderer;
    }
}