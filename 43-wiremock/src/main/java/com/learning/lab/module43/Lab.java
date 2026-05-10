package com.learning.lab.module43;

import com.github.tomakehurst.wiremock.*;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.stubbing.Scenario;

import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

public class Lab {
    public static void main(String[] args) {
        System.out.println("=== Module 43: WireMock Lab ===\n");

        WireMockServer wireMockServer = new WireMockServer(8080);
        wireMockServer.start();

        System.out.println("1. HTTP Mocking:");
        System.out.println("   - Server Port: 8080");
        System.out.println("   - Admin URL: http://localhost:8080/__admin");

        System.out.println("\n2. Stubbing:");
        basicStubbing(wireMockServer);

        System.out.println("\n3. Response Templates:");
        responseTemplates(wireMockServer);

        System.out.println("\n4. Request Matching:");
        requestMatching(wireMockServer);

        System.out.println("\n5. Stateful Behavior:");
        statefulBehavior(wireMockServer);

        System.out.println("\n6. Proxying:");
        proxying(wireMockServer);

        wireMockServer.stop();
        System.out.println("\n=== WireMock Lab Complete ===");
    }

    static void basicStubbing(WireMockServer server) {
        server.stubFor(get(urlEqualTo("/api/hello"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBody("Hello, World!")
                        .withHeader("Content-Type", "application/json")));

        server.stubFor(get(urlEqualTo("/api/users/1"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBody("{\"id\":1,\"name\":\"John\"}")
                        .withHeader("Content-Type", "application/json")));

        server.stubFor(post(urlEqualTo("/api/users"))
                .withRequestBody(containing("name"))
                .willReturn(created()
                        .withBody("{\"id\":2,\"name\":\"Created\"}")));

        System.out.println("   Basic stubs created");
    }

    static void responseTemplates(WireMockServer server) {
        server.stubFor(get(urlEqualTo("/api/template"))
                .willReturn(ok()
                        .withBody("{{jsonPath request.body '$.name'}}")
                        .withTransformers("response-template")));

        server.stubFor(get(urlEqualTo("/api/random"))
                .willReturn(ok()
                        .withBody("Random: {{randomValue type='UUID'}}")));

        server.stubFor(get(urlEqualTo("/api/date"))
                .willReturn(ok()
                        .withBody("Date: {{now format='yyyy-MM-dd'}}")));

        System.out.println("   Response templates created");
    }

    static void requestMatching(WireMockServer server) {
        server.stubFor(get(urlPathEqualTo("/api/search"))
                .withQueryParam("q", equalTo("test"))
                .willReturn(ok("Results for: test")));

        server.stubFor(get(urlMatching("/api/product/\\d+"))
                .willReturn(ok("Product ID matched")));

        server.stubFor(post(urlEqualTo("/api/submit"))
                .withHeader("Content-Type", containing("json"))
                .willReturn(ok("JSON accepted")));

        server.stubFor(get(urlEqualTo("/api/headers"))
                .withHeader("Authorization", matching("Bearer .*"))
                .willReturn(ok("Authenticated")));

        System.out.println("   Request matchers created");
    }

    static void statefulBehavior(WireMockServer server) {
        server.stubFor(get(urlEqualTo("/api/order/status"))
                .inScenario("Order Lifecycle")
                .whenScenarioStateIs(Scenario.STARTED)
                .willReturn(ok("Order placed"))
                .willSetStateTo("Processing"));

        server.stubFor(get(urlEqualTo("/api/order/status"))
                .inScenario("Order Lifecycle")
                .whenScenarioStateIs("Processing")
                .willReturn(ok("Order shipped"))
                .willSetStateTo("Shipped"));

        server.stubFor(get(urlEqualTo("/api/order/status"))
                .inScenario("Order Lifecycle")
                .whenScenarioStateIs("Shipped")
                .willReturn(ok("Order delivered")));

        System.out.println("   Stateful stubs created");
    }

    static void proxying(WireMockServer server) {
        server.stubFor(get(urlEqualTo("/api/proxy"))
                .willReturn(proxyTo("http://localhost:9000")));

        server.stubFor(post(urlEqualTo("/api/preserve"))
                .willReturn(proxyTo("http://localhost:9000")
                        .withProxyHostHeader("localhost")));

        System.out.println("   Proxy stubs created");
    }
}