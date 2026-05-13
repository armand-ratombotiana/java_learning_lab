# JMeter - Projects

This document contains two complete projects demonstrating Apache JMeter: a mini-project for learning load testing basics and a real-world project implementing production-grade performance testing for microservices.

## Mini-Projects by Concept

### 1. Test Plan Creation (2 hours)
Create JMeter test plans with thread groups, samplers, and controllers. Configure ramp-up periods and iteration strategies.

### 2. Request Configuration (2 hours)
Configure HTTP requests with parameters, headers, and body. Handle authentication and session management.

### 3. Assertions & Validation (2 hours)
Add response assertions for status codes, response time, and content validation. Use JSON and XPath extractors.

### 4. Results Analysis (2 hours)
Analyze test results with listeners, aggregate reports, and response time graphs. Identify bottlenecks and performance issues.

### Real-world: Performance Testing Suite
Build comprehensive performance testing suite with realistic scenarios, assertions, and detailed reporting.

---

## Project 1: JMeter Basics Mini-Project

### Overview

This mini-project demonstrates fundamental JMeter concepts including thread groups, HTTP samplers, assertions, and result reporting. It serves as a learning starting point for understanding load testing fundamentals.

### Project Structure

```
jmeter-basics/
├── pom.xml
├── src/
│   └── test/
│       └── jmeter/
│           └── basics.jmx
└── results/
```

### pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
    http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    
    <groupId>com.learning</groupId>
    <artifactId>jmeter-basics</artifactId>
    <version>1.0.0</version>
    <packaging>jar</packaging>
    
    <properties>
        <java.version>21</java.version>
        <jmeter.version>5.6.3</jmeter.version>
    </properties>
    
    <dependencies>
        <dependency>
            <groupId>org.apache.jmeter</groupId>
            <artifactId>ApacheJMeter</artifactId>
            <version>${jmeter.version}</version>
        </dependency>
    </dependencies>
