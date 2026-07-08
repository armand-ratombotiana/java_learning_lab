package com.security11;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

class SamlAssertionProcessorTest {
    private SamlAssertionProcessor processor;

    @BeforeEach
    void setUp() {
        processor = new SamlAssertionProcessor();
    }

    @Test
    void testParseSamlResponseWithInvalidXml() {
        assertThrows(Exception.class, () -> processor.parseSamlResponse("not xml"));
    }

    @Test
    void testValidateAssertionWithNullId() {
        var assertion = new SamlAssertionProcessor.SamlAssertion();
        assertFalse(processor.validateAssertion(assertion));
    }

    @Test
    void testExtractAttributesFromEmptyAssertion() {
        var assertion = new SamlAssertionProcessor.SamlAssertion();
        var attrs = processor.extractAttributes(assertion);
        assertTrue(attrs.isEmpty());
    }
}
