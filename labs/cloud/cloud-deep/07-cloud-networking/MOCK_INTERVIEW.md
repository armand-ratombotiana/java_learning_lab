# Lab 07: Mock Interview — Senior Network/Cloud Engineer

**Role**: Senior Cloud Network Engineer | **Topic**: VPC Peering and Routing with Subnet Isolation | **Duration**: 45 minutes

---

## Interview Transcript

**Interviewer**: "Design a VPC peering and routing system. Two VPCs need to talk to each other, subnets must be isolated from each other, and traffic must follow a well-defined routing model. Where do you start?"

**Candidate**: "I start with the routing model, because peering is a means to an end: it's how two isolated networks get a *point-to-point* path between them. The mental model I use: a VPC is a black box with a route table per subnet; peering adds a 'peer' target to a route. The three things I'd define up front: the addressing plan (no overlapping CIDRs between peers — peering requires disjoint address space), the route table structure (which subnets route where), and the isolation model (which subnets are allowed to reach the peer at all, and what the security boundaries are). Everything else — the peering connection object, the route propagation, the security groups — hangs off those three decisions."

**Interviewer**: "Let's say VPC-A (10.0.0.0/16) and VPC-B (10.1.0.0/16). VPC-A has a public web subnet and a private DB subnet; VPC-B has an application subnet. The web tier needs to talk to the app tier, and the app tier needs to talk to the database — but the web subnet must never reach the database subnet directly. How do you lay out the routes?"

**Candidate**: "Route tables are per-subnet, and that's the isolation mechanism — isolation is enforced by *which routes a subnet has*, not by filtering. So: VPC-A's web subnet route table gets a route `10.1.0.0/16 → pcx-A-B`. VPC-A's DB subnet route table — here's the design decision — gets *no* route to the peer, so even though the peering connection exists, the DB subnet has no path to VPC-B. And the critical detail that people miss: a route table without a peer route doesn't mean the peer can't reach *us*. VPC-B's app subnet gets a route `10.0.0.0/16 → pcx-A-B`, so the app tier can initiate connections to the DB. Whether the DB can respond is determined by the *return path*: since the DB subnet route table has no peer route, responses from the DB to the app will fail — the route lookup is done per-packet at each hop, and the return packet in the DB subnet would have no route to 10.1.0.0/16. So if the app must reach the DB, the DB subnet needs a route `10.1.0.0/16 → pcx` for the return traffic — which then makes the DB subnet *reachable* from the peer. That's the moment where security decisions must be made: route = reachability; the *deep* protection on top is network ACLs and security groups."

**Interviewer**: "So routes alone aren't the security boundary. What is?"

**Candidate**: "Right — routes answer 'can a packet leave my subnet with a destination prefix?'; they don't answer 'is this traffic allowed?'. The security boundary is defense-in-depth: network ACLs at the subnet level (stateless, evaluation order matters, allow/deny rules with port ranges) and security groups at the instance level (stateful, evaluated as a set of allow rules). The layered model I'd insist on: (1) route tables give the topological isolation — the DB subnet has no route to the public internet and no route to the web subnet; (2) NACLs give the coarse-grained boundary — e.g., the DB subnet's NACL allows only 10.1.0.0/16 and only port 5432 from the app tier; (3) security groups give the fine-grained instance policy — the DB security group allows 5432 only from the app tier's security group ID, not from an IP range. The difference between NACL and SG is an interview favorite: NACL is stateless — you must write both inbound and outbound rules, and it's applied at the subnet edge; SG is stateful — a single inbound rule implies the corresponding return flow."

**Interviewer**: "Now the interesting failure mode: what if someone makes a subnet route table that catches traffic destined for the peer with a *more specific* or *less specific* route? How does the longest-prefix-match rule interact with peering routes?"

**Candidate**: "Longest prefix match wins, always — and that's the source of most routing bugs. Suppose the DB subnet has a route `10.0.0.0/16 → local` (VPC's own implicit route) and a route `10.1.0.0/16 → pcx`. If someone adds a *supernet* route `10.0.0.0/8 → some gateway` — that's the classic disaster, it's what happened in well-known outages — then traffic to `10.1.0.5`... no wait, 10.1.0.0/16 is more specific than 10.0.0.0/8, so the /16 peer route wins. The dangerous case is the *reverse*: a more-specific route `10.1.2.0/24 → some VPC endpoint` shadowing the peer route for part of the peer's space, silently blackholing or redirecting traffic to a subset of the peer's subnets. The other classic: an overlapping CIDR in a *third* VPC — when a new VPC is peered whose CIDR overlaps a peer route, the new route can shadow the old one with equal prefix length (behavior: the newer route wins in some implementations), causing asymmetric routing where packets go one way through peer A and come back through peer B. My system would add a routing-audit check: detect any two routes with overlapping prefixes and different targets, and flag them as conflicts."

