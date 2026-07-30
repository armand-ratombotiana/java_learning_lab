package com.cloud.deep.lab05;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.*;

public class IaCDeepDive {

    public record ResourceState(String id, String type, String name, Map<String,Object> attributes, String module) {}
    public record Module(String name, String source, String version, Map<String,Object> inputs, Map<String,Object> outputs) {}
    public record BackendConfig(String type, String bucket, String key, String region) {}

    public record TerraformState(String version, String backend, String workspace, List<ResourceState> resources) {
        public TerraformState { resources = List.copyOf(resources); }
    }

    public interface Backend {
        void write(TerraformState state);
        TerraformState read();
        boolean lock(String id);
        void unlock(String id);
        boolean isLocked();
    }

    public static class S3Backend implements Backend {
        private TerraformState state;
        private volatile boolean locked = false;
        private final String bucket;

        public S3Backend(String bucket) { this.bucket = bucket; }

        public synchronized void write(TerraformState state) { this.state = state; }
        public synchronized TerraformState read() { return state; }
        public synchronized boolean lock(String id) { if (locked) return false; locked = true; return true; }
        public synchronized void unlock(String id) { locked = false; }
        public boolean isLocked() { return locked; }
        public String getBucket() { return bucket; }
    }

    public static class ModuleRegistry {
        private final Map<String, Module> modules = new ConcurrentHashMap<>();

        public void register(Module module) { modules.put(module.name() + "@" + module.version(), module); }

        public Optional<Module> resolve(String name, String version) {
            return Optional.ofNullable(modules.get(name + "@" + version));
        }

        public Module compose(String name, List<String> moduleNames) {
            Map<String,Object> allInputs = new HashMap<>();
            Map<String,Object> allOutputs = new HashMap<>();
            for (String mn : moduleNames) {
                var found = modules.values().stream().filter(m -> m.name().equals(mn)).findFirst();
                if (found.isPresent()) {
                    allInputs.putAll(found.get().inputs());
                    allOutputs.putAll(found.get().outputs());
                }
            }
            return new Module(name, "composed", "1.0", allInputs, allOutputs);
        }
    }

    public static class WorkspaceManager {
        private final Map<String, TerraformState> workspaces = new ConcurrentHashMap<>();
        private volatile String currentWorkspace = "default";
        private final Backend backend;

        public WorkspaceManager(Backend backend) { this.backend = backend; }

        public void select(String workspace) { currentWorkspace = workspace; }

        public void apply(Module module) {
            var state = workspaces.getOrDefault(currentWorkspace,
                new TerraformState("1.0", "s3", currentWorkspace, List.of()));
            var resources = new ArrayList<>(state.resources());
            resources.add(new ResourceState(UUID.randomUUID().toString(), module.name(), module.name() + "-resource",
                module.outputs(), module.name()));
            var newState = new TerraformState(state.version(), state.backend(), currentWorkspace, resources);
            workspaces.put(currentWorkspace, newState);
            backend.write(newState);
        }

        public TerraformState getState() { return workspaces.get(currentWorkspace); }
    }

    public static class CfTemplateGenerator {
        private final List<Map<String,Object>> resources = new ArrayList<>();

        public void addResource(String logicalId, String type, Map<String,Object> properties) {
            var resource = new HashMap<String,Object>();
            resource.put("LogicalId", logicalId);
            resource.put("Type", type);
            resource.put("Properties", properties);
            resources.add(resource);
        }

        public String generate() {
            var sb = new StringBuilder();
            sb.append("AWSTemplateFormatVersion: '2010-09-09'\n");
            sb.append("Resources:\n");
            for (var r : resources) {
                sb.append("  ").append(r.get("LogicalId")).append(":\n");
                sb.append("    Type: ").append(r.get("Type")).append("\n");
                sb.append("    Properties:\n");
                @SuppressWarnings("unchecked")
                var props = (Map<String,Object>) r.get("Properties");
                for (var e : props.entrySet()) {
                    sb.append("      ").append(e.getKey()).append(": ").append(e.getValue()).append("\n");
                }
            }
            return sb.toString();
        }
    }

    public static void main(String[] args) {
        System.out.println("=== Remote Backend ===");
        var backend = new S3Backend("my-infra-state");
        System.out.println("Backend bucket: " + backend.getBucket());
        System.out.println("Lock acquired: " + backend.lock("op-1"));

        System.out.println("\n=== Workspace Manager ===");
        var wm = new WorkspaceManager(backend);
        var vpcModule = new Module("vpc", "terraform-aws-modules/vpc/aws", "5.0", Map.of("cidr", "10.0.0.0/16"), Map.of("vpc_id", "vpc-12345"));
        var registry = new ModuleRegistry();
        registry.register(vpcModule);
        wm.select("prod");
        wm.apply(vpcModule);
        System.out.println("Prod state resources: " + wm.getState().resources().size());

        wm.select("staging");
        wm.apply(vpcModule);
        System.out.println("Staging state resources: " + wm.getState().resources().size());

        System.out.println("\n=== CloudFormation Template ===");
        var cf = new CfTemplateGenerator();
        cf.addResource("WebServer", "AWS::EC2::Instance", Map.of("InstanceType", "t3.medium", "ImageId", "ami-12345"));
        cf.addResource("WebSecurityGroup", "AWS::EC2::SecurityGroup", Map.of("GroupDescription", "Web SG"));
        System.out.println(cf.generate());
    }
}
