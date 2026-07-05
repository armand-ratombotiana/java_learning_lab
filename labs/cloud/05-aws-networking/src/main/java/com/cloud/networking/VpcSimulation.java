package com.cloud.networking;

import java.util.*;
import java.util.concurrent.*;

public class VpcSimulation {

    public static class Subnet {
        public final String id;
        public final String cidrBlock;
        public final String availabilityZone;
        public final Map<String, String> routeTable = new HashMap<>();

        public Subnet(String id, String cidr, String az) {
            this.id = id;
            this.cidrBlock = cidr;
            this.availabilityZone = az;
            routeTable.put("0.0.0.0/0", "igw-12345");
        }

        public boolean isIpInRange(String ip) {
            return cidrBlock.startsWith(ip.substring(0, ip.lastIndexOf('.')));
        }

        @Override
        public String toString() { return id + " [" + cidrBlock + "] " + availabilityZone; }
    }

    public static class VPC {
        public final String vpcId;
        public final String cidrBlock;
        public final List<Subnet> subnets = new ArrayList<>();
        public final List<String> securityGroups = new ArrayList<>();

        public VPC(String vpcId, String cidrBlock) {
            this.vpcId = vpcId;
            this.cidrBlock = cidrBlock;
            System.out.println("Created VPC: " + vpcId + " (" + cidrBlock + ")");
        }

        public Subnet addSubnet(String cidr, String az) {
            String id = "subnet-" + UUID.randomUUID().toString().substring(0, 8);
            Subnet subnet = new Subnet(id, cidr, az);
            subnets.add(subnet);
            System.out.println("Added subnet: " + subnet);
            return subnet;
        }

        public void addSecurityGroup(String name) {
            securityGroups.add(name);
            System.out.println("Added security group: " + name);
        }
    }

    public static class VPCPeering {
        private final VPC vpcA;
        private final VPC vpcB;
        private final String peeringId;
        private boolean active;

        public VPCPeering(VPC vpcA, VPC vpcB) {
            this.vpcA = vpcA;
            this.vpcB = vpcB;
            this.peeringId = "pcx-" + UUID.randomUUID().toString().substring(0, 8);
        }

        public void accept() {
            active = true;
            System.out.println("VPC peering " + peeringId + ": " + vpcA.vpcId + " <-> " + vpcB.vpcId + " ACTIVE");
        }
    }

    public static void main(String[] args) {
        VPC vpc = new VPC("vpc-12345", "10.0.0.0/16");
        vpc.addSubnet("10.0.1.0/24", "us-east-1a");
        vpc.addSubnet("10.0.2.0/24", "us-east-1b");
        vpc.addSubnet("10.0.3.0/24", "us-east-1c");
        vpc.addSecurityGroup("web-sg");
        vpc.addSecurityGroup("db-sg");

        VPC vpc2 = new VPC("vpc-67890", "172.16.0.0/16");
        VPCPeering peering = new VPCPeering(vpc, vpc2);
        peering.accept();

        System.out.println("\n=== VPC ===");
        System.out.println("VPC " + vpc.vpcId + " has " + vpc.subnets.size() + " subnets, "
            + vpc.securityGroups.size() + " security groups");
    }
}
