package com.oracleebs.hrms;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;
import java.math.BigDecimal;
import java.time.LocalDate;

class PayrollEngineTest {
    private PayrollEngine engine;

    @BeforeEach
    void setUp() {
        engine = PayrollEngine.createDefault();
    }

    @Test
    void testMonthlyPayroll() {
        var result = engine.runPayroll("E001", "JAN-2026");
        assertEquals("E001", result.getEmployeeId());
        assertEquals(BigDecimal.valueOf(10000.00).setScale(2), result.getGrossPay());
    }

    @Test
    void testBiweeklyPayroll() {
        var result = engine.runPayroll("E002", "PERIOD-01");
        BigDecimal expectedGross = BigDecimal.valueOf(75000).divide(BigDecimal.valueOf(26), 2, BigDecimal.ROUND_HALF_UP);
        assertEquals(expectedGross, result.getGrossPay());
    }

    @Test
    void testTaxCalculation() {
        var result = engine.runPayroll("E001", "JAN-2026");
        assertTrue(result.getTaxWithheld().compareTo(BigDecimal.ZERO) > 0);
    }

    @Test
    void testNetPay() {
        var result = engine.runPayroll("E003", "PERIOD-01");
        assertEquals(result.getGrossPay().subtract(result.getTaxWithheld()), result.getNetPay());
    }

    @Test
    void testInvalidEmployee() {
        assertThrows(IllegalArgumentException.class, () -> engine.runPayroll("NONEXISTENT", "JAN-2026"));
    }

    @Test
    void testPayrollElements() {
        var result = engine.runPayroll("E001", "JAN-2026");
        assertTrue(result.getElements().containsKey("Base Salary"));
        assertTrue(result.getElements().containsKey("Tax Withholding"));
    }
}
