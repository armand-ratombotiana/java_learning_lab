package com.databases.plsql;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;

class PlSqlSimulatorTest {
    private PlSqlSimulator sim;
    @BeforeEach void setUp() { sim = new PlSqlSimulator(); }

    @Test void shouldDeclareAndResolve() {
        sim.declare("x", "NUMBER", 42);
        assertEquals(42, sim.resolve("x"));
    }

    @Test void shouldAssignNewValue() {
        sim.declare("x", "NUMBER", 10);
        sim.assign("x", 20);
        assertEquals(20, sim.resolve("x"));
    }

    @Test void shouldHandleScopes() {
        sim.pushFrame("INNER");
        sim.declare("y", "VARCHAR2", "hello");
        assertEquals("hello", sim.resolve("y"));
        sim.popFrame();
        assertNull(sim.resolve("y"));
    }

    @Test void shouldSimulatePutLine() {
        sim.declare("v_name", "VARCHAR2", "John");
        sim.declare("v_sal", "NUMBER", 5000);
        var output = sim.simulateBlock("""
            BEGIN
              DBMS_OUTPUT.PUT_LINE('Name: ' || v_name || ' Salary: ' || v_sal);
            END;
            """);
        assertTrue(output.contains("John"));
        assertTrue(output.contains("5000"));
    }
}