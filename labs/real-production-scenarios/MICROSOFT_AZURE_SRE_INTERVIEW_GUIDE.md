# Microsoft Azure SRE Interview Guide — Real Production Scenarios Academy

## Interview Process for Azure SRE Roles

### Rounds
1. **Phone Screen (45 min)**: Azure fundamentals, SRE principles, one debugging scenario
2. **Onsite (4-5 rounds, 45-60 min each)**:
   - **Azure Architecture**: Design a solution using Azure services (AKS, Cosmos DB, Event Hubs, Traffic Manager)
   - **SRE Incident Response**: On-call simulation with log/metric analysis
   - **Coding/Automation**: PowerShell, Azure CLI, or ARM/Bicep templates — automation-focused
   - **Systems Design**: Reliability design with Azure building blocks
   - **Behavioral (STAR)**: Growth mindset, customer-obsession, dealing with ambiguity

### Azure SRE-Specific Expectations
- Microsoft's SRE culture focuses on "Live Site" culture — first priority is keeping the service running
- Expect deep knowledge of Azure infrastructure: AKS, Cosmos DB, Azure SQL, Service Bus, Traffic Manager, Front Door
- Automation via PowerShell, Azure CLI, ARM/Bicep, Terraform is critical
- Microsoft values "customer-obsession" — incidents are framed in terms of customer impact
- Must understand Azure Monitor, Application Insights, Log Analytics Workspace (KQL is essential)
- "Growth mindset" — expected to learn from failures and share learnings broadly

### Round Breakdown
- Azure Architecture: 30% — Azure service selection and configuration
- SRE Incident Response: 30% — KQL queries, live site debugging
- Coding/Automation: 20% — PowerShell, ARM/Bicep, scripting
- Behavioral: 20% — Microsoft culture fit

### Azure CLI and ARM/Bicep Cheat Sheet

Master these Azure CLI / Bicep patterns for interview scenarios:

**Azure CLI — Quick diagnostics**:
```bash
# Check AKS pod status
az aks get-credentials --resource-group prod-rg --name prod-aks
kubectl get pods --all-namespaces | grep -E "CrashLoop|Error|Evicted"

# Check Azure SQL connection count
az sql db show --resource-group prod-rg --server prod-svr --name orders-db \
  --query "currentServiceObjectiveName"

# Check certificate expiry
az keyvault certificate list --vault-name prod-kv \
  --query "[].{Name:name,Expiry:attributes.expires}"
```

**Bicep — Common resource templates**:

```bicep
// AKS cluster with monitoring
resource aks 'Microsoft.ContainerService/managedClusters@2023-01-01' = {
  name: 'prod-aks'
  location: resourceGroup().location
  properties: {
    kubernetesVersion: '1.27'
    enableRBAC: true
    agentPoolProfiles: [
      { name: 'systempool', count: 3, vmSize: 'Standard_D4s_v3' }
    ]
    addonProfiles: {
      omsagent: { enabled: true }
    }
  }
}

// Cosmos DB with multi-region
resource cosmos 'Microsoft.DocumentDB/databaseAccounts@2023-04-15' = {
  name: 'prod-cosmos'
  location: 'East US'
  properties: {
    locations: [
      { locationName: 'East US', failoverPriority: 0 }
      { locationName: 'West US', failoverPriority: 1 }
    ]
    consistencyPolicy: { defaultConsistencyLevel: 'Session' }
  }
}
```

**Azure Monitor queries (KQL)**:
```kusto
// AKS container logs with errors
ContainerLog
| where TimeGenerated > ago(1h)
| where LogEntry contains "ERROR" or LogEntry contains "FATAL"
| project TimeGenerated, ContainerName, LogEntry
| take 100

// App Service HTTP errors
AppServiceHTTPLogs
| where TimeGenerated > ago(30m)
| where ScStatus >= 500
| summarize ErrorCount = count() by Url
| order by ErrorCount desc
```

## Top Incidents Aligned to Microsoft Azure SRE Focus

