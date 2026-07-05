package com.cloud.terraform;

import java.util.*;
import java.util.concurrent.*;

public class IaCSimulation {

    public static class Resource {
        public final String type;
        public final String name;
        public final Map<String, Object> attributes = new LinkedHashMap<>();
        public String id;

        public Resource(String type, String name) {
            this.type = type;
            this.name = name;
        }

        public Resource attr(String key, Object value) {
            attributes.put(key, value);
            return this;
        }

        public void create() {
            this.id = type + "." + name + "-" + UUID.randomUUID().toString().substring(0, 8);
            System.out.println("  + Created " + type + "." + name + " (" + id + ")");
        }

        public void destroy() {
            System.out.println("  - Destroyed " + type + "." + name + " (" + id + ")");
            this.id = null;
        }

        @Override
        public String toString() {
            return type + "." + name + " " + attributes;
        }
    }

    public static class TerraformConfig {
        public final String name;
        public final List<Resource> resources = new ArrayList<>();

        public TerraformConfig(String name) { this.name = name; }

        public Resource addResource(String type, String name) {
            Resource r = new Resource(type, name);
            resources.add(r);
            return r;
        }

        public void plan() {
            System.out.println("\nTerraform Plan for '" + name + "':");
            for (Resource r : resources) {
                if (r.id == null) {
                    System.out.println("  + " + r.type + "." + r.name);
                }
            }
        }

        public void apply() {
            System.out.println("\nTerraform Apply for '" + name + "':");
            for (Resource r : resources) {
                if (r.id == null) {
                    r.create();
                }
            }
        }

        public void destroy() {
            System.out.println("\nTerraform Destroy for '" + name + "':");
            for (int i = resources.size() - 1; i >= 0; i--) {
                if (resources.get(i).id != null) {
                    resources.get(i).destroy();
                }
            }
        }
    }

    public static class TerraformExecutor {
        public void init() { System.out.println("Initializing Terraform..."); }
        public void plan(TerraformConfig config) { config.plan(); }
        public void apply(TerraformConfig config) { config.apply(); }
        public void destroy(TerraformConfig config) { config.destroy(); }
    }

    public static void main(String[] args) {
        TerraformConfig config = new TerraformConfig("production");

        config.addResource("aws_vpc", "main")
            .attr("cidr_block", "10.0.0.0/16")
            .attr("enable_dns_support", true);

        config.addResource("aws_subnet", "public")
            .attr("vpc_id", "${aws_vpc.main.id}")
            .attr("cidr_block", "10.0.1.0/24")
            .attr("map_public_ip_on_launch", true);

        config.addResource("aws_instance", "web")
            .attr("ami", "ami-0c55b159cbfafe1f0")
            .attr("instance_type", "t3.micro")
            .attr("subnet_id", "${aws_subnet.public.id}");

        TerraformExecutor tf = new TerraformExecutor();
        tf.init();

        System.out.println("=== Infrastructure as Code ===");
        tf.plan(config);
        tf.apply(config);
        System.out.println("\nResources created: " + config.resources.size());
        config.resources.forEach(r -> System.out.println("  " + r.type + "." + r.name + " = " + r.id));
        tf.destroy(config);
    }
}
