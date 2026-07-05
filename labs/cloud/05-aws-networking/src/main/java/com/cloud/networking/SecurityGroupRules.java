package com.cloud.networking;

import java.util.*;

public class SecurityGroupRules {

    public enum Protocol { TCP, UDP, ICMP, ALL }

    public static class Rule {
        public final Protocol protocol;
        public final int fromPort;
        public final int toPort;
        public final String sourceCidr;
        public final boolean inbound;

        public Rule(Protocol protocol, int fromPort, int toPort, String sourceCidr, boolean inbound) {
            this.protocol = protocol;
            this.fromPort = fromPort;
            this.toPort = toPort;
            this.sourceCidr = sourceCidr;
            this.inbound = inbound;
        }

        public boolean matches(Protocol p, int port, String ip, boolean in) {
            boolean protoMatch = protocol == Protocol.ALL || protocol == p;
            boolean portMatch = port >= fromPort && port <= toPort;
            boolean dirMatch = inbound == in;
            boolean ipMatch = sourceCidr.equals("0.0.0.0/0");
            if (!ipMatch) {
                String srcPrefix = sourceCidr.substring(0, sourceCidr.indexOf('/'));
                ipMatch = ip.startsWith(srcPrefix.substring(0, srcPrefix.lastIndexOf('.')));
            }
            return protoMatch && portMatch && dirMatch && ipMatch;
        }

        @Override
        public String toString() {
            return (inbound ? "Inbound" : "Outbound") + " " + protocol + " " + fromPort + "-" + toPort
                + " from " + sourceCidr;
        }
    }

    public static class SecurityGroup {
        public final String groupId;
        public final String groupName;
        public final List<Rule> rules = new ArrayList<>();

        public SecurityGroup(String groupId, String groupName) {
            this.groupId = groupId;
            this.groupName = groupName;
        }

        public SecurityGroup addRule(Protocol protocol, int port, String cidr, boolean inbound) {
            rules.add(new Rule(protocol, port, port, cidr, inbound));
            return this;
        }

        public SecurityGroup addRuleRange(Protocol protocol, int from, int to, String cidr, boolean inbound) {
            rules.add(new Rule(protocol, from, to, cidr, inbound));
            return this;
        }

        public boolean checkTraffic(Protocol protocol, int port, String sourceIp, boolean inbound) {
            for (Rule rule : rules) {
                if (rule.matches(protocol, port, sourceIp, inbound)) {
                    return true;
                }
            }
            return false;
        }

        public void printRules() {
            System.out.println("\n" + groupName + " (" + groupId + ") rules:");
            rules.forEach(r -> System.out.println("  " + r));
        }
    }

    public static void main(String[] args) {
        SecurityGroup webSg = new SecurityGroup("sg-web-123", "web-server-sg");
        webSg.addRule(Protocol.TCP, 80, "0.0.0.0/0", true)
             .addRule(Protocol.TCP, 443, "0.0.0.0/0", true)
             .addRule(Protocol.TCP, 22, "10.0.0.0/8", true)
             .addRule(Protocol.ALL, 0, 65535, "0.0.0.0/0", false);

        SecurityGroup dbSg = new SecurityGroup("sg-db-456", "database-sg");
        dbSg.addRule(Protocol.TCP, 3306, "10.0.0.0/8", true);

        System.out.println("=== Security Groups ===");
        webSg.printRules();
        dbSg.printRules();

        System.out.println("\n=== Traffic Evaluation ===");
        System.out.println("HTTP from internet to web-sg: " + webSg.checkTraffic(Protocol.TCP, 80, "203.0.113.1", true));
        System.out.println("SSH from internet to web-sg: " + webSg.checkTraffic(Protocol.TCP, 22, "203.0.113.1", true));
        System.out.println("SSH from internal to web-sg: " + webSg.checkTraffic(Protocol.TCP, 22, "10.0.1.5", true));
        System.out.println("MySQL from internet to db-sg: " + dbSg.checkTraffic(Protocol.TCP, 3306, "203.0.113.1", true));
    }
}