### Incident: AKS Pod CrashLoopBackOff (Lab 11)
#### Problem Scenario
An AKS cluster running a .NET Core payment processing service experiences multiple pods in `CrashLoopBackOff` state. The deployment was rolled out via Helm chart 10 minutes ago. Azure Monitor alerts show `KubePodCrashLooping` across 6 of 10 replicas.

#### Interview Walkthrough
**Step 1 — Check pod status**: `kubectl get pods -n payment -o wide` — 6 pods show `CrashLoopBackOff`. `kubectl describe pod payment-7d8f9g2h1-abcde -n payment` shows the pod restarted 5 times with `ExitCode: 139` (SIGSEGV — segmentation fault).

**Step 2 — Check container logs**: `kubectl logs payment-7d8f9g2h1-abcde --previous -n payment` — stream terminates with "Fatal error. Internal CLR error. (0x80131500)". This is a .NET runtime crash.

**Step 3 — Check Azure Monitor**: Run a KQL query:
```kusto
KubePodInventory
| where Namespace == "payment"
| where PodStatus == "CrashLoopBackOff"
| project TimeGenerated, PodName, ContainerName, PodStatus, RestartCount
| order by TimeGenerated desc
```

**Step 4 — Root cause**: The new deployment upgraded .NET from 6.0 to 8.0. The application uses a native library compiled for .NET 6.0 — memory layout changed in .NET 8.0, causing segfault.

**Step 5 — Fix**: Rollback the Helm chart. Test .NET 8.0 upgrade with native library in staging first.

**What Microsoft evaluates**: AKS troubleshooting; container exit code interpretation; .NET runtime understanding; Helm rollback skills; KQL.

#### Solution
```bash
# Immediate rollback
helm rollback payment-service 1 --namespace payment
kubectl rollout status deployment/payment-service -n payment

# Verify recovery
kubectl get pods -n payment | grep -c Running

# KQL for crash loop detection
let crashThreshold = 3;
KubePodInventory
| where ClusterName == "aks-prod-eastus"
| where Namespace == "payment"
| extend CrashLoop = iif(PodStatus == "CrashLoopBackOff", 1, 0)
| summarize CrashCount = sum(CrashLoop), TotalPods = dcount(PodName)
| where CrashCount > crashThreshold
```

**Post-mortem**: Add .NET runtime version to Helm chart annotations. Add native library compatibility test in CI. Add crash loop alert with auto-rollback.

#### Follow-ups
- **At Azure scale**: Use Azure Policy to enforce .NET runtime version consistency. Use ACR with image scanning for runtime compatibility.
- **Prevention**: Add staging canary deployment that runs new images for 10 minutes before rolling to production.

### Incident: Connection Pool Exhaustion — Azure SQL (Lab 04)
#### Problem Scenario
Azure SQL Database for the "inventory-service" reaches MAX_SESSIONS limit. The service returns 500 errors. Azure Monitor shows `connection_successful` metric dropping to 0.

#### Interview Walkthrough
**Step 1 — Check Azure SQL metrics**: `dtu_consumption_percent` at 100% and `sessions_percent` at 100%.

**Step 2 — Query active sessions**:
```sql
SELECT COUNT(*) AS ActiveSessions, state
FROM sys.dm_exec_sessions
GROUP BY state;
```
300 sessions are in `SLEEPING` state. Only 20 are `RUNNING`.

**Step 3 — Root cause**: Application doesn't close connections in `finally` blocks. Azure SQL keeps idle sessions for 30 minutes.

**Step 4 — Immediate fix**: Kill idle sessions via script. Long-term: use `using` statements in C#, enable connection pooling.

**What Microsoft evaluates**: Azure SQL internals; session management; immediate and long-term solution.

#### Solution
```sql
DECLARE @sessionId INT;
DECLARE session_cursor CURSOR FOR
SELECT session_id FROM sys.dm_exec_sessions
WHERE status = 'sleeping'
  AND login_name = 'app_user'
  AND last_request_end_time < DATEADD(MINUTE, -5, GETUTCDATE());
OPEN session_cursor;
FETCH NEXT FROM session_cursor INTO @sessionId;
WHILE @@FETCH_STATUS = 0
BEGIN
    EXEC('KILL ' + @sessionId);
    FETCH NEXT FROM session_cursor INTO @sessionId;
END
CLOSE session_cursor;
DEALLOCATE session_cursor;
```

