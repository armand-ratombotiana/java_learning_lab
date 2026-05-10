package com.learning.serverless;

import com.amazonaws.services.lambda.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ServerlessSolutionTest {

    private ServerlessSolution solution;

    @BeforeEach
    void setUp() {
        solution = new ServerlessSolution();
    }

    @Test
    void testCreateFunction() {
        CreateFunctionRequest req = solution.createFunction("my-function", "java11", "com.example.Handler", "arn:aws:iam::123456789:role/lambda-role");
        assertEquals("my-function", req.getFunctionName());
        assertEquals("java11", req.getRuntime());
        assertEquals("com.example.Handler", req.getHandler());
    }

    @Test
    void testCreateInvokeRequest() {
        InvokeRequest req = solution.createInvokeRequest("my-function", "{\"key\":\"value\"}");
        assertEquals("my-function", req.getFunctionName());
    }

    @Test
    void testCreateEnvironment() {
        Map<String, String> env = solution.createEnvironment(Map.of("KEY", "value"));
        assertEquals("value", env.get("KEY"));
    }

    @Test
    void testWithTimeout() {
        CreateFunctionRequest req = solution.createFunction("my-function", "java11", "handler", "role");
        req = solution.withTimeout(req, 30);
        assertEquals(30, req.getTimeout());
    }

    @Test
    void testWithMemory() {
        CreateFunctionRequest req = solution.createFunction("my-function", "java11", "handler", "role");
        req = solution.withMemory(req, 512);
        assertEquals(512, req.getMemorySize());
    }

    @Test
    void testCreateS3Trigger() {
        AddPermissionRequest req = solution.createS3Trigger("my-function", "my-bucket", "s3-trigger");
        assertEquals("s3-trigger", req.getStatementId());
    }

    @Test
    void testCreateApiGatewayResponse() {
        Map<String, Object> response = solution.createApiGatewayResponse(200, "{\"message\":\"success\"}");
        assertEquals(200, response.get("statusCode"));
    }
}