</project>
```

### Test Plan: basics.jmx

```xml
<?xml version="1.0" encoding="UTF-8"?>
<jmeterTestPlan version="1.2" properties="5.0" jmeter="5.6.3">
    <hashTree>
        <!-- Test Plan -->
        <TestPlan guiclass="TestPlanGui" testclass="TestPlan" 
            testname="Basics Load Test" enabled="true">
            <stringProp name="TestPlan.comments">Basic JMeter test plan</stringProp>
            <boolProp name="TestPlan.functional_mode">false</boolProp>
            <boolProp name="TestPlan.serialize_threadgroups">false</boolProp>
            <elementProp name="TestPlan.user_defined_variables" 
                elementType="Arguments" guiclass="ArgumentsPanel">
                <collectionProp name="Arguments.arguments"/>
            </elementProp>
            <stringProp name="TestPlan.user_define_classpath"></stringProp>
        </TestPlan>
        
        <hashTree>
            <!-- Thread Group: 10 users -->
            <ThreadGroup guiclass="ThreadGroupGui" testclass="ThreadGroup"
                testname="API Users" enabled="true">
                <stringProp name="ThreadGroup.on_sample_error">continue</stringProp>
                <elementProp name="ThreadGroup.main_controller" 
                    elementType="LoopController" guiclass="LoopControllerGui">
                    <boolProp name="LoopController.continue_forever">false</boolProp>
                    <stringProp name="LoopController.loops">5</stringProp>
                </elementProp>
                <stringProp name="ThreadGroup.num_threads">10</stringProp>
                <stringProp name="ThreadGroup.ramp_time">5</stringProp>
                <boolProp name="ThreadGroup.scheduler">false</boolProp>
                <stringProp name="ThreadGroup.duration"></stringProp>
                <stringProp name="ThreadGroup.delay"></stringProp>
            </ThreadGroup>
            
            <hashTree>
                <!-- HTTP Request: GET Products -->
                <HTTPSamplerProxy guiclass="HttpTestSampleGui" 
                    testclass="HTTPSamplerProxy" testname="GET Products">
                    <elementProp name="HTTPsampler.Arguments" 
                        elementType="Arguments" guiclass="HTTPArgumentsPanel">
                        <collectionProp name="Arguments.arguments"/>
                    </elementProp>
                    <stringProp name="HTTPSampler.domain">localhost</stringProp>
                    <stringProp name="HTTPSampler.port">8080</stringProp>
                    <stringProp name="HTTPSampler.protocol">http</stringProp>
                    <stringProp name="HTTPSampler.path">/api/products</stringProp>
                    <stringProp name="HTTPSampler.method">GET</stringProp>
                </HTTPSamplerProxy>
                
                <hashTree>
                    <!-- Response Assertion -->
                    <ResponseAssertion guiclass="ResponseAssertionGui"
                        testclass="ResponseAssertion" testname="Status 200">
                        <collectionProp name="Asserion.teststrings">
                            <stringProp name="Assertion.response_code">200</stringProp>
                        </collectionProp>
                        <stringProp name="Assertion.testfield">response_code</stringProp>
                    </ResponseAssertion>
                </hashTree>
                
                <hashTree>
                    <!-- JSON Assertion -->
                    <JSONPathAssertion expectedConfigName="JSON Assertion"
                        gui-class="com.atlassian.jmeter.plugins.jsonassert.
                        jsonassert.gui.JSONPathAssertionGui"
                        testname="JSON has products array">
                        <stringProp name="JSON_PATH">$.products</stringProp>
                        <stringProp name="EXPECTED_VALUE">[*]</stringProp>
                        <boolProp name="VALIDATE_RESPONSE">true</boolProp>
                    </JSONPathAssertion>
                </hashTree>
            </hashTree>
            
            <hashTree>
                <!-- HTTP Request: POST Order -->
                <HTTPSamplerProxy guiclass="HttpTestSampleGui"
                    testclass="HTTPSamplerProxy" testname="POST Order">
                    <elementProp name="HTTPsampler.Arguments"
                        elementType="Arguments" guiclass="HTTPArgumentsPanel">
                        <collectionProp name="Arguments.arguments">
                            <elementProp name="" elementType="HTTPArgument">
                                <boolProp name="HTTPArgument.always_encode">true</boolProp>
                                <stringProp name="Argument.value">{"productId":"PROD-001","quantity":1}</stringProp>
                                <stringProp name="Argument.metadata">=</stringProp>
                            </elementProp>
                        </collectionProp>
                    </elementProp>
                    <stringProp name="HTTPSampler.domain">localhost</stringProp>
                    <stringProp name="HTTPSampler.port">8080</stringProp>
                    <stringProp name="HTTPSampler.protocol">http</stringProp>
                    <stringProp name="HTTPSampler.path">/api/orders</stringProp>
                    <stringProp name="HTTPSampler.method">POST</stringProp>
                </HTTPSamplerProxy>
                
                <hashTree>
                    <HeaderManager guiclass="HeaderPanel" testclass="HeaderManager"
                        testname="HTTP Header Manager">
                        <collectionProp name="Header.headers">
                            <elementProp name="" elementType="Header">
                                <stringProp name="Header.name">Content-Type</stringProp>
                                <stringProp name="Header.value">application/json</stringProp>
                            </elementProp>
                        </collectionProp>
                    </HeaderManager>
                </hashTree>
            </hashTree>
            
            <hashTree>
                <!-- Constant Timer -->
                <ConstantTimer guiclass="ConstantTimerGui"
                    testclass="ConstantTimer" testname="Think Timer">
                    <stringProp name="ConstantTimer.delay">500</stringProp>
                </ConstantTimer>
            </hashTree>
        </hashTree>
        
        <!-- Results -->
        <ResultCollector guiclass="TableVisualizer" testclass="ResultCollector"
            testname="View Results in Table">
            <boolProp name="ResultCollector.saveResponseData">true</boolProp>
            <objProp name="filename">results/basics-results.csv</objProp>
        </ResultCollector>
        
        <hashTree>
            <SummaryReport guiclass="SummaryReportGui" testclass="SummaryReport"
                testname="Summary Report"/>
        </hashTree>
    </hashTree>
</jmeterTestPlan>
```

### JMeterRunner.java (Java API Example)

```java
package com.learning.jmeter;

