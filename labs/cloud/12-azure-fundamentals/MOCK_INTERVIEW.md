# Mock Interview — Azure Fundamentals

## Format
- **Duration**: 45 minutes
- **Type**: Technical + Architecture
- **Difficulty**: Associate Level

## Warm-Up (5 min)

Q1: Explain Azure Regions, Availability Zones, and Resource Groups.

Q2: What is Azure Active Directory? How does it differ from on-premises Active Directory?

## Technical Questions (20 min)

### Question 1: Azure Compute Options (10 min)
A company wants to migrate a Java Spring Boot application and a SQL Server database to Azure. The web app has variable traffic (500-3000 req/s). The database is 200GB.

**Design the Azure architecture**: Which compute (VMs, App Service, AKS, Functions)? Which database (Azure SQL, SQL on VM, Cosmos DB)? High availability? Cost optimization?

### Question 2: Azure Networking (10 min)
Design a hub-spoke network topology in Azure:
- Hub VNet: shared services (firewall, AD, monitoring)
- 3 spoke VNets: prod, staging, dev
- On-premises connectivity via VPN
- Internet access for spoke VMs (outbound only)

**Design**: VNet peering, Azure Firewall, route tables, NAT Gateway.

## Behavioral Question (10 min)

**Question**: Tell me about a time you worked with Azure (or any cloud) in an enterprise environment. What unique challenges did enterprise requirements present?

## System Design Whiteboard (10 min)

**Problem**: Design a disaster recovery solution for a critical application running on Azure VMs and Azure SQL Database. Requirements:
- RPO: 15 minutes, RTO: 1 hour
- Cross-region DR (East US → West US)
- Automated failover
- Test failover capability (no impact on production)
- Cost-efficient DR (no idle DR infrastructure)

## Evaluation Criteria

| Area | Excellent | Good | Needs Improvement |
|------|-----------|------|-------------------|
| Azure Compute | App Service, VMs, AKS, Functions, containers | Knows VMs and App Service | Basic only |
| Azure SQL | DTU vs vCore, elastic pools, geo-replication | Knows managed SQL | No differentiation |
| Networking | Hub-spoke, Firewall, VNet peering, UDR | Basic VNet | Single VNet only |
| DR | Site Recovery, geo-replication, failover testing | Basic backup | No DR strategy |
| Identity | Azure AD, RBAC, managed identities | Basic Azure AD | No identity concept |

## Sample Solution Outline

### Java App + SQL Server on Azure
- **Compute**: App Service with Premium v3 plan, auto-scale rules (CPU > 70%)
  - Slots for staging/blue-green deployment
  - Always On enabled, 3-10 instances
- **Database**: Azure SQL Database (General Purpose, vCore-based)
  - Business Critical if low-latency required
  - Geo-replication for DR
- **Networking**: VNet integration for App Service, private endpoint for SQL
- **CDN**: Azure Front Door for global routing, WAF
- **Identity**: Managed Identity for app → SQL access (no credentials in code)
- **Monitoring**: Application Insights, Azure Monitor

### Hub-Spoke Topology
- Hub VNet: 10.0.0.0/16
  - Azure Firewall (inbound/outbound filtering)
  - Azure Bastion for management
  - Azure AD Domain Services or AD DS
  - Log Analytics workspace
- Spoke VNets: 10.1.0.0/16 (prod), 10.2.0.0/16 (stg), 10.3.0.0/16 (dev)
- VNet peering: each spoke to hub (hub not peered between spokes)
- Route tables: Spoke default route → hub Azure Firewall
- On-prem: VPN Gateway in hub → Site-to-Site VPN
- Outbound: Azure Firewall with NAT rules for spoke internet access
- Private DNS zones integrated with hub for resolution

### DR Solution
- **VMs**: Azure Site Recovery with replication to West US (continuous)
  - Recovery Plan with custom scripts for app configuration
  - Test failover: isolated network in DR region
- **Database**: Azure SQL Active Geo-Replication
  - Primary: East US, secondary: West US (readable)
  - Failover group for automated failover
- **Traffic Manager**: Priority routing — East US primary, West US backup
- **Cost optimization**:
  - ASR replication uses minimal compute in DR
  - SQL secondary is billable but readable (use for reporting)
  - Auto-shutdown of non-critical DR resources during testing
