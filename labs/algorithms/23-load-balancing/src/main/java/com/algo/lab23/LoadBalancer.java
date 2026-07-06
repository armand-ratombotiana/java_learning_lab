package com.algo.lab23;

import java.util.List;

/**
 * Interface for load balancing strategies.
 * A load balancer distributes requests across a set of backend servers.
 */
@FunctionalInterface
public interface LoadBalancer {

    String selectServer(List<String> servers, String request);
}
