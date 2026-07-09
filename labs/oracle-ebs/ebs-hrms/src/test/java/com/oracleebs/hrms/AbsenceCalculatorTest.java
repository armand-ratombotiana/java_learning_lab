package com.oracleebs.hrms;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDate;

class AbsenceCalculatorTest {
    private AbsenceCalculator calc;

    @BeforeEach
    void setUp() {
        calc = AbsenceCalculator.createDefault();
    }

    @Test
    void testRecordAbsence() {
        var entry = calc.recordAbsence("ABS001", "E001", AbsenceCalculator.AbsenceType.VACATION,
            LocalDate.of(2026, 6, 1), LocalDate.of(2026, 6, 5), "Family vacation");
        assertNotNull(entry);
        assertEquals(AbsenceCalculator.AbsenceStatus.SUBMITTED, entry.getStatus());
        assertEquals(5, entry.getDurationDays());
    }

    @Test
    void testApproveAbsenceWithinEntitlement() {
        calc.recordAbsence("ABS001", "E001", AbsenceCalculator.AbsenceType.VACATION,
            LocalDate.of(2026, 6, 1), LocalDate.of(2026, 6, 5), "Vacation");
        assertTrue(calc.approveAbsence("ABS001"));
    }

    @Test
    void testRejectAbsenceExceedingEntitlement() {
        calc.recordAbsence("ABS002", "E001", AbsenceCalculator.AbsenceType.VACATION,
            LocalDate.of(2026, 6, 1), LocalDate.of(2026, 7, 15), "Long vacation");
        assertFalse(calc.approveAbsence("ABS002"));
    }

    @Test
    void testNonexistentAbsence() {
        assertFalse(calc.approveAbsence("NONEXISTENT"));
    }

    @Test
    void testTotalAbsenceDays() {
        calc.recordAbsence("ABS001", "E001", AbsenceCalculator.AbsenceType.SICKNESS,
            LocalDate.of(2026, 3, 1), LocalDate.of(2026, 3, 3), "Flu");
        calc.approveAbsence("ABS001");
        assertEquals(3, calc.getTotalAbsenceDays("E001", AbsenceCalculator.AbsenceType.SICKNESS, AbsenceCalculator.AbsenceStatus.APPROVED));
    }
}