```csharp
public InventoryItem GetInventoryItem(string itemId) {
    string sql = "SELECT * FROM Inventory WHERE ItemId = @ItemId";
    using var connection = new SqlConnection(connectionString);
    using var command = new SqlCommand(sql, connection);
    command.Parameters.AddWithValue("@ItemId", itemId);
    connection.Open();
    using var reader = command.ExecuteReader();
    if (reader.Read()) {
        return new InventoryItem { ItemId = (string)reader["ItemId"] };
    }
    return null;
}
```

### Incident: TLS Certificate Expiry — Azure Front Door (Lab 12)
#### Problem Scenario
Azure Front Door reports "Backend health probe failed" for the production endpoint. End users receive 502 errors. The TLS certificate for the backend expired.

#### Interview Walkthrough
**Step 1 — Check Front Door health**: All backend pools show `Unhealthy`.

**Step 2 — Verify with OpenSSL**: `openssl s_client -connect backend.example.com:443 -servername backend.example.com` shows expired certificate.

**Step 3 — Root cause**: App Service Managed Certificate auto-renewal failed because DNS validation record was removed during migration.

**Step 4 — Fix**: Upload new certificate from Key Vault and associate with Front Door.

#### Solution
```bash
az keyvault certificate create \
  --vault-name prod-kv \
  --name wildcard-backend \
  --policy @cert-policy.json

az network front-door frontend-endpoint update \
  --front-door-name prod-fd \
  --name api-endpoint \
  --certificate-source KeyVault \
  --secret-name wildcard-backend
```

### Incident: High CPU — .NET GC Storm (Lab 03)
#### Problem Scenario
An Azure App Service (.NET Core 6.0) shows 100% CPU and exceeds memory limit (4GB). P95 latency increased from 200ms to 5 seconds.

#### Interview Walkthrough
**Step 1 — Check App Service metrics**: `CPU Time` shows step function increase. `Memory Working Set` exceeds limit.

**Step 2 — Enable Profiler**: Application Insights Profiler shows 60% of CPU in `gc_heap::plan_phase`. GC Gen2 collections every 3 seconds.

**Step 3 — Root cause**: A feature reads entire 10MB documents into `string` objects. High allocation rate causes Gen0 to fill every 100ms, promoting to Gen1/Gen2 quickly. Gen2 blocking GC causes the CPU spike.

**Step 4 — Fix**: Use `Span<byte>` and `ArrayPool<byte>`. Process documents in streaming fashion.

**What Microsoft evaluates**: .NET GC internals; Application Insights Profiler; memory allocation patterns.

#### Solution
```csharp
public string ProcessDocument(Stream documentStream) {
    byte[] buffer = ArrayPool<byte>.Shared.Rent(8192);
    var sb = new StringBuilder((int)documentStream.Length * 2);
    try {
        int bytesRead;
        while ((bytesRead = documentStream.Read(buffer, 0, buffer.Length)) > 0) {
            for (int i = 0; i < bytesRead; i++) {
                sb.Append(buffer[i].ToString("X2"));
            }
        }
        return sb.ToString();
    } finally {
        ArrayPool<byte>.Shared.Return(buffer);
    }
}
```

### Incident: Azure Service Bus Consumer Lag (Lab 13)
#### Problem Scenario
An order processing system uses Azure Service Bus queues. The "order-fulfillment" queue depth grows from 0 to 500k messages in 30 minutes.

#### Interview Walkthrough
**Step 1 — Check Service Bus metrics**: `ActiveMessages` growing linearly. `IncomingMessages` is 1000/s, `OutgoingMessages` is 200/s.

**Step 2 — Check Function App**: Application Insights shows `ProcessOrder` average duration 25 seconds, `Success=False` for 80%.

**Step 3 — Check logs**: KQL:
```kusto
FunctionsLogs
| where FunctionName == "ProcessOrder"
| where Level == "Error"
| summarize Count = count() by Message
```
Error: "The database 'orderdb' has reached its size quota."

