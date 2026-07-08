package com.cloud.awssrvls;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;

class LambdaSimulatorTest {
    @Test void testStepFunctionWorkflow() {
        var workflow = new LambdaSimulator.StepFunctionWorkflow();
        workflow.addState(new LambdaSimulator.StepFunctionWorkflow.State("Start", "Pass", "End",
            Map.of("result", Map.of("status", "started"))));
        workflow.addState(new LambdaSimulator.StepFunctionWorkflow.State("End", "Pass", null, Map.of()));
        var path = workflow.execute(Map.of("input", "test"));
        assertEquals(2, path.size());
        assertEquals("Start", path.get(0));
        assertEquals("End", path.get(1));
    }

    @Test void testEventBridgeBus() {
        var bus = new LambdaSimulator.EventBridgeBus();
        var results = new java.util.ArrayList<String>();
        bus.subscribe("order.created", event -> results.add((String) event.get("orderId")));
        bus.publish("order.created", Map.of("orderId", "123"));
        assertEquals(1, results.size());
        assertEquals("123", results.get(0));
    }

    @Test void testEventBridgeNoSubscribers() {
        var bus = new LambdaSimulator.EventBridgeBus();
        bus.publish("unknown.event", Map.of("key", "val"));
    }
}
