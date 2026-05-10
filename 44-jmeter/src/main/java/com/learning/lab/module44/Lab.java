package com.learning.lab.module44;

public class Lab {
    public static void main(String[] args) {
        System.out.println("=== Module 44: JMeter Lab ===\n");

        System.out.println("1. JMeter Configuration:");
        System.out.println("   - GUI Mode: bin/jmeter.sh");
        System.out.println("   - CLI Mode: jmeter -n -t test.jmx -l results.jtl");
        System.out.println("   - Server Mode: jmeter-server");
        System.out.println("   - Port: 1099 (RMI), 4444 (分布式)");

        System.out.println("\n2. Test Elements:");
        testElementsDemo();

        System.out.println("\n3. Thread Group:");
        threadGroupDemo();

        System.out.println("\n4. Samplers:");
        samplerDemo();

        System.out.println("\n5. Assertions:");
        assertionDemo();

        System.out.println("\n6. Listeners:");
        listenerDemo();

        System.out.println("\n7. Controllers:");
        controllerDemo();

        System.out.println("\n8. Variables and Functions:");
        variableFunctionsDemo();

        System.out.println("\n=== JMeter Lab Complete ===");
    }

    static void testElementsDemo() {
        System.out.println("   Test Plan Components:");
        System.out.println("   - Test Plan: Root element, defines overall test");
        System.out.println("   - Thread Group: Users and iterations");
        System.out.println("   - Controllers: Logic flow control");
        System.out.println("   - Samplers: HTTP, JDBC, FTP requests");
        System.out.println("   - Logic Controllers: Loop, If, While");
        System.out.println("   - Listeners: Result collection");
        System.out.println("   - Timers: Think time");
        System.out.println("   - Assertions: Response validation");
    }

    static void threadGroupDemo() {
        System.out.println("   Thread Group Settings:");
        System.out.println("   - Number of Threads: 100");
        System.out.println("   - Ramp-up Period: 10 seconds");
        System.out.println("   - Loop Count: 5");
        System.out.println("   - Same user on each iteration: true");
        System.out.println("   - Delay until next loop: 1000ms");

        System.out.println("\n   Thread Group Types:");
        System.out.println("   - Thread Group: Standard threads");
        System.out.println("   - setUp Thread Group: Pre-test actions");
        System.out.println("   - tearDown Thread Group: Post-test actions");
    }

    static void samplerDemo() {
        System.out.println("   HTTP Sampler:");
        System.out.println("   - Protocol: http/https");
        System.out.println("   - Server Name: api.example.com");
        System.out.println("   - Port: 443");
        System.out.println("   - Path: /api/users/${id}");
        System.out.println("   - Method: GET/POST/PUT/DELETE");
        System.out.println("   - Body Data: JSON payload");
        System.out.println("   - Parameters: Query parameters");

        System.out.println("\n   Other Samplers:");
        System.out.println("   - JDBC Request: Database queries");
        System.out.println("   - JMS Publisher/Subscriber: Message testing");
        System.out.println("   - FTP Request: File transfer");
        System.out.println("   - LDAP Request: Directory services");
        System.out.println("   - SOAP/XML-RPC: Web services");
    }

    static void assertionDemo() {
        System.out.println("   Assertions:");
        System.out.println("   - Response Assertion: Contains text, Matches regex");
        System.out.println("   - JSON Assertion: JSON path validation");
        System.out.println("   - XML Assertion: XML structure validation");
        System.out.println("   - Duration Assertion: Response time < 500ms");
        System.out.println("   - Size Assertion: Response size < 1MB");
        System.out.println("   - HTML Assertion: HTML validity");
        System.out.println("   - XPath Assertion: XPath expression match");
    }

    static void listenerDemo() {
        System.out.println("   Listeners:");
        System.out.println("   - View Results Tree: Request/response details");
        System.out.println("   - View Results in Table: Tabular view");
        System.out.println("   - Aggregate Report: Statistics summary");
        System.out.println("   - Summary Report: Quick summary");
        System.out.println("   - Graph Results: Visual graphs");
        System.out.println("   - Response Time Graph: Time series");
        System.out.println("   - Simple Data Writer: CSV export");
    }

    static void controllerDemo() {
        System.out.println("   Controllers:");
        System.out.println("   - Simple Controller: Grouping");
        System.out.println("   - Loop Controller: Repeat N times");
        System.out.println("   - ForEach Controller: Iterate variables");
        System.out.println("   - If Controller: Conditional execution");
        System.out.println("   - While Controller: While condition");
        System.out.println("   - Switch Controller: Random case selection");
        System.out.println("   - Interleave Controller: Alternate requests");
        System.out.println("   - Random Controller: Random selection");
    }

    static void variableFunctionsDemo() {
        System.out.println("   Variables:");
        System.out.println("   - User Defined Variables: Static values");
        System.out.println("   - CSV Data Set Config: External data");
        System.out.println("   - JSON Extractor: Extract from JSON");
        System.out.println("   - XPath Extractor: Extract from XML");
        System.out.println("   - Boundary Extractor: Pattern matching");

        System.out.println("\n   Functions:");
        System.out.println("   - \${__time()}: Current timestamp");
        System.out.println("   - \${__Random(1,100)}: Random number");
        System.out.println("   - \${__counter(TRUE)}: Counter");
        System.out.println("   - \${__property(property)}: JMeter property");
        System.out.println("   - \${__groovy(script)}: Groovy script");
    }
}