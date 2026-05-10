package com.learning.serverless;

import com.amazonaws.services.lambda.AWSLambda;
import com.amazonaws.services.lambda.model.*;

import java.util.HashMap;
import java.util.Map;

public class ServerlessSolution {

    public CreateFunctionRequest createFunction(String functionName, String runtime, String handler, String role) {
        return new CreateFunctionRequest()
                .withFunctionName(functionName)
                .withRuntime(runtime)
                .withHandler(handler)
                .withRole(role)
                .withDescription("Lambda function created via Java");
    }

    public InvokeRequest createInvokeRequest(String functionName, String payload) {
        return new InvokeRequest()
                .withFunctionName(functionName)
                .withPayload(payload);
    }

    public String extractResponse(InvokeResult result) {
        return result.getPayload().toString();
    }

    public Map<String, Object> parseEvent(Map<String, Object> event) {
        return event;
    }

    public Map<String, String> createEnvironment(Map<String, String> variables) {
        return variables;
    }

    public CreateFunctionRequest withEnvironment(CreateFunctionRequest request, Map<String, String> env) {
        return request.withEnvironment(new Environment().withVariables(env));
    }

    public CreateFunctionRequest withTimeout(CreateFunctionRequest request, int seconds) {
        return request.withTimeout(seconds);
    }

    public CreateFunctionRequest withMemory(CreateFunctionRequest request, int memoryMB) {
        return request.withMemorySize(memoryMB);
    }

    public AddPermissionRequest createS3Trigger(String functionName, String bucketName, String statementId) {
        return new AddPermissionRequest()
                .withFunctionName(functionName)
                .withStatementId(statementId)
                .withAction("lambda:InvokeFunction")
                .withPrincipal("s3.amazonaws.com")
                .withSourceArn("arn:aws:s3:::" + bucketName);
    }

    public CreateFunctionRequest withS3Event(CreateFunctionRequest request, String bucketName) {
        Map<String, Object> envVars = request.getEnvironment() != null 
            ? request.getEnvironment().getVariables() 
            : new HashMap<>();
        envVars.put("S3_BUCKET", bucketName);
        return request.withEnvironment(new Environment().withVariables(envVars));
    }

    public CreateFunctionRequest withAPIGatewayTrigger(CreateFunctionRequest request, String path, String method) {
        Map<String, Object> envVars = request.getEnvironment() != null 
            ? request.getEnvironment().getVariables() 
            : new HashMap<>();
        envVars.put("API_PATH", path);
        envVars.put("API_METHOD", method);
        return request.withEnvironment(new Environment().withVariables(envVars));
    }

    public Map<String, Object> createApiGatewayResponse(int statusCode, String body) {
        Map<String, Object> response = new HashMap<>();
        response.put("statusCode", statusCode);
        response.put("body", body);
        return response;
    }
}