package com.cloud.deep.lab07;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.*;

public class CloudNetworkingDeep {

    public record Vpc(String name, String cidr, String region, List<Subnet> subnets) {}
    public record Subnet(String name, String cidr, String availabilityZone, boolean isPublic) {}
    public record Route(String destination, String target, String type) {}
    public record VpcPeering(String id, Vpc requester, Vpc accepter, boolean active) {}
    public record VpnTunnel(String tunnelId, String vpnGatewayId, String customerGatewayId, String status, String insideCidr) {}
    public record DirectConnectVlan(String vlanId, int vlanNumber, long bandwidthMbps, String status) {}
    public record PrivateLinkEndpoint(String endpointId, String serviceName, String vpcId, String status) {}

    public static class RoutingTable {
        private final List<Route> routes = new CopyOnWriteArrayList<>();

        public void addRoute(Route route) { routes.add(route); }

        public Optional<String> routeTo(String destination) {
            return routes.stream()
                .filter(r -> matches(r.destination(), destination))
                .max(Comparator.comparingInt(r -> prefixLength(r.destination())))
                .map(Route::target);
        }

        private boolean matches(String cidr, String ip) {
            if (cidr.equals("0.0.0.0/0")) return true;
            var parts = cidr.split("/");
            if (parts.length != 2) return false;
            int prefix = Integer.parseInt(parts[1]);
            if (prefix == 0) return true;
            long ipLong = ipToLong(ip);
            long cidrLong = ipToLong(parts[0]);
            long mask = prefix == 0 ? 0 : (0xFFFFFFFFL << (32 - prefix));
            return (ipLong & mask) == (cidrLong & mask);
        }

        private int prefixLength(String cidr) { return Integer.parseInt(cidr.split("/")[1]); }
        private long ipToLong(String ip) { var oct = Arrays.stream(ip.split("\\.")).mapToInt(Integer::parseInt).toArray(); return (long) oct[0] << 24 | (long) oct[1] << 16 | (long) oct[2] << 8 | oct[3]; }
    }

    public static class NatGateway {
        private final String id;
        private final String elasticIp;
        private boolean active;

        public NatGateway(String id, String elasticIp) { this.id = id; this.elasticIp = elasticIp; this.active = true; }
        public String getId() { return id; }
        public boolean isActive() { return active; }
        public void setActive(boolean a) { active = a; }
    }

    public static class TransitGateway {
        private final String id;
        private final Map<String, List<String>> attachments = new ConcurrentHashMap<>();
        private final RoutingTable routeTable = new RoutingTable();

        public TransitGateway(String id) { this.id = id; }

        public String attach(String vpcId, List<String> subnets) {
            attachments.put(vpcId, subnets);
            return "tgw-attach-" + vpcId.substring(0, 4);
        }

        public void propagateRoute(String cidr, String target) { routeTable.addRoute(new Route(cidr, target, "tgw-propagation")); }
        public Optional<String> route(String destination) { return routeTable.routeTo(destination); }
    }

    public static class PrivateLinkService {
        private final String serviceName;
        private final List<PrivateLinkEndpoint> endpoints = new CopyOnWriteArrayList<>();
        private final boolean autoAccept;

        public PrivateLinkService(String serviceName, boolean autoAccept) { this.serviceName = serviceName; this.autoAccept = autoAccept; }

        public PrivateLinkEndpoint createEndpoint(String vpcId) {
            var ep = new PrivateLinkEndpoint("vpce-" + UUID.randomUUID().toString().substring(0, 4), serviceName, vpcId, autoAccept ? "available" : "pending");
            endpoints.add(ep);
            return ep;
        }

        public void acceptEndpoint(String endpointId) {
            for (int i = 0; i < endpoints.size(); i++) {
                if (endpoints.get(i).endpointId().equals(endpointId)) {
                    endpoints.set(i, new PrivateLinkEndpoint(endpointId, serviceName, endpoints.get(i).vpcId(), "available"));
                }
            }
        }

        public List<PrivateLinkEndpoint> listEndpoints() { return List.copyOf(endpoints); }
    }

    public static void main(String[] args) {
        System.out.println("=== VPC Peering ===");
        var vpc1 = new Vpc("app-vpc", "10.0.0.0/16", "us-east-1", List.of(
            new Subnet("web", "10.0.1.0/24", "us-east-1a", true),
            new Subnet("db", "10.0.2.0/24", "us-east-1b", false)));
        var vpc2 = new Vpc("data-vpc", "10.1.0.0/16", "us-east-1", List.of(
            new Subnet("analytics", "10.1.1.0/24", "us-east-1a", false)));
        var peering = new VpcPeering("pcx-1234", vpc1, vpc2, true);
        System.out.printf("Peering %s between %s and %s: active=%b%n", peering.id(), peering.requester().name(), peering.accepter().name(), peering.active());

        System.out.println("\n=== Routing ===");
        var rt = new RoutingTable();
        rt.addRoute(new Route("10.0.0.0/16", "local", "local"));
        rt.addRoute(new Route("0.0.0.0/0", "igw-123", "igw"));
        rt.addRoute(new Route("10.1.0.0/16", "pcx-1234", "peering"));
        System.out.println("Route to 10.1.1.1: " + rt.routeTo("10.1.1.1").orElse("none"));
        System.out.println("Route to 8.8.8.8: " + rt.routeTo("8.8.8.8").orElse("none"));

        System.out.println("\n=== Transit Gateway ===");
        var tgw = new TransitGateway("tgw-001");
        tgw.attach("vpc-abc", List.of("subnet-web-1", "subnet-web-2"));
        tgw.attach("vpc-def", List.of("subnet-data-1"));
        tgw.propagateRoute("10.2.0.0/16", "vpc-abc");
        tgw.propagateRoute("10.3.0.0/16", "vpc-def");
        System.out.println("TGW route for 10.2.0.5: " + tgw.route("10.2.0.5").orElse("none"));

        System.out.println("\n=== Private Link ===");
        var svc = new PrivateLinkService("com.myapp.api", false);
        var ep1 = svc.createEndpoint("vpc-client-1");
        var ep2 = svc.createEndpoint("vpc-client-2");
        System.out.println("Endpoint 1: " + ep1.status());
        svc.acceptEndpoint(ep1.endpointId());
        System.out.println("Endpoint 1 after accept: " + ep1.status());
    }
}
