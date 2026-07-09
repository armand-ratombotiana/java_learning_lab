package com.oracleebs.setup;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class FlexfieldConfigurator {
    public enum FlexfieldType { KEY, DESCRIPTIVE }

    public static class FlexfieldSegment {
        private final String name;
        private final int position;
        private final String valueSet;
        private final boolean required;
        private final String defaultValue;

        public FlexfieldSegment(String name, int position, String valueSet, boolean required, String defaultValue) {
            this.name = name;
            this.position = position;
            this.valueSet = valueSet;
            this.required = required;
            this.defaultValue = defaultValue;
        }

        public String getName() { return name; }
        public int getPosition() { return position; }
        public String getValueSet() { return valueSet; }
        public boolean isRequired() { return required; }
        public String getDefaultValue() { return defaultValue; }
    }

    private final Map<String, List<FlexfieldSegment>> flexfields;

    public FlexfieldConfigurator() {
        this.flexfields = new ConcurrentHashMap<>();
    }

    public void defineFlexfield(String name, FlexfieldType type, List<FlexfieldSegment> segments) {
        flexfields.put(name + ":" + type, segments);
    }

    public Map<String, String> resolveContext(String flexfieldName, FlexfieldType type, Map<String, String> context) {
        List<FlexfieldSegment> segments = flexfields.get(flexfieldName + ":" + type);
        if (segments == null) return Map.of();
        Map<String, String> result = new LinkedHashMap<>();
        for (FlexfieldSegment seg : segments) {
            String val = context.getOrDefault(seg.getName(), seg.getDefaultValue());
            if (val == null && seg.isRequired()) {
                throw new IllegalStateException("Required segment missing: " + seg.getName());
            }
            if (val != null) result.put(seg.getName(), val);
        }
        return result;
    }

    public List<FlexfieldSegment> getSegments(String flexfieldName, FlexfieldType type) {
        return flexfields.getOrDefault(flexfieldName + ":" + type, List.of());
    }

    public boolean hasFlexfield(String name, FlexfieldType type) {
        return flexfields.containsKey(name + ":" + type);
    }

    public static FlexfieldConfigurator createDefault() {
        FlexfieldConfigurator ff = new FlexfieldConfigurator();
        ff.defineFlexfield("GL_ACCOUNT", FlexfieldType.KEY, List.of(
            new FlexfieldSegment("Company", 1, "COMPANY_VS", true, "01"),
            new FlexfieldSegment("Department", 2, "DEPT_VS", true, "000"),
            new FlexfieldSegment("Account", 3, "ACCOUNT_VS", true, null),
            new FlexfieldSegment("Sub-Account", 4, "SUB_ACCT_VS", false, "0")
        ));
        ff.defineFlexfield("INV_ITEM", FlexfieldType.DESCRIPTIVE, List.of(
            new FlexfieldSegment("Color", 1, "COLOR_VS", false, "Default"),
            new FlexfieldSegment("Size", 2, "SIZE_VS", false, "M")
        ));
        return ff;
    }
}
