package com.learning.gatling;

import io.gatling.core.session.Session;
import io.gatling.core.Predef.*;
import io.gatling.http.Predef.*;

import java.util.Map;

public class GatlingSolution {

    public HttpRequestBuilder createGetRequest(String url) {
        return http("GET Request")
            .get(url)
            .check(statusIn(200, 201));
    }

    public HttpRequestBuilder createPostRequest(String url, String body) {
        return http("POST Request")
            .post(url)
            .header("Content-Type", "application/json")
            .body(StringBody(body))
            .check(statusIs(201));
    }

    public HttpRequestBuilder createPutRequest(String url, String body) {
        return http("PUT Request")
            .put(url)
            .header("Content-Type", "application/json")
            .body(StringBody(body))
            .check(statusIn(200, 204));
    }

    public HttpRequestBuilder createDeleteRequest(String url) {
        return http("DELETE Request")
            .delete(url)
            .check(statusIs(204));
    }

    public ChainBuilder createScenario(String name, HttpRequestBuilder... requests) {
        return exec(requests[0]);
    }

    public ScenarioBuilder createLoadScenario(String name, ChainBuilder chain) {
        return scenario(name).exec(chain);
    }

    public feeders.FeederBuilder createCsvFeeder(String filePath) {
        return csv(filePath).random();
    }

    public io.gatling.core.structure.PopulationBuilder createSimulation(
            ScenarioBuilder scenario, int users, int duration) {
        return scenario.injectOpen(
            rampUsers(users).during(duration)
        );
    }

    public Session setSessionVariable(Session session, String key, Object value) {
        return session.set(key, value);
    }

    public Object getSessionVariable(Session session, String key) {
        return session.get(key);
    }
}