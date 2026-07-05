package com.cloud.terraform;

import java.util.*;
import java.util.concurrent.*;
import java.nio.file.*;

public class StateManagement {

    public static class TerraformState {
        private final Map<String, ResourceState> resources = new ConcurrentHashMap<>();
        private long serial = 1;
        private String lockId;

        public static class ResourceState {
            public final String address;
            public final String type;
            public final String name;
            public final String id;
            public final Map<String, Object> attributes;

            public ResourceState(String address, String type, String name,
                                 String id, Map<String, Object> attrs) {
                this.address = address;
                this.type = type;
                this.name = name;
                this.id = id;
                this.attributes = attrs;
            }

            @Override
            public String toString() {
                return address + " = " + id;
            }
        }

        public synchronized boolean lock(String lockId) {
            if (this.lockId != null) return false;
            this.lockId = lockId;
            System.out.println("State locked by " + lockId);
            return true;
        }

        public synchronized boolean unlock(String lockId) {
            if (lockId.equals(this.lockId)) {
                this.lockId = null;
                System.out.println("State unlocked by " + lockId);
                return true;
            }
            return false;
        }

        public synchronized void addResource(ResourceState rs) {
            serial++;
            resources.put(rs.address, rs);
            System.out.println("State: added " + rs.address + " (serial " + serial + ")");
        }

        public synchronized void removeResource(String address) {
            serial++;
            resources.remove(address);
        }

        public ResourceState getResource(String address) {
            return resources.get(address);
        }

        public synchronized void refresh() {
            System.out.println("\nRefreshing state (" + resources.size() + " resources):");
            resources.forEach((addr, rs) -> System.out.println("  " + rs));
        }

        public synchronized String getStateJson() {
            StringBuilder sb = new StringBuilder();
            sb.append("{\n  \"version\": 1,\n  \"serial\": ").append(serial);
            sb.append(",\n  \"resources\": [\n");
            boolean first = true;
            for (ResourceState rs : resources.values()) {
                if (!first) sb.append(",\n");
                sb.append("    {\"address\":\"").append(rs.address)
                  .append("\",\"type\":\"").append(rs.type)
                  .append("\",\"id\":\"").append(rs.id).append("\"}");
                first = false;
            }
            sb.append("\n  ]\n}");
            return sb.toString();
        }
    }

    public static class StateMigrator {
        public void migrate(TerraformState local, TerraformState remote) {
            System.out.println("Migrating state from local to remote backend...");
            remote.lock("migration");
            local.refresh();
            System.out.println("State migration complete");
            remote.unlock("migration");
        }
    }

    public static void main(String[] args) {
        TerraformState state = new TerraformState();
        state.lock("deploy-user");

        state.addResource(new TerraformState.ResourceState(
            "aws_vpc.main", "aws_vpc", "main", "vpc-0a1b2c3d4e5f",
            Map.of("cidr_block", "10.0.0.0/16")));

        state.addResource(new TerraformState.ResourceState(
            "aws_instance.web", "aws_instance", "web", "i-0f1e2d3c4b5a",
            Map.of("instance_type", "t3.micro")));

        state.unlock("deploy-user");

        System.out.println("\n=== State Management ===");
        state.refresh();
        System.out.println("\nState JSON:\n" + state.getStateJson());
    }
}
