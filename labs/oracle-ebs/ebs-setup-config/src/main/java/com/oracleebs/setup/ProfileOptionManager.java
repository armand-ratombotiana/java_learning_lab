package com.oracleebs.setup;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ProfileOptionManager {
    public enum ProfileLevel { SITE, APPLICATION, RESPONSIBILITY, USER }

    public static class ProfileValue {
        private final String optionName;
        private final ProfileLevel level;
        private final String levelValue;
        private final String value;

        public ProfileValue(String optionName, ProfileLevel level, String levelValue, String value) {
            this.optionName = optionName;
            this.level = level;
            this.levelValue = levelValue;
            this.value = value;
        }

        public String getOptionName() { return optionName; }
        public ProfileLevel getLevel() { return level; }
        public String getLevelValue() { return levelValue; }
        public String getValue() { return value; }
    }

    private final Map<String, List<ProfileValue>> profileStore;

    public ProfileOptionManager() {
        this.profileStore = new ConcurrentHashMap<>();
    }

    public void setProfile(String name, ProfileLevel level, String levelValue, String value) {
        ProfileValue pv = new ProfileValue(name, level, levelValue, value);
        profileStore.computeIfAbsent(name, k -> new ArrayList<>()).add(pv);
    }

    public Optional<String> resolveProfile(String name, ProfileLevel level, String levelValue) {
        var values = profileStore.get(name);
        if (values == null) return Optional.empty();
        return values.stream()
            .filter(v -> v.getLevel() == level && v.getLevelValue().equals(levelValue))
            .findFirst()
            .map(ProfileValue::getValue);
    }

    public String resolveEffectiveValue(String name, String userId, String responsibilityId, String applicationId) {
        var values = profileStore.get(name);
        if (values == null) return null;
        return values.stream()
            .sorted((a, b) -> Integer.compare(b.getLevel().ordinal(), a.getLevel().ordinal()))
            .findFirst()
            .map(ProfileValue::getValue)
            .orElse(null);
    }

    public List<ProfileValue> getAllProfiles() {
        return profileStore.values().stream().flatMap(Collection::stream).toList();
    }

    public void clearProfile(String name) {
        profileStore.remove(name);
    }

    public static ProfileOptionManager createDefault() {
        ProfileOptionManager mgr = new ProfileOptionManager();
        mgr.setProfile("MO_DEFAULT_ORG_ID", ProfileLevel.SITE, "SITE", "101");
        mgr.setProfile("FND_COLOR_SCHEME", ProfileLevel.USER, "OPERATIONS", "BLUE");
        mgr.setProfile("ICX_SESSION_TIMEOUT", ProfileLevel.APPLICATION, "ERP", "30");
        mgr.setProfile("FND_ENCRYPTED_COLUMNS", ProfileLevel.SITE, "SITE", "N");
        mgr.setProfile("APPS_FRAMEWORK_AGENT", ProfileLevel.SITE, "SITE", "http://ebsserver:8000");
        return mgr;
    }
}