import org.apache.jmeter.engine.StandardJMeterEngine;
import org.apache.jmeter.reporters.ResultCollector;
import org.apache.jmeter.reporters.Summariser;
import org.apache.jmeter.save.SaveService;
import org.apache.jmeter.util.JMeterUtils;

import java.io.File;

public class JMeterRunner {
    
    public static void main(String[] args) {
        System.out.println("=== JMeter Basics Lab ===\n");
        
        // Test Plan Components
        System.out.println("1. Thread Groups:");
        System.out.println("   - Number of threads: 10");
        System.out.println("   - Ramp-up period: 5 seconds");
        System.out.println("   - Loop count: 5");
        System.out.println("   Total requests: 10 users x 5 loops x 2 samplers = 100");
        
        System.out.println("\n2. Samplers:");
        System.out.println("   - HTTP GET: /api/products");
        System.out.println("   - HTTP POST: /api/orders");
        
        System.out.println("\n3. Assertions:");
        System.out.println("   - Response code equals 200");
        System.out.println("   - JSON path $.products exists");
        System.out.println("   - Response time < 500ms");
        
        System.out.println("\n4. Timers:");
        System.out.println("   - Constant timer: 500ms delay between requests");
        
        System.out.println("\n5. Listeners:");
        System.out.println("   - View Results in Table");
        System.out.println("   - Summary Report");
        System.out.println("   - Aggregate Report");
        
        // Simulate results
        System.out.println("\n6. Sample Results:");
        simulateResults();
        
        System.out.println("\n7. Key Metrics:");
        System.out.println("   - Average Response Time: 125ms");
        System.out.println("   - Min Response Time: 45ms");
        System.out.println("   - Max Response Time: 320ms");
        System.out.println("   - Error %: 0.0%");
        System.out.println("   - Throughput: 20 req/sec");
        
        System.out.println("\n=== Lab Complete ===");
    }
    
    private static void simulateResults() {
        String[] samplerNames = {"GET Products", "POST Order"};
        int[] responseTimes = {85, 120, 95, 150, 110, 200, 130, 100, 145, 180};
        int[] statusCodes = {200, 200, 200, 200, 200, 200, 200, 200, 200, 200};
        
        System.out.println("   Sampler        | Avg   | Min | Max | Err%");
        System.out.println("   ---------------|-------|-----|-----|-----");
        
        for (String sampler : samplerNames) {
            int sum = 0;
            for (int rt : responseTimes) sum += rt;
            int avg = sum / responseTimes.length;
            int min = java.util.Arrays.stream(responseTimes).min().orElse(0);
            int max = java.util.Arrays.stream(responseTimes).max().orElse(0);
            
            System.out.printf("   %-14s | %4dms | %3dms | %3dms | 0.0%%%n", 
                sampler, avg, min, max);
        }
    }
}
```

### Build and Run Instructions

```bash
# Using command line
jmeter -n -t basics.jmx -l results/basics-results.csv -e -o results/html-report

# Using Maven
mvn clean verify

# Run distributed test
jmeter -n -t basics.jmx -Rremote1,remote2
```

### Test Execution

```bash
# Run with specific thread count
jmeter -n -t basics.jmx -Jthread.count=50 -Jduration=300

# Generate HTML report
jmeter -g results/basics-results.csv -o results/report

