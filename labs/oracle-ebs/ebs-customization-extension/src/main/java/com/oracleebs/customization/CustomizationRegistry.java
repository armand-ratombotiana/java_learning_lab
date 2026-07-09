package com.oracleebs.customization;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class CustomizationRegistry {
    public enum CustomizationType { CONFIGURATION, EXTENSION, MODIFICATION, LOCALIZATION, INTEGRATION }

    public static class CustomizationEntry {
        private final String id;
        private final String name;
        private final CustomizationType type;
        private final String module;
        private final String description;
        private final boolean active;
        private final String version;

        public CustomizationEntry(String id, String name, CustomizationType type, String module, String desc, boolean active, String version) {
            this.id = id;
            this.name = name;
            this.type = type;
            this.module = module;
            this.description = desc;
            this.active = active;
            this.version = version;
        }

        public String getId() { return id; }
        public String getName() { return name; }
        public CustomizationType getType() { return type; }
        public String getModule() { return module; }
        public String getDescription() { return description; }
        public boolean isActive() { return active; }
        public String getVersion() { return version; }
    }

    private final Map<String, CustomizationEntry> entries;

    public CustomizationRegistry() {
        this.entries = new ConcurrentHashMap<>();
    }

    public void register(CustomizationEntry entry) {
        entries.put(entry.getId(), entry);
    }

    public Optional<CustomizationEntry> getEntry(String id) {
        return Optional.ofNullable(entries.get(id));
    }

    public List<CustomizationEntry> getByType(CustomizationType type) {
        return entries.values().stream().filter(e -> e.getType() == type).toList();
    }

    public List<CustomizationEntry> getByModule(String module) {
        return entries.values().stream().filter(e -> e.getModule().equals(module)).toList();
    }

    public List<CustomizationEntry> getActiveEntries() {
        return entries.values().stream().filter(CustomizationEntry::isActive).toList();
    }

    public int getTotalCount() {
        return entries.size();
    }

    public static CustomizationRegistry createDefault() {
        CustomizationRegistry reg = new CustomizationRegistry();
        reg.register(new CustomizationEntry("C001", "Set MO Profile", CustomizationType.CONFIGURATION, "FND", "Set MO_DEFAULT_ORG_ID", true, "1.0"));
        reg.register(new CustomizationEntry("E001", "Custom Invoice Validation", CustomizationType.EXTENSION, "AP", "Add custom validation", true, "2.1"));
        reg.register(new CustomizationEntry("M001", "Seed Data Modification", CustomizationType.MODIFICATION, "PO", "Update approval limits", false, "1.0"));
        reg.register(new CustomizationEntry("L001", "Local Tax Rules", CustomizationType.LOCALIZATION, "AR", "Brazil local tax rules", true, "3.0"));
        reg.register(new CustomizationEntry("I001", "SOA Integration", CustomizationType.INTEGRATION, "OM", "Order sync via SOA", true, "2.0"));
        return reg;
    }
}
