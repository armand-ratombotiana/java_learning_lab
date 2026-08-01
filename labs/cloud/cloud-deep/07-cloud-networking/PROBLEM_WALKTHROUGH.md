# Lab 07: Problem Walkthrough — VPC Peering and Routing System

## Problem Statement

Implement a VPC peering and routing system with subnet isolation. The system must:

1. Model **VPCs** (CIDR blocks), **subnets** within VPCs, and **route tables** attached per subnet.
2. Support **peering connections** between VPCs and the routes that use them as targets.
3. Implement the **longest-prefix-match** routing decision: given a packet (source subnet, destination IP), pick the most specific matching route and resolve the next hop.
4. Enforce **subnet isolation**: subnets only reach destinations their own route table allows; the DB subnet must never route to the web subnet or to the internet.
5. Validate the model before routing: **CIDR overlap detection** between peered VPCs, **conflicting overlapping routes** (shadowing), invalid route targets, and route-prefix blackhole checks.
6. Produce a **reachability matrix**: for every (source subnet, destination subnet) pair, the routing outcome — REACHABLE (with the forwarding path), BLACKHOLE (no route), or ISOLATED (no route by design).

**Constraints**

- Peering is point-to-point and **non-transitive**: a route to a peer CIDR must resolve via a *direct* peering connection.
- IPv4 CIDR prefix math must be implemented from scratch (no external library).
- All code must compile under Java 21+.

---

## Walkthrough

### Step 1: CIDR math — prefix representation

A CIDR is (address bits, prefix length). The canonical representation is the *network address* of the prefix; two CIDRs overlap if their network addresses are equal over the shorter prefix length. Matching a specific IP against a CIDR is a shift-and-compare over the prefix bits.

```java
package com.cloud.deep.lab07;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class VpcPeeringSystem {

    public record Cidr(int address, int prefix) implements Comparable<Cidr> {
        static Cidr parse(String cidr) {
            String[] parts = cidr.split("/");
            int prefix = Integer.parseInt(parts[1]);
            long raw = ipToLong(parts[0]);
            int masked = (int) raw & maskFor(prefix);
            return new Cidr(masked, prefix);
        }

        boolean contains(int ip) {
            return (ip & maskFor(prefix)) == address;
        }

        static boolean overlaps(Cidr a, Cidr b) {
            int p = Math.min(a.prefix, b.prefix);
            return (a.address & maskFor(p)) == (b.address & maskFor(p));
        }

        static boolean prefixOf(Cidr a, Cidr b) {
            return a.prefix >= b.prefix && b.contains(a.address);
        }

        static long ipToLong(String ip) {
            long v = 0;
            for (String octet : ip.split("\\.")) {
                v = (v << 8) | Integer.parseInt(octet);
            }
            return v;
        }

        static int maskFor(int prefix) {
            return prefix == 0 ? 0 : (0xFFFFFFFF << (32 - prefix));
        }

        @Override public String toString() {
            return ((address >>> 24) & 0xFF) + "." + ((address >>> 16) & 0xFF) + "."
                    + ((address >>> 8) & 0xFF) + "." + (address & 0xFF) + "/" + prefix;
        }

        @Override public int compareTo(Cidr o) {
            int byPrefix = Integer.compare(o.prefix, prefix); // longer prefix first
            return byPrefix != 0 ? byPrefix : Integer.compare(address, o.address);
        }
    }
```

### Step 2: Model VPCs, subnets, route tables, and peering

The model uses an interface `RouteTarget` so routes can point at the local VPC, a peering connection, or a gateway — the routing engine stays provider-agnostic.

```java
    public interface RouteTarget { String name(); }

    public record LocalVpc(String vpcName) implements RouteTarget {
        @Override public String name() { return "local(" + vpcName + ")"; }
    }

    public record PeeringConnection(String name, String vpcA, String vpcB)
            implements RouteTarget {
        @Override public String name() { return "pcx:" + name; }
        boolean involves(String vpc) { return vpcA.equals(vpc) || vpcB.equals(vpc); }
    }

    public record Gateway(String name) implements RouteTarget {
        @Override public String name() { return "gw:" + name; }
    }

    public record Route(Cidr destination, RouteTarget target) {}

    public static final class RouteTable {
        private final String ownerSubnet;
        private final List<Route> routes = new ArrayList<>();

        RouteTable(String ownerSubnet) { this.ownerSubnet = ownerSubnet; }
        String owner() { return ownerSubnet; }
        void add(Route r) { routes.add(r); }
        List<Route> routes() { return List.copyOf(routes); }

        Optional<Route> longestPrefixMatch(int destIp) {
            return routes.stream()
                    .filter(r -> r.destination().contains(destIp))
                    .min(Comparator.comparing(r -> r.destination().prefix()));
        }
    }

    public record Subnet(String name, String vpc, Cidr cidr, RouteTable routes) {}

    public static final class Vpc {
        private final String name;
        private final Cidr cidr;
        private final Map<String, Subnet> subnets = new HashMap<>();
        private final Map<String, RouteTable> tables = new HashMap<>();

        Vpc(String name, Cidr cidr) {
            this.name = name;
            this.cidr = cidr;
        }

        void addSubnet(Subnet s) { subnets.put(s.name(), s); }
        RouteTable newTable(String subnetName) {
            RouteTable t = new RouteTable(subnetName);
            t.add(new Route(cidr, new LocalVpc(name))); // implicit local route
            tables.put(subnetName, t);
            return t;
        }
        Map<String, Subnet> subnets() { return Map.copyOf(subnets); }
        String name() { return name; }
        Cidr cidr() { return cidr; }
    }
```

