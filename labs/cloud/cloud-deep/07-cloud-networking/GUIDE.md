# GUIDE — Cloud Networking

## Step 1: VPC Topology
```java
public record Vpc(String name, String cidr, List<Subnet> subnets, InternetGateway igw) {}
public record Subnet(String name, String cidr, String availabilityZone, boolean isPublic) {}
```

## Step 2: Routing Engine
- Build route tables with destination-target pairs
- Implement longest-prefix-match routing
- Propagate routes from VPN and Direct Connect

## Step 3: NAT Gateway
```java
NatGateway ngw = new NatGateway("ngw-1", eip);
ngw.connect(publicSubnet, privateRouteTable);
```

## Step 4: VPC Peering / Transit Gateway
```java
VpcPeeringConnection peering = vpc1.peerWith(vpc2);
peering.accept();
peering.addRoute(vpc1, vpc2.getCidr());
```

## Step 5: VPN / Direct Connect
- Model IPSec tunnel with pre-shared key
- Implement BGP session for route exchange
- Build redundant tunnels with BFD

## Step 6: Exercises
1. Implement a CIDR collision detector for VPC peering
2. Build a transit gateway route propagation simulator
3. Create a Private Link endpoint service with approval workflow
