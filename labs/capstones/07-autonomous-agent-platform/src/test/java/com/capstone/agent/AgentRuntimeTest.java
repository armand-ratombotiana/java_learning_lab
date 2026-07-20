package com.capstone.agent;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class AgentRuntimeTest {
    private ToolRegistry tools;
    private AgentMemory memory;
    private PlanningEngine planner;
    private AgentRuntime agent;

    @BeforeEach
    void setUp() {
        tools = ToolRegistry.createDefaultTools();
        memory = new AgentMemory();
        planner = new PlanningEngine(tools);
        agent = new AgentRuntime("agent-1", tools, memory, planner);
    }

    @Test void testExecuteStep() {
        var step = agent.executeStep("search for Java tutorials");
        assertNotNull(step);
        assertTrue(step.stepNumber() > 0);
    }

    @Test void testRunLoop() {
        agent.runLoop("calculate 2+2", 3);
        assertTrue(agent.getStepCount() > 0);
    }

    @Test void testStartStop() {
        agent.start();
        assertTrue(agent.isRunning());
        agent.stop();
        assertFalse(agent.isRunning());
    }

    @Test void testStateManagement() {
        agent.setState("key", "value");
        assertEquals("value", agent.getState().get("key"));
    }

    @Test void testStepContainsAllData() {
        var step = agent.executeStep("remember test=value");
        assertNotNull(step.thought());
        assertNotNull(step.action());
        assertNotNull(step.timestamp());
    }

    @Test void testClear() {
        agent.executeStep("test");
        agent.clear();
        assertEquals(0, agent.getStepCount());
    }
}
