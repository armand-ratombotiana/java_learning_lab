package com.capstone.agent;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class MultiAgentOrchestratorTest {
    private MultiAgentOrchestrator orchestrator;
    private AgentRuntime agent1;
    private AgentRuntime agent2;

    @BeforeEach
    void setUp() {
        orchestrator = new MultiAgentOrchestrator();
        var tools = ToolRegistry.createDefaultTools();
        var memory = new AgentMemory();
        var planner = new PlanningEngine(tools);
        agent1 = new AgentRuntime("agent-1", tools, memory, planner);
        agent2 = new AgentRuntime("agent-2", tools, memory, planner);
        orchestrator.registerAgent("agent-1", agent1);
        orchestrator.registerAgent("agent-2", agent2);
    }

    @Test void testRegisterAgent() {
        assertEquals(2, orchestrator.agentCount());
        assertTrue(orchestrator.hasAgent("agent-1"));
    }

    @Test void testSendMessage() {
        orchestrator.sendMessage("agent-1", "agent-2", "Hello", "text");
        var messages = orchestrator.getMessages("agent-2");
        assertEquals(1, messages.size());
        assertEquals("Hello", messages.get(0).content());
    }

    @Test void testProcessMessages() {
        orchestrator.sendMessage("agent-1", "agent-2", "Task: search", "task");
        orchestrator.processMessages("agent-2");
        assertTrue(agent2.getStepCount() > 0);
    }

    @Test void testUnregisterAgent() {
        orchestrator.unregisterAgent("agent-1");
        assertEquals(1, orchestrator.agentCount());
    }

    @Test void testShutdown() {
        orchestrator.shutdown();
        assertEquals(0, orchestrator.agentCount());
    }
}
