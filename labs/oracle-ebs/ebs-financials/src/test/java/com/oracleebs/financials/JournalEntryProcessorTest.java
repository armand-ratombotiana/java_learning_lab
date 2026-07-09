package com.oracleebs.financials;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

class JournalEntryProcessorTest {
    private JournalEntryProcessor processor;

    @BeforeEach
    void setUp() {
        processor = JournalEntryProcessor.createDefault();
    }

    @Test
    void testCreateJournal() {
        var je = processor.createJournal("BATCH1", "JOURNAL1", LocalDate.of(2026, 1, 15), "USD");
        assertNotNull(je);
        assertEquals(JournalEntryProcessor.JournalStatus.DRAFT, je.getStatus());
    }

    @Test
    void testPostBalancedJournal() {
        var je = processor.createJournal("BATCH1", "J1", LocalDate.of(2026, 1, 15), "USD");
        je.addLine(new JournalEntryProcessor.JournalLine("01-000-1010", BigDecimal.valueOf(1000), true, "Cash"));
        je.addLine(new JournalEntryProcessor.JournalLine("01-000-2020", BigDecimal.valueOf(1000), false, "Revenue"));
        var result = processor.validateAndPost("J1");
        assertTrue(result.success());
        assertEquals(JournalEntryProcessor.JournalStatus.POSTED, je.getStatus());
    }

    @Test
    void testRejectUnbalancedJournal() {
        var je = processor.createJournal("BATCH1", "J2", LocalDate.of(2026, 1, 15), "USD");
        je.addLine(new JournalEntryProcessor.JournalLine("01-000-1010", BigDecimal.valueOf(500), true, "Cash"));
        je.addLine(new JournalEntryProcessor.JournalLine("01-000-2020", BigDecimal.valueOf(400), false, "Revenue"));
        var result = processor.validateAndPost("J2");
        assertFalse(result.success());
        assertEquals(JournalEntryProcessor.JournalStatus.ERROR, je.getStatus());
    }

    @Test
    void testRejectInvalidAccount() {
        var je = processor.createJournal("BATCH1", "J3", LocalDate.of(2026, 1, 15), "USD");
        je.addLine(new JournalEntryProcessor.JournalLine("INVALID-ACCT", BigDecimal.valueOf(100), true, "Bad"));
        je.addLine(new JournalEntryProcessor.JournalLine("01-000-1010", BigDecimal.valueOf(100), false, "Cash"));
        var result = processor.validateAndPost("J3");
        assertFalse(result.success());
    }

    @Test
    void testValidateNonexistentJournal() {
        var result = processor.validateAndPost("NONEXISTENT");
        assertFalse(result.success());
    }

    @Test
    void testJournalContainsLines() {
        var je = processor.createJournal("BATCH1", "J4", LocalDate.of(2026, 1, 15), "USD");
        je.addLine(new JournalEntryProcessor.JournalLine("01-000-1010", BigDecimal.ONE, true, "Test"));
        assertEquals(1, je.getLines().size());
    }
}
