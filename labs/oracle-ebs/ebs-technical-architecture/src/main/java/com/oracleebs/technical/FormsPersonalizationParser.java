package com.oracleebs.technical;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FormsPersonalizationParser {
    public static class PersonalizationRule {
        private final String ruleId;
        private final String formName;
        private final String triggerEvent;
        private final String condition;
        private final String actionType;
        private final Map<String, String> actionParameters;

        public PersonalizationRule(String id, String form, String event, String cond, String action, Map<String, String> params) {
            this.ruleId = id;
            this.formName = form;
            this.triggerEvent = event;
            this.condition = cond;
            this.actionType = action;
            this.actionParameters = params;
        }

        public String getRuleId() { return ruleId; }
        public String getFormName() { return formName; }
        public String getTriggerEvent() { return triggerEvent; }
        public String getCondition() { return condition; }
        public String getActionType() { return actionType; }
        public Map<String, String> getActionParameters() { return Collections.unmodifiableMap(actionParameters); }
    }

    private final Map<String, List<PersonalizationRule>> rulesByForm;
    private static final Pattern RULE_PATTERN = Pattern.compile(
        "RULE\\s+(\\S+)\\s+FORM\\s+(\\S+)\\s+EVENT\\s+(\\S+)\\s+IF\\s+\"(.+?)\"\\s+ACTION\\s+(\\S+)"
    );

    public FormsPersonalizationParser() {
        this.rulesByForm = new LinkedHashMap<>();
    }

    public PersonalizationRule parseRule(String ruleString) {
        Matcher m = RULE_PATTERN.matcher(ruleString);
        if (!m.matches()) throw new IllegalArgumentException("Invalid rule format: " + ruleString);
        return new PersonalizationRule(m.group(1), m.group(2), m.group(3),
            m.group(4), m.group(5), Map.of());
    }

    public void addRule(PersonalizationRule rule) {
        rulesByForm.computeIfAbsent(rule.getFormName(), k -> new ArrayList<>()).add(rule);
    }

    public List<PersonalizationRule> getRulesForForm(String formName) {
        return rulesByForm.getOrDefault(formName, List.of());
    }

    public List<PersonalizationRule> getAllRules() {
        return rulesByForm.values().stream().flatMap(Collection::stream).toList();
    }

    public boolean applyRule(String formName, String triggerEvent, Map<String, Object> context) {
        var rules = rulesByForm.getOrDefault(formName, List.of());
        for (PersonalizationRule rule : rules) {
            if (rule.getTriggerEvent().equals(triggerEvent)) {
                return true;
            }
        }
        return false;
    }

    public static FormsPersonalizationParser createDefault() {
        FormsPersonalizationParser parser = new FormsPersonalizationParser();
        parser.addRule(new PersonalizationRule("RULE001", "APXINWKB", "WHEN-NEW-FORM-INSTANCE",
            "true", "SET_ITEM_PROPERTY", Map.of("item", "BLOCK.INVOICE_NUM", "property", "REQUIRED", "value", "TRUE")));
        parser.addRule(new PersonalizationRule("RULE002", "POXPOEPO", "WHEN-NEW-BLOCK-INSTANCE",
            ":SYSTEM.CURRENT_BLOCK = 'PO_HEADERS'", "SET_WINDOW_TITLE", Map.of("title", "Custom PO Form")));
        return parser;
    }
}