**Step 4 — Root cause**: Azure SQL reached max size of 250GB. INSERT operations fail. Function retries indefinitely — poison message loop.

**Step 5 — Fix**: Increase Azure SQL max size. Clear queue of poison messages. Add dead-letter queue.

#### Solution
```bash
az sql db update --resource-group prod-rg --server order-svr \
  --name orderdb --max-size 500GB
```

```csharp
[FunctionName("ProcessOrder")]
public async Task Run(
    [ServiceBusTrigger("order-fulfillment", Connection = "ServiceBusConnection")]
    Message message, ILogger log)
{
    try {
        await processOrder(message);
    }
    catch (DbException ex) when (ex.Message.Contains("size quota")) {
        await deadLetterClient.SendAsync(message);
        log.LogWarning("Order moved to DLQ: {OrderId}", message.MessageId);
    }
}
```

### Incident: Kubernetes CrashLoop — ConfigMap Mount Issue (Lab 11)
#### Problem Scenario
After applying a new ConfigMap to AKS, all pods in "auth-service" crash with `Error: 'Failed to mount ConfigMap volume'`.

#### Interview Walkthrough
**Step 1 — Check pod events**: `MountVolume.SetUp failed for volume "config" : secrets "auth-config" not found`.

**Step 2 — Root cause**: Team member deleted old ConfigMap. Deployment still references old name.

**Step 3 — Fix**: Recreate the missing ConfigMap. Use Helm with immutable ConfigMaps.

#### Solution
```bash
kubectl create configmap auth-config \
  --from-literal=AUTH_ENDPOINT=https://auth.internal \
  --from-literal=TOKEN_TTL=3600 -n auth
kubectl wait --for=condition=Ready pod -l app=auth-service -n auth --timeout=60s
```

### Incident: DR Failover — Azure Region Outage (Lab 15)
#### Problem Scenario
The primary Azure region (East US) experiences a storage service degradation. All Azure SQL databases and blob storage in East US are inaccessible. The application must fail over to West US.

#### Interview Walkthrough
**Step 1 — Assess the situation**: Azure Service Health shows "Storage accounts in East US — degraded". All VMs using managed disks are affected. The geo-replicated secondary region is West US.

**Step 2 — Initiate failover**: `az account set --subscription prod-westus`. For Cosmos DB, the failover is automatic (multi-master). For Azure SQL, trigger geo-replication failover: `az sql db replica create --resource-group prod-rg --server eastus-svr --name orders-db --partner-server westus-svr`.

**Step 3 — Redirect traffic**: Update Traffic Manager or Front Door to point to West US endpoints. `az network traffic-manager endpoint update --profile-name prod-tm --name westus-endpoint --priority 1 --endpoint-status Enabled`.

**Step 4 — Verify failover**: Run a smoke test: `curl -f https://api.contoso.com/health`. All services operational. RTO: 12 minutes (within 1 hour target). RPO: less than 5 seconds (Cosmos DB multi-master).

**Step 5 — Root cause**: Azure region-level storage degradation. No design flaw — the multi-region architecture performed as expected.

**What Microsoft evaluates**: DR planning; Azure service failover mechanics; RTO/RPO understanding; calm under pressure.

#### Solution
```bash
# Trigger Azure SQL geo-replication failover
az sql db replica create \
  --resource-group prod-rg \
  --server eastus-svr \
  --name orders-db \
  --partner-server westus-svr \
  --failover-group-name orders-failover-group

# Update Traffic Manager endpoint priority
az network traffic-manager endpoint update \
  --resource-group prod-rg \
  --profile-name prod-tm \
  --name westus-endpoint \
  --type azureEndpoints \
  --priority 1 \
  --endpoint-status Enabled

# Verify all endpoints healthy
az network traffic-manager endpoint show \
  --resource-group prod-rg \
  --profile-name prod-tm \
  --name westus-endpoint \
  --query "endpointStatus"
```