# Run in non-GUI mode
jmeter -n -t test.jmx -l results.jtl
```

## Project 2: Production Performance Testing

### Overview

This real-world project implements comprehensive production-grade performance testing with multiple thread groups, database testing, distributed testing setup, and detailed analysis.

### Project Structure

```
jmeter-production/
├── pom.xml
├── scripts/
│   └── run-tests.sh
├── test-plans/
│   ├── api-load-test.jmx
│   ├── database-test.jmx
│   └── full-scenario.jmx
└── results/
```

### pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
    http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    
    <groupId>com.learning</groupId>
    <artifactId>jmeter-production</artifactId>
    <version>1.0.0</version>
    <packaging>jar</packaging>
    
    <properties>
        <java.version>21</java.version>
        <jmeter.version>5.6.3</jmeter.version>
    </properties>
    
    <dependencies>
        <dependency>
            <groupId>org.apache.jmeter</groupId>
            <artifactId>ApacheJMeter</artifactId>
            <version>${jmeter.version}</version>
        </dependency>
        <dependency>
            <groupId>org.apache.jmeter</groupId>
            <artifactId>ApacheJMeter-core</artifactId>
            <version>${jmeter.version}</version>
        </dependency>
        <dependency>
            <groupId>org.postgresql</groupId>
            <artifactId>postgresql</artifactId>
        </dependency>
    </dependencies>
    
    <build>
        <plugins>
            <plugin>
                <groupId>com.lazerycode.jmeter</groupId>
                <artifactId>jmeter-maven-plugin</artifactId>
                <version>3.2.0</version>
                <configuration>
                    <testFiles>
                        <testFile>api-load-test.jmx</testFile>
                    </testFiles>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
```

### api-load-test.jmx

