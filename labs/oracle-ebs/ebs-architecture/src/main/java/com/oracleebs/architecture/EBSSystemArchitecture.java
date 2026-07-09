package com.oracleebs.architecture;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class EBSSystemArchitecture {
    public enum Tier { DESKTOP, APPLICATION, DATABASE }
    public enum Component { FORMS_SERVER, OHS, CONCURRENT_MANAGER, DATABASE_INSTANCE, JINITIATOR }

    private final Map<Tier, List<Component>> topology;
    private final Map<String, String> environment;
    private final Set<String> activeNodes;
    private final Map<String, Integer> portRegistry;

    public EBSSystemArchitecture() {
        this.topology = new EnumMap<>(Tier.class);
        this.environment = new ConcurrentHashMap<>();
        this.activeNodes = ConcurrentHashMap.newKeySet();
        this.portRegistry = new ConcurrentHashMap<>();
    }

    public EBSSystemArchitecture addComponent(Tier tier, Component component) {
        topology.computeIfAbsent(tier, k -> new ArrayList<>()).add(component);
        return this;
    }

    public EBSSystemArchitecture setEnvironment(String key, String value) {
        environment.put(key, value);
        return this;
    }

    public EBSSystemArchitecture registerPort(String service, int port) {
        portRegistry.put(service, port);
        return this;
    }

    public EBSSystemArchitecture activateNode(String node) {
        activeNodes.add(node);
        return this;
    }

    public List<Component> getComponents(Tier tier) {
        return topology.getOrDefault(tier, List.of());
    }

    public String getEnv(String key) {
        return environment.getOrDefault(key, "NOT_SET");
    }

    public boolean isMultiNode() {
        return activeNodes.size() > 1;
    }

    public int getPort(String service) {
        return portRegistry.getOrDefault(service, -1);
    }

    public Map<String, String> getEnvironment() {
        return Collections.unmodifiableMap(environment);
    }

    public Set<String> getActiveNodes() {
        return Collections.unmodifiableSet(activeNodes);
    }

    public boolean hasComponent(Tier tier, Component component) {
        return topology.containsKey(tier) && topology.get(tier).contains(component);
    }

    public int totalComponents() {
        return topology.values().stream().mapToInt(List::size).sum();
    }

    public static EBSSystemArchitecture createDefault() {
        return new EBSSystemArchitecture()
            .addComponent(Tier.DESKTOP, Component.JINITIATOR)
            .addComponent(Tier.APPLICATION, Component.FORMS_SERVER)
            .addComponent(Tier.APPLICATION, Component.OHS)
            .addComponent(Tier.APPLICATION, Component.CONCURRENT_MANAGER)
            .addComponent(Tier.DATABASE, Component.DATABASE_INSTANCE)
            .setEnvironment("CONTEXT_FILE", "/u01/inst/apps/ERSDEV/apps/apps_st/appl/admin/CONTEXT_file.xml")
            .setEnvironment("TWO_TASK", "ERSDEV")
            .setEnvironment("APPL_TOP", "/u01/inst/apps/ERSDEV/apps/apps_st/appl")
            .setEnvironment("COMMON_TOP", "/u01/inst/apps/ERSDEV/apps/apps_st/comn")
            .setEnvironment("INST_TOP", "/u01/inst/apps/ERSDEV/apps/apps_st/inst")
            .registerPort("FORMS", 9001)
            .registerPort("OHS", 8000)
            .registerPort("DB", 1521)
            .activateNode("node1.example.com")
            .activateNode("node2.example.com");
    }
}