#### Follow-ups
- **At Azure scale**: Use Azure Site Recovery for automated DR orchestration. Use Cosmos DB multi-master for zero-downtime failover. Run quarterly DR drills.
- **Prevention**: Implement Chaos Studio experiments that simulate regional failures to validate DR procedures.

### Incident: Connection String Leak — Security Breach (Lab 10)
#### Problem Scenario
A developer accidentally commits Azure SQL connection strings to a public GitHub repository. Within 2 hours, the database is compromised — data exfiltration detected.

#### Interview Walkthrough
**Step 1 — Rotate credentials immediately**: `az sql db show-connection-string --server order-svr --name orderdb --client ado`. Then `az sql server update --resource-group prod-rg --name order-svr --admin-password <new-password>`.

**Step 2 — Audit access**: Check Azure SQL audit logs. `sys.fn_get_audit_file` to find unauthorized queries.

**Step 3 — Root cause**: No secret scanning in CI pipeline. No managed identity for service-to-database authentication.

**Step 4 — Fix**: Use Azure Managed Identity instead of connection strings. Add secret scanning (CredScan) to CI/CD pipeline. Enable Azure Defender for SQL.

#### Solution
```csharp
// Before: connection string in appsettings.json
// "ConnectionStrings:OrderDb": "Server=order-svr.database.windows.net;Database=orderdb;User Id=app;Password=***"

// After: Managed Identity authentication
await using var connection = new SqlConnection();
connection.ConnectionString = "Server=order-svr.database.windows.net;Database=orderdb;Authentication=Active Directory Managed Identity;";
```

## System Design for Reliability

### Design Question 1: Design a Multi-Region Active-Active Architecture on Azure
Design a global e-commerce platform using Azure Front Door, Cosmos DB multi-master, and AKS across regions. Discuss conflict resolution, data partitioning, failover strategy.

**Key points**: Front Door for global load balancing + WAF. Cosmos DB multi-master with last-writer-wins conflict resolution. AKS clusters per region with pod anti-affinity. Traffic Manager for DNS-level failback.

### Design Question 2: Design a CI/CD Pipeline for AKS
Design a secure, reliable deployment pipeline using Azure DevOps, ACR, and AKS. Discuss canary deployments, blue-green, progressive delivery, automated rollback.

**Key points**: Azure DevOps with release gates. ACR with geo-replication. Helm + Flux for GitOps. Canary via Istio traffic splitting. Rollback triggers via Azure Monitor metrics.

### Design Question 3: Design an Observability Platform with Azure Monitor
Design a centralized monitoring solution using Azure Monitor, Log Analytics, and Application Insights. Discuss sampling strategies, retention policies, and cost management.

## Incident Command Behavioral

### Question 1: Describe a time you dealt with a customer-impacting incident. (Customer Obsession)
**STAR**: During the AKS CrashLoop incident (Lab 11), I immediately declared a Sev-1 incident, communicated ETA via Teams, identified the ConfigMap issue within 5 minutes, and restored service in 10 minutes. Post-incident, I automated ConfigMap validation.

### Question 2: Tell me about a time you learned a new technology. (Growth Mindset)
**STAR**: I didn't know Azure Service Bus dead-lettering well. During the consumer lag incident (Lab 13), I studied the documentation, implemented a DLQ, and documented the pattern for the team.

### Question 3: How do you handle ambiguous problems? (Deal with Ambiguity)
**STAR**: When high CPU on the App Service (Lab 03) had no obvious root cause, I enabled Application Insights Profiler, analyzed GC logs, and identified a memory allocation storm.

### Question 4: Describe a time you influenced without authority. (Influence Without Authority)
**STAR**: I identified Azure SQL was at 85% capacity (Lab 04). I built a dashboard showing the trend, presented it to the team, and got buy-in to implement connection pooling improvements.

### Question 5: Tell me about a time you used data to make a decision. (Data-Driven)
**STAR**: When deciding whether to increase App Service plan or optimize code (Lab 03), I collected allocation rate data, profiler traces, and cost analysis. Code optimization would save $40k/year.

