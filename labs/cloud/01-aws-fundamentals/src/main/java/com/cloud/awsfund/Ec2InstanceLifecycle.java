package com.cloud.awsfund;

import java.util.*;
import java.util.concurrent.*;

public class Ec2InstanceLifecycle {

    public enum InstanceState { PENDING, RUNNING, STOPPING, STOPPED, TERMINATED }

    public static class EC2Instance {
        public final String instanceId;
        public final String instanceType;
        public final String ami;
        public InstanceState state;
        public final String publicIp;
        public final long launchTime;
        private long stopTime;

        public EC2Instance(String instanceId, String instanceType, String ami) {
            this.instanceId = instanceId;
            this.instanceType = instanceType;
            this.ami = ami;
            this.state = InstanceState.PENDING;
            this.publicIp = generateIp();
            this.launchTime = System.currentTimeMillis();
        }

        private String generateIp() {
            Random r = new Random();
            return "52." + r.nextInt(256) + "." + r.nextInt(256) + "." + r.nextInt(256);
        }

        public void start() {
            if (state == InstanceState.STOPPED || state == InstanceState.PENDING) {
                state = InstanceState.RUNNING;
                System.out.println(instanceId + " -> RUNNING");
            }
        }

        public void stop() {
            if (state == InstanceState.RUNNING) {
                state = InstanceState.STOPPING;
                System.out.println(instanceId + " -> STOPPING");
                try { Thread.sleep(100); } catch (InterruptedException e) {}
                state = InstanceState.STOPPED;
                stopTime = System.currentTimeMillis();
                System.out.println(instanceId + " -> STOPPED");
            }
        }

        public void terminate() {
            state = InstanceState.TERMINATED;
            System.out.println(instanceId + " -> TERMINATED");
        }

        @Override
        public String toString() {
            return instanceId + " [" + instanceType + "] " + state + " @" + publicIp;
        }
    }

    public static class EC2Service {
        private final Map<String, EC2Instance> instances = new ConcurrentHashMap<>();
        private final AtomicLong counter = new AtomicLong(1);

        public EC2Instance runInstance(String instanceType, String ami) {
            String id = "i-" + String.format("%08x", counter.getAndIncrement());
            EC2Instance inst = new EC2Instance(id, instanceType, ami);
            instances.put(id, inst);
            new Thread(() -> {
                try { Thread.sleep(200); } catch (InterruptedException e) {}
                inst.start();
            }).start();
            System.out.println("Launching " + id + " (" + instanceType + ")");
            return inst;
        }

        public EC2Instance getInstance(String id) { return instances.get(id); }

        public List<EC2Instance> listInstances() {
            return new ArrayList<>(instances.values());
        }
    }

    public static void main(String[] args) throws Exception {
        EC2Service ec2 = new EC2Service();

        EC2Instance web = ec2.runInstance("t3.medium", "ami-0c55b159cbfafe1f0");
        EC2Instance db = ec2.runInstance("t3.large", "ami-0c55b159cbfafe1f0");

        Thread.sleep(500);

        System.out.println("\n=== EC2 Instances ===");
        ec2.listInstances().forEach(System.out::println);

        web.stop();
        web.start();
        db.terminate();

        System.out.println("\nFinal state:");
        ec2.listInstances().forEach(System.out::println);
    }
}