**Interviewer**: "What about transitive peering — can traffic go from VPC-A through VPC-B to reach VPC-C? That's the classic peering misunderstanding."

**Candidate**: "No — peering is point-to-point and **non-transitive**. VPC-A peered with VPC-B and VPC-B peered with VPC-C does NOT give A a path to C. Packets routed through a peering connection are not re-examined by the peer's route tables for further peering hops; the route table of the *destination VPC* handles only its local destinations. If A sends to C's IP, A's route table must have a direct route to C's CIDR via a *direct* peering connection A-C. The non-transitivity is a feature: it keeps the peering graph manageable and prevents routing loops and security holes. If you need transitive routing, the architecture answer is a **transit gateway** — a hub that every VPC attaches to, with route tables per attachment and centralized policy — or for cross-cloud, a cloud-agnostic transit layer. My design would model exactly this: `Vpc`, `Subnet`, `RouteTable`, `PeeringConnection`, plus an optional `TransitGateway` hub for the transit case, and a validation rule that peering is never used transitively."

**Interviewer**: "How do you validate the design before traffic flows? What are the checks?"

**Candidate**: "A validation pass over the whole model: (1) CIDR disjointness — no two peered VPCs may overlap, because overlapping space makes longest-prefix-match ambiguous; (2) route target validity — every route's target must exist (local VPC, peering ID, gateway); (3) route-prefix sanity — no route more specific than the VPC's own CIDR pointing at a non-local target *inside* the VPC (that's a blackhole generator); (4) no conflicting overlapping routes; (5) the isolation invariants — e.g., 'the DB subnet has no internet route' — expressed as declarative policies. Then the connectivity test: compute the path a packet would take from any (source subnet, dest IP) using longest-prefix-match on the source subnet's route table, and produce a reachability matrix — this is exactly what AWS's Reachability Analyzer does, and implementing a simplified version in code is the right way to prove the model."

**Interviewer**: "Cross-cloud VPC peering — AWS to Azure — how does that work? The mechanics are provider-specific."

**Candidate**: "There is no such thing as direct VPC peering across providers — each provider's peering is internal. Cross-cloud connectivity goes through a different layer: an encrypted tunnel over the public internet (IPsec site-to-site VPN), a provider-agnostic overlay (WireGuard-style mesh, which is how many multi-cloud mesh products work), or a private interconnect fabric (partner interconnects that bridge cloud exchanges). The design principle survives though: you still model it as a route with a target — the target is now a VPN connection or overlay interface instead of a peering ID, and the *same* longest-prefix-match routing and the same isolation-by-route-table logic apply. The abstraction in my design: a `RouteTarget` interface with implementations `LocalVpc`, `PeeringConnection`, `TransitGatewayAttachment`, `VpnTunnel`, so the routing engine is provider-agnostic and the same validation rules run everywhere."

**Interviewer**: "How do you test the routing system? A routing bug is silent — traffic works until it doesn't."

**Candidate**: "The reachability matrix is the test oracle: for every (subnet, destination) pair, compute the forwarding path and assert the expected outcome — allowed with path X, dropped at subnet edge, blackholed. That's a golden test that catches the shadowing and transitivity bugs. Then integration tests with real peering in a staging cloud account: create the VPCs, peer, apply routes, run the matrix, and compare. And the operational check that catches drift: a scheduled re-run of the validation pass against the *live* environment — cloud configurations drift when someone edits a route table by hand — the live model must match the declared model, or the audit fails and pages the network team."

**Interviewer**: "What's your single most important piece of advice for running peered VPCs in production?"

**Candidate**: "Treat the routing model as code with full review — the same discipline as IaC. The highest-value specific rule: **one route table per subnet, never shared by accident**, because the moment two subnets share a route table, your isolation invariants are unenforceable by construction. And run the conflict/validation audit on every change, because the failure mode isn't the peering connection — it's the route someone added at 2 AM to 'make it work' that shadows a peer route and silently re-routes traffic."

---

## Wrap-Up

**What the interviewer is looking for**:
- Route tables as the isolation mechanism (per-subnet route tables)
- Longest-prefix-match correctness and the shadowing/overlap failure modes
- Non-transitivity of peering and the transit gateway as the transitive answer
- The stateless NACL vs stateful security group distinction
- Return-path reasoning: a missing peer route on the destination subnet kills responses
- Validation via reachability analysis

**Common mistakes candidates make**:
- Believing peering is transitive
- Treating security groups as the only boundary and ignoring route tables
- Forgetting the return path on the destination subnet's route table
- Not checking CIDR overlap before peering
- Sharing one route table across subnets that need isolation