### Step 3: The routing engine — forwarding decision

Given a source subnet and a destination IP, the engine finds the longest-prefix-match route in the source subnet's table. A route to a peering connection resolves by checking the peer is a *direct* peer (non-transitivity) — if the destination IP is in the peer's CIDR, the packet is forwarded; otherwise the route is a misconfiguration (a peer route whose prefix is outside the peer's own CIDR).

```java
    public enum ForwardOutcome { DELIVERED, BLACKHOLE, NO_ROUTE }

    public record Hop(String viaSubnet, Cidr matched, RouteTarget target) {}

    public record ForwardResult(ForwardOutcome outcome, List<Hop> path) {}

    public static final class Router {
        private final Map<String, Vpc> vpcs;
        private final List<PeeringConnection> peers;

        public Router(Map<String, Vpc> vpcs, List<PeeringConnection> peers) {
            this.vpcs = vpcs;
            this.peers = peers;
        }

        public ForwardResult forward(String sourceVpc, String sourceSubnet, int destIp) {
            Vpc vpc = vpcs.get(sourceVpc);
            Subnet sub = vpc.subnets().get(sourceSubnet);
            if (sub == null) return new ForwardResult(ForwardOutcome.NO_ROUTE, List.of());

            Optional<Route> match = sub.routes().longestPrefixMatch(destIp);
            if (match.isEmpty()) return new ForwardResult(ForwardOutcome.NO_ROUTE, List.of());
            Route r = match.get();

            List<Hop> path = new ArrayList<>();
            path.add(new Hop(sub.name(), r.destination(), r.target()));

            if (r.target() instanceof LocalVpc) {
                return new ForwardResult(ForwardOutcome.DELIVERED, path);
            }
            if (r.target() instanceof Gateway gw) {
                return new ForwardResult(ForwardOutcome.DELIVERED, path);
            }
            if (r.target() instanceof PeeringConnection pcx) {
                // Non-transitive: the destination must be inside the peer's CIDR.
                Vpc peer = pcx.involves(sourceVpc)
                        ? vpcs.get(pcx.vpcA().equals(sourceVpc) ? pcx.vpcB() : pcx.vpcA())
                        : null;
                if (peer != null && peer.cidr().contains(destIp)) {
                    return new ForwardResult(ForwardOutcome.DELIVERED, path);
                }
                return new ForwardResult(ForwardOutcome.BLACKHOLE, path);
            }
            return new ForwardResult(ForwardOutcome.BLACKHOLE, path);
        }
    }
```

### Step 4: Model validation — catching misconfiguration before traffic flows

The validator enforces the invariants:

1. Peered VPC CIDRs must not overlap.
2. No two routes in a table may conflict: overlapping prefixes with different targets.
3. A peer route's prefix must be inside the peer's CIDR (no blackhole through the peer).
4. A route prefix more specific than the local VPC CIDR pointing at a non-local target is a shadow-route check.

```java
    public record ValidationIssue(String severity, String message) {}

    public static final class Validator {
        public List<ValidationIssue> validate(Map<String, Vpc> vpcs,
                                              List<PeeringConnection> peers) {
            List<ValidationIssue> issues = new ArrayList<>();

            for (PeeringConnection pcx : peers) {
                Vpc a = vpcs.get(pcx.vpcA());
                Vpc b = vpcs.get(pcx.vpcB());
                if (a == null || b == null) {
                    issues.add(new ValidationIssue("ERROR", "Peer " + pcx.name()
                            + " references missing VPC"));
                    continue;
                }
                if (Cidr.overlaps(a.cidr(), b.cidr())) {
                    issues.add(new ValidationIssue("ERROR", "Peer " + pcx.name()
                            + ": overlapping CIDRs " + a.cidr() + " and " + b.cidr()));
                }
            }

            for (Vpc vpc : vpcs.values()) {
                for (Subnet sub : vpc.subnets().values()) {
                    List<Route> routes = sub.routes().routes();
                    for (int i = 0; i < routes.size(); i++) {
                        for (int j = i + 1; j < routes.size(); j++) {
                            Route x = routes.get(i), y = routes.get(j);
                            if (Cidr.overlaps(x.destination(), y.destination())
                                    && !x.target().name().equals(y.target().name())) {
                                issues.add(new ValidationIssue("WARN", sub.name()
                                        + ": conflicting routes " + x.destination()
                                        + " -> " + x.target().name() + " vs "
                                        + y.destination() + " -> " + y.target().name()));
                            }
                        }
                        Route r = routes.get(i);
                        if (r.target() instanceof PeeringConnection pcx) {
                            Vpc peer = pcx.involves(vpc.name())
                                    ? vpcs.get(pcx.vpcA().equals(vpc.name()) ? pcx.vpcB() : pcx.vpcA())
                                    : null;
                            if (peer != null && !peer.cidr().contains(r.destination().address())) {
                                issues.add(new ValidationIssue("ERROR", sub.name()
                                        + ": peer route " + r.destination() + " outside "
                                        + peer.name() + " CIDR " + peer.cidr()));
                            }
                        }
                    }
                }
            }
            return issues;
        }
    }
```

### Step 5: Reachability matrix

For every (source subnet, destination subnet) pair, the matrix computes the forwarding outcome. This is the test oracle of the whole design: the web subnet reaches the app subnet, the app subnet reaches the DB subnet, and the DB subnet reaches *nothing* external (its route table has no peer or internet routes).

```java
    public static final class ReachabilityMatrix {
        public record Row(String from, String to, ForwardOutcome outcome, List<Hop> path) {}

        public List<Row> compute(Router router, Map<String, Vpc> vpcs) {
            List<Row> rows = new ArrayList<>();
            List<String> allSubnets = new ArrayList<>();
            for (Vpc v : vpcs.values())
                for (String s : v.subnets().keySet())
                    allSubnets.add(v.name() + "/" + s);

            for (String from : allSubnets) {
                for (String to : allSubnets) {
                    if (from.equals(to)) continue;
                    String[] fromParts = from.split("/");
                    String[] toParts = to.split("/");
                    Vpc toVpc = vpcs.get(toParts[0]);
                    int destIp = toVpc.subnets().get(toParts[1]).cidr().address() + 5;
                    ForwardResult r = router.forward(fromParts[0], fromParts[1], destIp);
                    rows.add(new Row(from, to, r.outcome(), r.path()));
                }
            }
            return rows;
        }
    }
```

### Step 6: Demo — the full topology

Topology: VPC-A (10.0.0.0/16) with `web` (10.0.1.0/24) and `db` (10.0.2.0/24); VPC-B (10.1.0.0/16) with `app` (10.1.1.0/24); VPC-C (10.2.0.0/16) with `analytics`. Peering: A↔B, B↔C.

Routing intent: web→app via peering; app→db via return route; db has *no* external routes (isolation); analytics reachable only from app (B↔C peering) — **not** from web (non-transitivity).

```java
    public static void main(String[] args) {
        Vpc vpcA = new Vpc("vpc-a", Cidr.parse("10.0.0.0/16"));
        Vpc vpcB = new Vpc("vpc-b", Cidr.parse("10.1.0.0/16"));
        Vpc vpcC = new Vpc("vpc-c", Cidr.parse("10.2.0.0/16"));

        Subnet web = new Subnet("web", "vpc-a", Cidr.parse("10.0.1.0/24"), vpcA.newTable("web"));
        Subnet db = new Subnet("db", "vpc-a", Cidr.parse("10.0.2.0/24"), vpcA.newTable("db"));
        Subnet app = new Subnet("app", "vpc-b", Cidr.parse("10.1.1.0/24"), vpcB.newTable("app"));
        Subnet analytics = new Subnet("analytics", "vpc-c", Cidr.parse("10.2.1.0/24"),
                vpcC.newTable("analytics"));

        vpcA.addSubnet(web);
        vpcA.addSubnet(db);
        vpcB.addSubnet(app);
        vpcC.addSubnet(analytics);

        PeeringConnection pcxAB = new PeeringConnection("a-b", "vpc-a", "vpc-b");
        PeeringConnection pcxBC = new PeeringConnection("b-c", "vpc-b", "vpc-c");

        web.routes().add(new Route(Cidr.parse("10.1.0.0/16"), pcxAB));   // web -> app
        app.routes().add(new Route(Cidr.parse("10.0.2.0/24"), pcxAB));   // app -> db (return path)
        app.routes().add(new Route(Cidr.parse("10.2.0.0/16"), pcxBC));   // app -> analytics
        // db route table intentionally has ONLY the implicit local route.

        Map<String, Vpc> vpcs = new HashMap<>();
        vpcs.put("vpc-a", vpcA);
        vpcs.put("vpc-b", vpcB);
        vpcs.put("vpc-c", vpcC);
        List<PeeringConnection> peers = List.of(pcxAB, pcxBC);

        System.out.println("=== VPC Peering & Routing Demo ===\n");

        System.out.println("-- Model validation --");
        List<ValidationIssue> issues = new Validator().validate(vpcs, peers);
        if (issues.isEmpty()) System.out.println("  (no issues)");
        issues.forEach(i -> System.out.println("  [" + i.severity() + "] " + i.message()));

        System.out.println("\n-- Reachability matrix --");
        List<ReachabilityMatrix.Row> rows = new ReachabilityMatrix()
                .compute(new Router(vpcs, peers), vpcs);
        for (ReachabilityMatrix.Row row : rows) {
            System.out.printf("  %-10s -> %-12s %-10s %s%n", row.from(), row.to(),
                    row.outcome(), row.path().isEmpty() ? ""
                            : "via " + row.path().get(0).target().name()
                            + " [" + row.path().get(0).matched() + "]");
        }

        System.out.println("\n-- Validate shadow-route detection --");
        web.routes().add(new Route(Cidr.parse("10.0.0.0/8"), new Gateway("igw")));
        Validator validator = new Validator();
        validator.validate(vpcs, peers).forEach(i ->
                System.out.println("  [" + i.severity() + "] " + i.message()));
    }
}
```

### Step 7: Verify the expected matrix

| From | To | Outcome | Why |
|------|----|---------|-----|
| web | app | DELIVERED via pcx:a-b | Peer route 10.1.0.0/16 |
| web | db | DELIVERED via local | Local route |
| web | analytics | NO_ROUTE | No route to 10.2.0.0/16 in web's table (non-transitive peering) |
| db | anything external | NO_ROUTE | DB route table has only the local route — isolation |
| app | db | DELIVERED via pcx:a-b | Peer route 10.0.2.0/24 |
| app | analytics | DELIVERED via pcx:b-c | Peer route 10.2.0.0/16 |
| analytics | app | NO_ROUTE | No route added back (one-way by design) |

The added `10.0.0.0/8 → igw` route in web's table triggers the conflict warning (overlaps the local 10.0.0.0/16 route with a different target) — shadow-route detection working as intended.

---

## Complexity Analysis

- **Longest-prefix match**: O(R) per packet over the subnet's route table — production engines use a compressed trie (LC-trie) for O(P) lookups where P = prefix length (32).
- **Validation**: O(V² · R²) worst case over VPCs and routes — runs on model changes, not per packet.
- **Reachability matrix**: O(N² · R) for N subnets — one LPM per pair; cached per source subnet for N× fewer lookups.
- **CIDR containment/overlap**: O(1) — pure bitwise operations.
- **Space**: O(V + S + R + P) for VPCs, subnets, routes, peers — negligible; the matrix output is O(N²) rows for reporting.

---

## Follow-Up Questions

1. **How do you scale LPM to production packet rates?** A binary trie over the 32-bit key space, shared across route tables with per-table root pointers; longest-prefix-match becomes O(32) worst case. Hardware switches use TCAM for O(1) — the software answer is the trie.

2. **How do you detect asymmetric routing between peers?** Compare the forward path with the reverse path for the same (src, dst) pair in the reachability matrix; a route table missing the return route is the classic cause (exactly the db-subnet trap from the demo).

3. **How does a transit gateway change this model?** Add `TransitGateway` as another `RouteTarget`; routes point at the hub, the hub's own route tables decide which attachment the packet exits through — enabling transitive routing *explicitly* with central policy, unlike peering.

4. **How do you handle IPv6 in the same engine?** Generalize `Cidr` to a 128-bit key (two longs) with the same prefix math; the route table key becomes (address family, prefix) so v4 and v6 routes coexist.

5. **What about dynamic routing (BGP) into the model?** Replace static route addition with a route-learned channel: BGP sessions advertise prefixes into the route table with a priority (static > BGP > default) — the LPM still decides among installed routes; the validator then also checks redistribution loops.

6. **How do you map this to real cloud providers?** The `RouteTarget` abstraction maps to AWS VPC peering IDs / transit gateway attachments, Azure VNet peerings, and GCP VPC peering; the validation and matrix logic run identically — this is exactly what a multi-cloud network policy tool does.