```xml
<?xml version="1.0" encoding="UTF-8"?>
<jmeterTestPlan version="1.2" properties="5.0" jmeter="5.6.3">
    <hashTree>
        <TestPlan guiclass="TestPlanGui" testclass="TestPlan"
            testname="Production API Load Test" enabled="true">
            <stringProp name="TestPlan.user_define_classpath"></stringProp>
            <boolProp name="TestPlan.serialize_threadgroups">false</boolProp>
            <elementProp name="TestPlan.user_defined_variables"
                elementType="Arguments" guiclass="ArgumentsPanel">
                <collectionProp name="Arguments.arguments">
                    <elementProp name="BASE_URL" elementType="Argument">
                        <stringProp name="Argument.name">BASE_URL</stringProp>
                        <stringProp name="Argument.value">http://localhost:8080</stringProp>
                    </elementProp>
                </collectionProp>
            </elementProp>
        </TestPlan>
        
        <hashTree>
            <!-- Ramp-up Test: 10 to 100 users -->
            <ThreadGroup guiclass="ThreadGroupGui" testclass="ThreadGroup"
                testname="Ramp-Up Load" enabled="true">
                <stringProp name="ThreadGroup.on_sample_error">continue</stringProp>
                <elementProp name="ThreadGroup.main_controller"
                    elementType="LoopController" guiclass="LoopControllerGui">
                    <boolProp name="LoopController.continue_forever">false</boolProp>
                    <stringProp name="LoopController.loops">-1</stringProp>
                </elementProp>
                <stringProp name="ThreadGroup.num_threads">100</stringProp>
                <stringProp name="ThreadGroup.ramp_time">60</stringProp>
                <boolProp name="ThreadGroup.scheduler">true</boolProp>
                <stringProp name="ThreadGroup.duration">300</stringProp>
                <stringProp name="ThreadGroup.delay">0</stringProp>
                <boolProp name="ThreadGroup.delayedStart">false</boolProp>
            </ThreadGroup>
            
            <hashTree>
                <!-- CSV Data Set Config -->
                <CSVDataSet guiclass="TestBeanGUI" testclass="CSVDataSet"
                    testname="CSV Users Data">
                    <stringProp name="delimiter">,</stringProp>
                    <stringProp name="fileEncoding">UTF-8</stringProp>
                    <stringProp name="filename">data/users.csv</stringProp>
                    <boolProp name="ignoreFirstLine">true</boolProp>
                    <boolProp name="quotedData">false</boolProp>
                    <boolProp name="recycle">true</boolProp>
                    <stringProp name="shareMode">shareMode.all</stringProp>
                    <boolProp name="stopThread">false</boolProp>
                    <stringProp name="variableNames">userId,productId,qty</stringProp>
                </CSVDataSet>
                
                <hashTree/>
                
                <!-- GET Products -->
                <HTTPSamplerProxy guiclass="HttpTestSampleGui"
                    testclass="HTTPSamplerProxy" testname="GET Products">
                    <stringProp name="HTTPSampler.domain">${BASE_URL}</stringProp>
                    <stringProp name="HTTPSampler.port"></stringProp>
                    <stringProp name="HTTPSampler.protocol">http</stringProp>
                    <stringProp name="HTTPSampler.path">/api/products</stringProp>
                    <stringProp name="HTTPSampler.method">GET</stringProp>
                    <stringProp name="ConnectTimeout">5000</stringProp>
                    <stringProp name="ResponseTimeout">30000</stringProp>
                </HTTPSamplerProxy>
                
                <hashTree>
                    <ResponseAssertion guiclass="ResponseAssertionGui"
                        testclass="ResponseAssertion" testname="Valid Response">
                        <collectionProp name="Asserion.teststrings">
                            <stringProp name="Assertion.response_code">200|201|204</stringProp>
                        </collectionProp>
                        <stringProp name="Assertion.testfield">response_code</stringProp>
                        <boolProp name="Assertion.assume_success">false</boolProp>
                    </ResponseAssertion>
                </hashTree>
                
                <hashTree>
                    <DurationAssertion guiclass="DurationAssertionGui"
                        testclass="DurationAssertion" testname="< 2s">
                        <stringProp name="DurationAssertion.duration">2000</stringProp>
                    </DurationAssertion>
                </hashTree>
                
                <hashTree/>
                
                <!-- GET Product Detail -->
                <HTTPSamplerProxy guiclass="HttpTestSampleGui"
                    testclass="HTTPSamplerProxy" testname="GET Product">
                    <stringProp name="HTTPSampler.domain">${BASE_URL}</stringProp>
                    <stringProp name="HTTPSampler.path">/api/products/${productId}</stringProp>
                    <stringProp name="HTTPSampler.method">GET</stringProp>
                </HTTPSamplerProxy>
                
                <hashTree/>
                
                <!-- POST Create Order -->
                <HTTPSamplerProxy guiclass="HttpTestSampleGui"
                    testclass="HTTPSamplerProxy" testname="POST Order">
                    <stringProp name="HTTPSampler.domain">${BASE_URL}</stringProp>
                    <stringProp name="HTTPSampler.path">/api/orders</stringProp>
                    <stringProp name="HTTPSampler.method">POST</stringProp>
                    <boolProp name="HTTPSampler.postBodyRaw">true</boolProp>
                    <elementProp name="HTTPsampler.Arguments"
                        elementType="Arguments">
                        <collectionProp name="Arguments.arguments">
                            <elementProp name="" elementType="HTTPArgument">
                                <boolProp name="HTTPArgument.always_encode">false</boolProp>
                                <stringProp name="Argument.value">{"productId":"${productId}","quantity":${qty},"userId":"${userId}"}</stringProp>
                                <stringProp name="Argument.metadata">=</stringProp>
                            </elementProp>
                        </collectionProp>
                    </elementProp>
                </HTTPSamplerProxy>
                
                <hashTree>
                    <HeaderManager guiclass="HeaderPanel" testclass="HeaderManager"
                        testname="HTTP Header Manager">
                        <collectionProp name="Header.headers">
                            <elementProp name="" elementType="Header">
                                <stringProp name="Header.name">Content-Type</stringProp>
                                <stringProp name="Header.value">application/json</stringProp>
                            </elementProp>
                        </collectionProp>
                    </HeaderManager>
                </hashTree>
                
                <hashTree/>
                
                <!-- Gaussian Random Timer -->
                <GaussianRandomTimer guiclass="GaussianRandomTimerGui"
                    testclass="GaussianRandomTimer" testname="Think Time">
                    <stringProp name="ConstantTimer.delay">1000</stringProp>
                    <stringProp name="RandomTimer.range">500</stringProp>
                </GaussianRandomTimer>
            </hashTree>
            
            <hashTree>
                <!-- Spike Test Thread Group -->
                <ThreadGroup guiclass="ThreadGroupGui" testclass="ThreadGroup"
                    testname="Spike Load" enabled="true">
                    <stringProp name="ThreadGroup.on_sample_error">continue</stringProp>
                    <elementProp name="ThreadGroup.main_controller"
                        elementType="LoopController">
                        <boolProp name="LoopController.continue_forever">false</boolProp>
                        <stringProp name="LoopController.loops">10</stringProp>
                    </elementProp>
                    <stringProp name="ThreadGroup.num_threads">200</stringProp>
                    <stringProp name="ThreadGroup.ramp_time">10</stringProp>
                </ThreadGroup>
                
                <hashTree>
                    <HTTPSamplerProxy guiclass="HttpTestSampleGui"
                        testclass="HTTPSamplerProxy" testname="GET Products">
                        <stringProp name="HTTPSampler.domain">${BASE_URL}</stringProp>
                        <stringProp name="HTTPSampler.path">/api/products</stringProp>
                    </HTTPSamplerProxy>
                </hashTree>
            </hashTree>
        </hashTree>
        
        <!-- Listeners -->
        <ResultCollector guiclass="SummaryReport" testclass="ResultCollector"
            testname="Summary Report">
            <boolProp name="ResultCollector.success">true</boolProp>
        </ResultCollector>
        
        <hashTree>
            <jp.JP@gc-ResultsVisualizer guiclass="jp@gc-ResultsVisualizer"
                testclass="jp.JMeterchartpluginresultsgui" testname="Charts"/>
        </hashTree>
    </hashTree>
</jmeterTestPlan>
```

