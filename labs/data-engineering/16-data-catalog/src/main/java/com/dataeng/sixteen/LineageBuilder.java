package com.dataeng.sixteen;

import java.util.*;

public class LineageBuilder {
    private final List<String> sources = new ArrayList<>();
    private final List<String> transformations = new ArrayList<>();
    private final List<String> targets = new ArrayList<>();

    public LineageBuilder addSource(String source) { sources.add(source); return this; }
    public LineageBuilder addTransformation(String transformation) { transformations.add(transformation); return this; }
    public LineageBuilder addTarget(String target) { targets.add(target); return this; }

    public String build() {
        var sb = new StringBuilder();
        sb.append("{"sources":[");
        sb.append(String.join(",", sources.stream().map(s -> "\"" + s + "\"").toList()));
        sb.append("],"transformations":[");
        sb.append(String.join(",", transformations.stream().map(t -> "\"" + t + "\"").toList()));
        sb.append("],"targets":[");
        sb.append(String.join(",", targets.stream().map(t -> "\"" + t + "\"").toList()));
        sb.append("]}");
        return sb.toString();
    }

    public boolean isValid() {
        return !sources.isEmpty() && !targets.isEmpty();
    }
}
