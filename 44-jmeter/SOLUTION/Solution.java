package com.learning.jmeter;

import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.config.CSVDataSet;
import org.apache.jmeter.config.ConfigTestElement;
import org.apache.jmeter.protocol.http.control.CookieManager;
import org.apache.jmeter.protocol.http.control.HeaderManager;
import org.apache.jmeter.protocol.http.sampler.HTTPSamplerProxy;
import org.apache.jmeter.testelement.TestPlan;
import org.apache.jmeter.threads.ThreadGroup;

import java.util.List;
import java.util.Map;

public class JMeterSolution {

    public HTTPSamplerProxy createHttpSampler(String name, String domain, String path, String method) {
        HTTPSamplerProxy sampler = new HTTPSamplerProxy();
        sampler.setName(name);
        sampler.setDomain(domain);
        sampler.setPath(path);
        sampler.setMethod(method);
        sampler.setPort(80);
        sampler.setProtocol("http");
        return sampler;
    }

    public ThreadGroup createThreadGroup(String name, int numThreads, int rampUp, int loops) {
        ThreadGroup threadGroup = new ThreadGroup();
        threadGroup.setName(name);
        threadGroup.setNumThreads(numThreads);
        threadGroup.setRampUp(rampUp);
        threadGroup.setLoopController(
            new org.apache.jmeter.control.LoopController()
        );
        return threadGroup;
    }

    public TestPlan createTestPlan(String name, boolean enabled) {
        TestPlan testPlan = new TestPlan(name);
        testPlan.setEnabled(enabled);
        testPlan.setUserDefinedVariables(new Arguments());
        return testPlan;
    }

    public CSVDataSet createCsvDataSet(String filename, String variableNames, String delimiter) {
        CSVDataSet csvDataSet = new CSVDataSet();
        csvDataSet.setProperty("filename", filename);
        csvDataSet.setProperty("variableNames", variableNames);
        csvDataSet.setProperty("delimiter", delimiter);
        csvDataSet.setProperty("shareMode", "shareMode.all");
        return csvDataSet;
    }

    public HeaderManager createHeaderManager(Map<String, String> headers) {
        HeaderManager headerManager = new HeaderManager();
        headers.forEach((key, value) -> {
            headerManager.add(new org.apache.jmeter.protocol.http.control.Header(key, value));
        });
        return headerManager;
    }

    public CookieManager createCookieManager() {
        CookieManager cookieManager = new CookieManager();
        cookieManager.setPolicy(org.apache.jmeter.protocol.http.control.CookiePolicy.STANDARD);
        return cookieManager;
    }

    public Arguments createUserDefinedVariables(Map<String, String> variables) {
        Arguments arguments = new Arguments();
        variables.forEach((name, value) ->
            arguments.addArgument(name, value)
        );
        return arguments;
    }
}