### RunTests.sh

```bash
#!/bin/bash

# JMeter Production Test Runner

BASE_URL="${BASE_URL:-http://localhost:8080}"
THREADS="${THREADS:-100}"
DURATION="${DURATION:-300}"
RAMP_UP="${RAMP_UP:-60}"

echo "Running Production Load Test..."
echo "  Base URL: $BASE_URL"
echo "  Threads: $THREADS"
echo "  Duration: ${DURATION}s"
echo "  Ramp-up: ${RAMP_UP}s"

# Clean previous results
rm -rf results/*
mkdir -p results

# Run JMeter tests
jmeter -n \
    -t test-plans/api-load-test.jmx \
    -l results/results.jtl \
    -e \
    -o results/html \
    -JBASE_URL="$BASE_URL" \
    -Jthreads="$THREADS" \
    -Jduration="$DURATION" \
    -Jramp="$RAMP_UP"

# Generate reports
echo "Generating reports..."
jmeter -g results/results.jtl -o results/html

echo "Test completed. Results in results/html"
```

### LoadTestRunner.java

```java
package com.learning.jmeter;

import org.apache.jmeter.engine.StandardJMeterEngine;
import org.apache.jmeter.engine.TreeCloner;
import org.apache.jmeter.protocol.http.control.CacheManager;
import org.apache.jmeter.protocol.http.control.CookieManager;
import org.apache.jmeter.protocol.http.control.HeaderManager;
import org.apache.jmeter.protocol.http.sampler.HTTPSamplerProxy;
import org.apache.jmeter.save.SaveService;
import org.apache.jmeter.testelement.TestPlan;
import org.apache.jmeter.threads.ThreadGroup;

import java.io.File;
import java.util.*;

public class LoadTestRunner {
    
    public static void main(String[] args) {
        System.out.println("=== JMeter Production Load Test ===\n");
        
        // Test Configuration
        Map<String, Object> config = new LinkedHashMap<>();
        config.put("Test Name", "Production API Load Test");
        config.put("Base URL", "http://localhost:8080");
        config.put("Duration", "5 minutes");
        config.put("Threads", 100);
        config.put("Ramp-up", "60 seconds");
        
        System.out.println("1. Test Configuration:");
        for (var entry : config.entrySet()) {
            System.out.println("   " + entry.getKey() + ": " + entry.getValue());
        }
        
        // Thread Groups
        System.out.println("\n2. Thread Groups:");
        System.out.println("   Ramp-Up: 100 threads, 60s ramp-up, 5min duration");
        System.out.println("   Spike: 200 threads, 10s ramp-up, quick burst");
        
        // Endpoints
        System.out.println("\n3. Endpoints Tested:");
        System.out.println("   - GET /api/products (list)");
        System.out.println("   - GET /api/products/{id} (detail)");
        System.out.println("   - POST /api/orders (create)");
        System.out.println("   - PUT /api/orders/{id} (update)");
        System.out.println("   - DELETE /api/orders/{id} (delete)");
        
        // Expected Results
        System.out.println("\n4. Target Metrics:");
        System.out.println("   - Throughput: > 500 req/sec");
        System.out.println("   - Response Time (p95): < 500ms");
        System.out.println("   - Error Rate: < 0.1%");
        System.out.println("   - CPU Usage: < 80%");
        
        // Execute load test simulation
        System.out.println("\n5. Running Load Test...\n");
        simulateLoadTest();
        
        System.out.println("\n=== Test Complete ===");
    }
    
    private static void simulateLoadTest() {
        Random random = new Random();
        String[] endpoints = {
            "GET /api/products",
            "GET /api/products/PROD-001",
            "POST /api/orders",
            "PUT /api/orders/ORD-001",
            "DELETE /api/orders/ORD-002"
        };
        
        int[] threadCounts = {10, 25, 50, 75, 100, 125, 150, 175, 200};
        
        System.out.println("   Thread | Req/sec | Avg ms | p95 ms | p99 ms | Err %");
        System.out.println("   -------|---------|--------|--------|--------|-------");
        
        for (int threads : threadCounts) {
            int throughput = threads * 5;
            int avgResponse = 100 + (threads * 2) + random.nextInt(20);
            int p95Response = (int)(avgResponse * 1.5);
            int p99Response = (int)(avgResponse * 2.0);
            double errorRate = threads > 150 ? 0.1 + (threads - 150) * 0.001 : 0.0;
            
            System.out.printf("   %6d | %7d | %6d | %6d | %6d | %4.2f%%%n",
                threads, throughput, avgResponse, p95Response, p99Response, errorRate);
        }
        
        System.out.println("\n6. Detailed Results:");
        
        // By endpoint
        Map<String, int[]> endpointStats = new LinkedHashMap<>();
        endpointStats.put("GET /api/products", new int[]{800, 80, 120, 180, 250});
        endpointStats.put("GET /api/products/{id}", new int[]{600, 60, 95, 140, 200});
        endpointStats.put("POST /api/orders", new int[]{400, 150, 220, 350, 500});
        endpointStats.put("DELETE /api/orders/{id}", new int[]{200, 100, 180, 280, 400});
        
        System.out.println("   Endpoint              | Req/sec | Avg  | p95  | p99  | Pass");
        System.out.println("   ----------------------|---------|------|------|------|-----");
        
        for (var entry : endpointStats.entrySet()) {
            int[] stats = entry.getValue();
            System.out.printf("   %-22s | %7d | %4dms | %4dms | %4dms | Yes%n",
                entry.getKey(), stats[0], stats[1], stats[2], stats[3], stats[4]);
        }
    }
}
```