### Question 6: Describe a time you improved security. (Security)
**STAR**: After the security breach incident (Lab 10), I implemented Managed Identity across all Azure services, removing connection strings from 50 application configuration files.

### Question 7: How do you ensure a service meets its reliability targets?
**STAR**: For the order processing pipeline (Lab 13), I defined SLOs: 99.9% availability, < 500ms p99 latency, < 1000 messages backlog. I set up Azure Monitor alerts on burn rate, and created a weekly reliability review dashboard.

### Question 8: Describe a time you had to make a difficult technical decision.
**STAR**: During the GC storm incident (Lab 03), I had to decide between increasing the App Service plan ($40k/year) or rewriting the document processing code (2 weeks). I chose the rewrite after profiling showed the code was the root cause.

### Question 9: How do you handle a situation where a service you own fails?
**STAR**: When the AKS Cluster CrashLoop occurred (Lab 11), I owned the incident end-to-end: declared Sev-1, restored service via rollback, led the post-mortem, and implemented ConfigMap immutability to prevent recurrence.

### Question 10: Tell me about a time you mentored someone on Azure best practices.
**STAR**: A team was using connection strings for all Azure services. I mentored them on Managed Identity, Key Vault references, and Azure AD authentication. We eliminated 200 connection strings across 20 applications.

### Question 11: Describe a time you automated yourself out of on-call pages.
**STAR**: After getting paged 5 times in one night for Azure SQL connection exhaustion (Lab 04), I implemented auto-scaling for connection pools via Azure Automation, added connection leak detection, and created a self-healing runbook. Pages dropped from 5/night to 0.

### Question 12: How do you handle the "blameless" aspect of Microsoft's SRE culture?
**STAR**: During the AKS CrashLoop post-mortem (Lab 11), the engineer who deployed the bad ConfigMap was afraid of repercussions. I explicitly stated "this is a system gap, not a people gap" and showed how the system allowed the mistake. We then added pre-deployment validation.

### Question 13: Tell me about a time you used KQL to solve a production problem.
**STAR**: During the Service Bus consumer lag (Lab 13), I wrote a KQL query that correlated function app failures with database size, identified the root cause (SQL size quota), and created a monitoring dashboard that tracks the entire data pipeline.

### Question 14: How do you approach capacity planning for Azure services?
**STAR**: For Azure SQL, I monitor DTU consumption trends (90-day lookback), set alerts at 80% utilization, and use predictive auto-scaling. For AKS, I monitor node pool utilization and set cluster auto-scaler with 20% headroom.

### Question 15: Describe a time you migrated a service to Azure.
**STAR**: I migrated an on-premises .NET application to AKS. I containerized the app, set up Azure DevOps pipelines, configured Azure Monitor, and implemented a blue-green deployment strategy. The migration had zero downtime and 3x cost savings.

### Question 16: How do you handle a situation where Azure services don't meet your reliability requirements?
**STAR**: When Azure SQL's max DTU couldn't handle our traffic spikes, I didn't just request more DTU. I analyzed the workload and implemented read replicas for reporting queries, caching with Redis for hot data, and connection pooling optimization. This reduced DTU consumption by 60% and stayed within our budget.

### Question 17: Describe your experience with Azure Policy and governance.
**STAR**: After the security breach (Lab 10), I implemented Azure Policy that: (1) prevents creation of public-facing App Services without WAF, (2) requires diagnostic settings on all Azure SQL databases, (3) enforces TLS 1.2+ on all front door endpoints. We achieved 100% compliance within 2 weeks.

### Question 18: How do you handle a situation where a team doesn't want to follow reliability best practices?
**STAR**: A team didn't want to add health probes to their AKS pods because "it works fine without them." I showed them the CrashLoop incident (Lab 11) — without proper liveness/readiness probes, unhealthy pods stay in the service pool, causing 50% error rate. They added probes within a week.

### Question 19: Tell me about a time you worked with Azure Support to resolve an incident.
**STAR**: During a Cosmos DB throttling incident, Azure Support helped us analyze the 429 responses. We identified a hot partition caused by a single customer's high write volume. We repartitioned the collection with a composite partition key and the throttling stopped.

