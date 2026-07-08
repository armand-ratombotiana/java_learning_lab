package com.cloud.mcloud;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class MultiCloudManagerTest {
    @Test void testAwsProvider() {
        var aws = new MultiCloudManager.AwsProvider();
        var inst = aws.createInstance("web", "t3.medium");
        assertEquals("AWS", inst.provider());
        assertNotNull(inst.publicIp());
        assertEquals(1, aws.listInstances().size());
    }

    @Test void testGcpProvider() {
        var gcp = new MultiCloudManager.GcpProvider();
        var inst = gcp.createInstance("web", "e2-standard-2");
        assertEquals("GCP", inst.provider());
        assertNotNull(inst.publicIp());
    }

    @Test void testAzureProvider() {
        var azure = new MultiCloudManager.AzureProvider();
        var inst = azure.createInstance("web", "Standard_D2s_v3");
        assertEquals("Azure", inst.provider());
    }

    @Test void testCloudflareDns() {
        var cf = new MultiCloudManager.CloudflareService();
        cf.addDnsRecord("test.example.com", "1.2.3.4", "A");
        assertEquals("1.2.3.4", cf.resolve("test.example.com"));
        assertEquals("NXDOMAIN", cf.resolve("unknown.example.com"));
    }
}
