package com.devops.servicemesh;

import java.util.Map;

public class IstioVirtualServiceSimulation {
    private final Map<String, Integer> routeWeights;

    public IstioVirtualServiceSimulation(Map<String, Integer> routeWeights) {
        this.routeWeights = routeWeights;
    }

    public String route(String request) {
        int totalWeight = routeWeights.values().stream().mapToInt(Integer::intValue).sum();
        int random = (int) (Math.random() * totalWeight);
        int cumulative = 0;

        for (Map.Entry<String, Integer> entry : routeWeights.entrySet()) {
            cumulative += entry.getValue();
            if (random < cumulative) {
                System.out.println("Routing '" + request + "' to " + entry.getKey());
                return entry.getKey();
            }
        }
        return "unknown";
    }

    public static void main(String[] args) {
        IstioVirtualServiceSimulation vs = new IstioVirtualServiceSimulation(Map.of(
            "v1.myapp.svc.cluster.local", 90,
            "v2.myapp.svc.cluster.local", 10
        ));

        for (int i = 0; i < 10; i++) {
            vs.route("request-" + (i + 1));
        }
    }
}