### Question 20: How do you approach cost optimization for Azure resources?
**STAR**: I use Azure Advisor recommendations, reserved instances for baseline capacity, and spot instances for batch processing. After implementing these for our AKS cluster, we reduced infrastructure costs by 35% while maintaining the same reliability SLO.

### Azure Governance and Compliance Context

Azure SRE interviews often test your understanding of enterprise governance:

**Azure Policy vs RBAC**: Policy enforces resource configuration rules (e.g., "all SQL databases must have auditing enabled"). RBAC controls who can do what. Both are needed for a secure environment.

**Management Groups**: Hierarchical structure for applying policies across subscriptions. Example: a "Production" management group with policies that block public network access.

**Azure Blueprints**: Pre-defined sets of Azure resources and policies. Used for: compliance (HIPAA, PCI-DSS), standard environments (dev/test/prod), and landing zones.

**Resource Locks**: Prevents accidental deletion or modification of critical resources. Apply `CanNotDelete` lock to production databases, key vaults, and networking resources.

**Azure Policy for Reliability**: Enforce: (1) autoscaling enabled on App Service plans, (2) geo-redundant storage for critical data, (3) backup configured on all SQL databases, (4) TLS 1.2 minimum on all endpoints.

## Study Plan

### Priority Labs for Microsoft Azure SRE
1. **Lab 11 (Kubernetes CrashLoop)** — AKS debugging is critical
2. **Lab 04 (Connection Pool)** — Azure SQL reliability
3. **Lab 12 (TLS Expiry)** — Azure networking/cert management
4. **Lab 03 (High CPU)** — .NET performance debugging
5. **Lab 13 (Service Bus Lag)** — Messaging infrastructure
6. **Lab 10 (Security Breach)** — Managed Identity and security
7. **Lab 06 (Deployment Rollback)** — CI/CD pipeline safety

### Recommended Schedule
- **Week 1**: Labs 11, 04 (AKS + Azure SQL)
- **Week 2**: Labs 12, 06 (networking + deployment)
- **Week 3**: Labs 03, 13, 10 (performance + messaging + security)
- **Week 4**: System design + KQL practice + behavioral
- **Week 5**: Mock interviews with Azure CLI practice

### Lab Practice Checklist
- For each lab: (1) deploy the scenario to a sandbox AKS cluster, (2) write a KQL query that would detect the issue, (3) script the remediation with Azure CLI, (4) create a Bicep template that prevents the issue, (5) write a post-mortem

## Tips

### Microsoft Azure SRE Interview Strategies
1. **KQL is your friend**: Practice Kusto Query Language for Azure Monitor. Many scenarios involve writing KQL queries.
2. **Know Azure services deeply**: AKS, Cosmos DB, Azure SQL, Service Bus, Front Door, Traffic Manager, App Service, Functions, Key Vault.
3. **ARM/Bicep fluency**: Be able to write infrastructure-as-code. Azure interviewers love Bicep/ARM template discussions.
4. **Live Site culture**: Every answer should reference customer impact. Microsoft is customer-obsessed.
5. **Growth mindset language**: Use "I learned", "I shared the post-mortem broadly", "I improved the process".
6. **PowerShell/Azure CLI proficiency**: Automation questions often involve scripting remediation steps.
7. **Understand the Well-Architected Framework**: Reliability, security, cost optimization, operational excellence, performance efficiency.
8. **Practice incident command**: Microsoft uses the Incident Commander model. Know how to triage, mitigate, resolve, and post-mortem.
9. **Managed Identity over secrets**: Show you know Azure AD authentication is preferred over connection strings and keys.
10. **GitOps mindset**: Know Flux, Helm, and Azure DevOps integration for declarative Kubernetes management.
11. **Practice KQL daily**: KQL is the #1 skill gap for Azure SRE candidates. Write queries for: pod crash loops, SQL slow queries, function app errors, Service Bus queue depth, and container restart counts.
12. **Bicep fluency**: Azure is moving from ARM to Bicep. Know how to write Bicep templates for common resources (AKS, Cosmos DB, Azure SQL, App Service).
