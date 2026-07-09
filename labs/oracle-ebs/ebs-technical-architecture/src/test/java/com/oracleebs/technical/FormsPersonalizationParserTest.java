package com.oracleebs.technical;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

class FormsPersonalizationParserTest {
    private FormsPersonalizationParser parser;

    @BeforeEach
    void setUp() {
        parser = FormsPersonalizationParser.createDefault();
    }

    @Test
    void testDefaultRulesExist() {
        assertEquals(2, parser.getAllRules().size());
    }

    @Test
    void testGetRulesForForm() {
        var rules = parser.getRulesForForm("APXINWKB");
        assertEquals(1, rules.size());
        assertEquals("RULE001", rules.get(0).getRuleId());
    }

    @Test
    void testParseRuleString() {
        var rule = parser.parseRule("RULE R003 FORM POXPOEPO EVENT WHEN-NEW-FORM-INSTANCE IF \"true\" ACTION SET_ITEM_PROPERTY");
        assertNotNull(rule);
        assertEquals("R003", rule.getRuleId());
    }

    @Test
    void testInvalidRuleString() {
        assertThrows(IllegalArgumentException.class, () -> parser.parseRule("INVALID STRING"));
    }

    @Test
    void testApplyRule() {
        assertTrue(parser.applyRule("APXINWKB", "WHEN-NEW-FORM-INSTANCE", Map.of()));
    }
}