### user.properties (JMeter Configuration)

```properties
# JMeter Performance Properties

# HTTP Settings
httpclient4.retrycount=0
httpclient.timeout=30000

# Result File Settings
jmeter.save.saveservice.output_format=csv
jmeter.save.saveservice.response_data=true
jmeter.save.saveservice.samplerData=true
jmeter.save.saveservice.assertions=true
jmeter.save.saveservice.timestamp_format=ms

# Thread Settings
jmeter.threads.startup.delay=5
jmeter.threads.shutdown.wait=30

# Log Settings
log_level.jmeter=INFO
log_level.jmeter.junit=DEBUG
```

### Build and Run Instructions

```bash
cd jmeter-production
mvn clean verify

# Run specific test plan
mvn jmeter:jmeter -DtestFile=test-plans/api-load-test.jmx

# Run with custom configuration
mvn jmeter:jmeter -Dthreads=50 -Dduration=180
```

### Viewing Results

```bash
# Open HTML report
firefox results/html/index.html

# View CSV results in table
jmeter -g results/results.jtl -o results

# Parse with Excel
open results/results.csv
```

## Summary

These two projects demonstrate:

1. **Mini-Project**: Basic JMeter setup with thread groups, HTTP samplers, assertions, and reporting
2. **Production Project**: Complete production load testing with CSV data, multiple thread groups, timers, and comprehensive reporting

JMeter enables thorough performance testing of APIs and microservices to identify bottlenecks before production deployment.