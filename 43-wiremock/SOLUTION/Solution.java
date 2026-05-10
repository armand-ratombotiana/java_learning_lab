package com.learning.wiremock;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.stubbing.Scenario;

import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

public class WireMockSolution {

    private WireMockServer wireMockServer;

    public WireMockSolution(WireMockServer server) {
        this.wireMockServer = server;
    }

    public void stubGetRequest(String url, int status, String responseBody) {
        wireMockServer.stubFor(get(urlEqualTo(url))
            .willReturn(aResponse()
                .withStatus(status)
                .withBody(responseBody)));
    }

    public void stubPostRequest(String url, String requestBody, int status, String responseBody) {
        wireMockServer.stubFor(post(urlEqualTo(url))
            .withRequestBody(equalToJson(requestBody))
            .willReturn(aResponse()
                .withStatus(status)
                .withBody(responseBody)));
    }

    public void stubWithHeaders(String url, Map<String, String> headers, String response) {
        wireMockServer.stubFor(get(urlEqualTo(url))
            .withHeader("Content-Type", equalTo(headers.get("Content-Type")))
            .willReturn(aResponse()
                .withStatus(200)
                .withBody(response)));
    }

    public void stubWithDelay(String url, int delayMs, String response) {
        wireMockServer.stubFor(get(urlEqualTo(url))
            .willReturn(aResponse()
                .withStatus(200)
                .withBody(response)
                .withFixedDelay(delayMs)));
    }

    public void stubStateful(String url, String state1Response, String state2Response) {
        wireMockServer.stubFor(get(urlEqualTo(url))
            .inScenario("State transition")
            .whenScenarioStateIs(Scenario.STARTED)
            .willReturn(aResponse().withBody(state1Response))
            .willSetStateTo("State2"));

        wireMockServer.stubFor(get(urlEqualTo(url))
            .inScenario("State transition")
            .whenScenarioStateIs("State2")
            .willReturn(aResponse().withBody(state2Response)));
    }

    public void verifyRequest(String url, int count) {
        wireMockServer.verify(1, getRequestedFor(urlEqualTo(url)));
    }
}