package com.cloud.gcpfund;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class GCPServicesSimulatorTest {
    @Test void testComputeEngineLifecycle() {
        var vm = new GCPServicesSimulator.ComputeEngineInstance("test", "e2-standard-2", "us-central1-a");
        assertEquals(GCPServicesSimulator.ComputeEngineInstance.Status.PROVISIONING, vm.getStatus());
        vm.start();
        assertEquals(GCPServicesSimulator.ComputeEngineInstance.Status.RUNNING, vm.getStatus());
        vm.stop();
        assertEquals(GCPServicesSimulator.ComputeEngineInstance.Status.STOPPED, vm.getStatus());
        vm.terminate();
        assertEquals(GCPServicesSimulator.ComputeEngineInstance.Status.TERMINATED, vm.getStatus());
    }

    @Test void testCloudStorage() {
        var bucket = new GCPServicesSimulator.CloudStorageBucket("test-bucket", "US-CENTRAL1");
        bucket.uploadObject("data.json", "{\"key\":\"value\"}".getBytes(), Map.of("content-type", "application/json"));
        assertArrayEquals("{\"key\":\"value\"}".getBytes(), bucket.downloadObject("data.json"));
        assertEquals(1, bucket.listObjects().size());
    }

    @Test void testGkeCluster() {
        var cluster = new GCPServicesSimulator.GkeCluster("test-cluster", "us-central1", 3);
        cluster.activate();
        assertEquals(GCPServicesSimulator.GkeCluster.Status.RUNNING, cluster.getStatus());
    }
}
