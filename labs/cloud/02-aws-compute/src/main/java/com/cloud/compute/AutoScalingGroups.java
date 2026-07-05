package com.cloud.compute;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

public class AutoScalingGroups {

    public static class LaunchTemplate {
        public final String name;
        public final String ami;
        public final String instanceType;

        public LaunchTemplate(String name, String ami, String instanceType) {
            this.name = name;
            this.ami = ami;
            this.instanceType = instanceType;
        }
    }

    public static class AutoScalingInstance {
        public final String id;
        public final String launchTemplate;
        public volatile boolean healthy;
        public final long launchTime;

        public AutoScalingInstance(String id, String launchTemplate) {
            this.id = id;
            this.launchTemplate = launchTemplate;
            this.healthy = true;
            this.launchTime = System.currentTimeMillis();
        }
    }

    public static class AutoScalingGroup {
        private final String name;
        private final LaunchTemplate template;
        private final List<AutoScalingInstance> instances = new CopyOnWriteArrayList<>();
        private final int minSize;
        private final int maxSize;
        private final int desiredCapacity;
        private final AtomicLong counter = new AtomicLong(1);

        public AutoScalingGroup(String name, LaunchTemplate template, int min, int max, int desired) {
            this.name = name;
            this.template = template;
            this.minSize = min;
            this.maxSize = max;
            this.desiredCapacity = desired;
        }

        public void initialize() {
            for (int i = 0; i < desiredCapacity; i++) {
                addInstance();
            }
            System.out.println("\nASG '" + name + "' initialized with " + instances.size() + " instances");
        }

        public String addInstance() {
            String id = "i-asg-" + counter.getAndIncrement();
            AutoScalingInstance inst = new AutoScalingInstance(id, template.name);
            instances.add(inst);
            System.out.println("  Added instance " + id);
            return id;
        }

        public void scaleUp(int count) {
            int canAdd = Math.min(count, maxSize - instances.size());
            System.out.println("\nScaling UP by " + canAdd + " instances");
            for (int i = 0; i < canAdd; i++) {
                addInstance();
            }
        }

        public void scaleDown(int count) {
            int canRemove = Math.min(count, instances.size() - minSize);
            System.out.println("\nScaling DOWN by " + canRemove + " instances");
            for (int i = 0; i < canRemove; i++) {
                AutoScalingInstance removed = instances.remove(instances.size() - 1);
                System.out.println("  Removed instance " + removed.id);
            }
        }

        public void healthCheck() {
            System.out.println("\nHealth check (" + instances.size() + " instances):");
            for (AutoScalingInstance inst : instances) {
                inst.healthy = Math.random() > 0.1;
                System.out.println("  " + inst.id + " -> " + (inst.healthy ? "HEALTHY" : "UNHEALTHY"));
            }
            instances.removeIf(i -> !i.healthy);
            while (instances.size() < desiredCapacity) {
                addInstance();
            }
        }

        public int getSize() { return instances.size(); }
    }

    public static void main(String[] args) throws Exception {
        LaunchTemplate template = new LaunchTemplate("web-server-v1", "ami-0abcdef", "t3.micro");
        AutoScalingGroup asg = new AutoScalingGroup("web-asg", template, 2, 10, 3);

        asg.initialize();
        asg.scaleUp(4);
        asg.scaleDown(2);

        System.out.println("\n=== Auto Scaling Group ===");
        asg.healthCheck();

        System.out.println("\nFinal size: " + asg.getSize());
    }
}
