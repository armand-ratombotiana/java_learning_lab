package com.learning.lab.module45;

import io.gatling.core仿真.*;
import io.gatling.core.session.*;
import io.gatling.http.protocol.*;
import io.gatling.http.请求.*;

import static io.gatling.core.dsl.Expressions.*;
import static io.gatling.http.请求.dsl.*;

public class Lab {
    public static void main(String[] args) {
        System.out.println("=== Module 45: Gatling Lab ===\n");

        System.out.println("1. Simulation Configuration:");
        System.out.println("   - Simulation Class: extends Simulation");
        System.out.println("   - HTTP Protocol: baseURL, headers, encoding");
        System.out.println("   - Feed: Data injection");
        System.out.println("   - Assertions: Response validation");

        System.out.println("\n2. Scenario Definition:");
        scenarioDefinitionDemo();

        System.out.println("\n3. Injection Profile:");
        injectionProfileDemo();

        System.out.println("\n4. HTTP Requests:");
        httpRequestsDemo();

        System.out.println("\n5. Checks and Extractors:");
        checksExtractorsDemo();

        System.out.println("\n6. Session and Variables:");
        sessionVariablesDemo();

        System.out.println("\n7. Loops and Conditions:");
        loopsConditionsDemo();

        System.out.println("\n8. Reports:");
        reportsDemo();

        System.out.println("\n=== Gatling Lab Complete ===");
    }

    static void scenarioDefinitionDemo() {
        System.out.println("   Scenario Creation:");
        System.out.println("   val scenario = scenario(\"My Scenario\")");
        System.out.println("       .exec(http(\"Request 1\").get(\"/api/users\"))");
        System.out.println("       .exec(http(\"Request 2\").get(\"/api/products\"))");
        System.out.println("       .pause(1)");

        System.out.println("\n   Chain of Executions:");
        System.out.println("   .exec() - HTTP request");
        System.out.println("   .pause() - Think time");
        System.out.println("   .feed() - Data injection");
        System.out.println("   .exec(session => {...}) - Custom action");
    }

    static void injectionProfileDemo() {
        System.out.println("   Injection Profiles:");
        System.out.println("   - atOnceUsers(10): 10 users immediately");
        System.out.println("   - rampUsers(100).during(30 seconds): Gradual ramp-up");
        System.out.println("   - constantUsersPerSec(10).during(60 seconds): Constant load");
        System.out.println("   - rampUsersPerSec(10).to(50).during(2 minutes): Ramp rate");
        System.out.println("   - heavisideUsers(1000).during(60 seconds): Heaviside step");

        System.out.println("\n   Combined Profile:");
        System.out.println("   setUp(");
        System.out.println("       scn.inject(rampUsers(50).during(30))");
        System.out.println("   ).protocols(httpProtocol)");
    }

    static void httpRequestsDemo() {
        System.out.println("   HTTP Request Methods:");
        System.out.println("   http(\"Name\").get(\"/path\")");
        System.out.println("   http(\"Name\").post(\"/path\")");
        System.out.println("   http(\"Name\").put(\"/path\")");
        System.out.println("   http(\"Name\").delete(\"/path\")");
        System.out.println("   http(\"Name\").patch(\"/path\")");

        System.out.println("\n   Request Parameters:");
        System.out.println("   .queryParam(\"key\", \"value\")");
        System.out.println("   .header(\"Content-Type\", \"application/json\")");
        System.out.println("   .body(StringBody(\"{ \\\"name\\\": \\\"test\\\" }\"))");
        System.out.println("   .formParam(\"username\", \"test\")");
    }

    static void checksExtractorsDemo() {
        System.out.println("   Response Checks:");
        System.out.println("   .check(status.is(200))");
        System.out.println("   .check(bodyString.contains(\"success\"))");
        System.out.println("   .check(jsonPath(\"$.name\").is(\"John\"))");
        System.out.println("   .check(css(\"h1\", \"text\").is(\"Welcome\"))");
        System.out.println("   .check(responseTimeInMillis.lte(500))");

        System.out.println("\n   Session Extraction:");
        System.out.println("   .check(jsonPath(\"$.id\").saveAs(\"userId\"))");
        System.out.println("   Session variable usage: \${userId}");
    }

    static void sessionVariablesDemo() {
        System.out.println("   Session Operations:");
        System.out.println("   session(\"key\") - Get value");
        System.out.println("   session.set(\"key\", value) - Set value");
        System.out.println("   session.remove(\"key\") - Remove");
        System.out.println("   session.validate(StringEquals, \"value\") - Validate");

        System.out.println("\n   Built-in Functions:");
        System.out.println("   \${userId} - Variable");
        System.out.println("   \${random(1,100)} - Random number");
        System.out.println("   \${timestamp()} - Current time");
        System.out.println("   \${uuid()} - UUID generation");
    }

    static void loopsConditionsDemo() {
        System.out.println("   Loops:");
        System.out.println("   .repeat(5) { exec(http(\"Request\").get(\"/\")) }");
        System.out.println("   .foreach(\"items\", \"item\") { ... }");
        System.out.println("   .doWhile(\"condition\") { ... }");
        System.out.println("   .asLongAs(\"condition\") { ... }");

        System.out.println("\n   Conditions:");
        System.out.println("   .doIf(\"\${isLogged}\") { ... }");
        System.out.println("   .doIfElse(\"\${isLogged}\") { ... } else { ... }");
        System.out.println("   .switch(\"\${userType}\") { ... }");
    }

    static void reportsDemo() {
        System.out.println("   Report Generation:");
        System.out.println("   mvn gatling:test");
        System.out.println("   java -jar gatling-charts-*-bundle.jar");

        System.out.println("\n   Report Contents:");
        System.out.println("   - Global: Total requests, response time");
        System.out.println("   - Request: Each request statistics");
        System.out.println("   - Assertions: Pass/fail status");
        System.out.println("   - Session: Session data");
    }
}