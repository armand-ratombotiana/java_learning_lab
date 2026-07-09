package com.databases.plsqladv;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;

class AdvancedPlSqlAnalyserTest {
    private AdvancedPlSqlAnalyser analyser;
    @BeforeEach void setUp() { analyser = AdvancedPlSqlAnalyser.createSample(); }

    @Test void shouldRegisterPipelinedFunc() {
        var result = analyser.simulatePipelinedExecution("get_employees_pipe", 10, 25);
        assertEquals(25, result.size());
    }

    @Test void shouldManageCache() {
        var result = analyser.cacheResult("test_key", () -> "computed_value", 5000);
        assertEquals("computed_value", result);
        assertEquals(1, analyser.cacheSize());
    }

    @Test void shouldReturnCachedValue() {
        var r1 = analyser.cacheResult("k", () -> "v1", 10000);
        var r2 = analyser.cacheResult("k", () -> "v2", 10000);
        assertEquals("v1", r2); // still cached
    }

    @Test void shouldScheduleJob() {
        var msg = analyser.scheduleJob(new AdvancedPlSqlAnalyser.ScheduleJob("nightly_purge", "purge_proc", "DAILY", true));
        assertTrue(msg.contains("ENABLED"));
    }

    @Test void shouldGenerateVpdPredicate() {
        var pred = analyser.generateVpdPredicate("HR", "EMPLOYEES");
        assertEquals("emp_sec_function()", pred);
    }
}