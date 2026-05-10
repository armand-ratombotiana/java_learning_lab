package com.learning.servicemesh;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ServiceMeshSolutionTest {

    private ServiceMeshSolution solution;

    @BeforeEach
    void setUp() {
        solution = new ServiceMeshSolution();
    }

    @Test
    void testCreateVirtualService() {
        VirtualService vs = solution.createVirtualService("my-vs", "my-service", "my-svc", "v1");
        assertEquals("my-vs", vs.getMetadata().getName());
        assertEquals("my-service", vs.getSpec().getHosts().get(0));
    }

    @Test
    void testCreateDestinationRule() {
        DestinationRule dr = solution.createDestinationRule("my-dr", "my-svc", "v1", "v1");
        assertEquals("my-dr", dr.getMetadata().getName());
        assertEquals("my-svc", dr.getSpec().getHost());
    }

    @Test
    void testCreateGateway() {
        Gateway gw = solution.createGateway("my-gw", "ingressgateway", 80);
        assertEquals("my-gw", gw.getMetadata().getName());
        assertEquals(80, gw.getSpec().getServers().get(0).getPort().getNumber());
    }

    @Test
    void testCreateSidecar() {
        Sidecar sc = solution.createSidecar("my-sidecar", "default");
        assertEquals("my-sidecar", sc.getMetadata().getName());
        assertEquals("default", sc.getMetadata().getNamespace());
    }

    @Test
    void testCreateAuthorizationPolicy() {
        AuthorizationPolicy ap = solution.createAuthorizationPolicy("my-authz", "default", "ALLOW");
        assertEquals("my-authz", ap.getMetadata().getName());
        assertEquals("ALLOW", ap.getSpec().getAction());
    }

    @Test
    void testCreatePeerAuthentication() {
        PeerAuthentication pa = solution.createPeerAuthentication("my-pa", "default", "STRICT");
        assertEquals("my-pa", pa.getMetadata().getName());
        assertEquals("STRICT", pa.getSpec().getMtls().getMode());
    }
}