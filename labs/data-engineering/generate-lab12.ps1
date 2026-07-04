$base = "C:\Users\jratombo-adm\Desktop\java_learning_lab\labs\data-engineering\12-data-governance"

$files = @{}

$files["README.md"] = @"
# Data Governance

## Overview
Data governance encompasses the policies, processes, and technologies that ensure data is managed properly across an organization - including data cataloging, lineage tracking, metadata management, access control, and compliance.

## Key Concepts
- **Data Catalog**: Centralized inventory of data assets
- **Data Lineage**: Tracking data from source to consumption
- **Metadata Management**: Technical, business, and operational metadata
- **Access Control**: Who can access what data
- **Compliance**: Meeting regulatory requirements (GDPR, CCPA, HIPAA)

## Java/Spring Example
```java
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/governance")
public class GovernanceController {

    @GetMapping("/catalog/search")
    public ResponseEntity<List<DataAsset>> searchCatalog(@RequestParam String query) {
        // Search data catalog for assets matching query
        return ResponseEntity.ok(catalogService.search(query));
    }

    @GetMapping("/lineage/{assetId}")
    public ResponseEntity<LineageGraph> getLineage(@PathVariable String assetId) {
        // Get full lineage for a data asset
        return ResponseEntity.ok(lineageService.getLineage(assetId));
    }

    @PostMapping("/policies/evaluate")
    public ResponseEntity<AccessDecision> evaluatePolicy(
            @RequestBody AccessRequest request) {
        // Evaluate access control policies
        boolean allowed = policyEngine.evaluate(request);
        return ResponseEntity.ok(new AccessDecision(allowed, request.getReason()));
    }

    @GetMapping("/compliance/report/{regulation}")
    public ResponseEntity<ComplianceReport> getComplianceReport(
            @PathVariable String regulation) {
        // Generate compliance report for specific regulation
        return ResponseEntity.ok(complianceService.generateReport(regulation));
    }
}
```
"@

$files["THEORY.md"] = @"
# Data Governance Theory

## Pillars of Data Governance
1. **Data Quality**: Accuracy, completeness, consistency
2. **Data Catalog**: Discovery and inventory
3. **Data Lineage**: Origin, transformation, and movement
4. **Data Security**: Access control, encryption, masking
5. **Data Privacy**: PII handling, consent management
6. **Compliance**: Regulatory adherence (GDPR, CCPA, HIPAA, SOX)
7. **Data Lifecycle**: Retention, archiving, deletion

## Metadata Types
- **Technical Metadata**: Schema, data types, partitions, file formats
- **Business Metadata**: Business definitions, KPIs, owners, tags
- **Operational Metadata**: Run times, error rates, freshness, quality scores

## Governance Maturity Model
1. **Initial**: Ad-hoc, no formal governance
2. **Repeatable**: Basic policies, manual processes
3. **Defined**: Documented policies, some automation
4. **Managed**: Automated enforcement, metrics tracking
5. **Optimized**: Continuous improvement, AI-driven
"@

$files["WHY_IT_EXISTS.md"] = @"
# Why Data Governance Exists

## The Problem
Without governance, organizations face: data swamps (can't find data), security breaches (uncontrolled access), compliance violations (GDPR fines up to 4% of revenue), and inconsistent metrics (multiple versions of truth).

## Root Cause
- Data grows faster than governance processes
- Teams create data in silos without standards
- Regulatory requirements are increasingly strict
- Data lineage is lost as pipelines grow complex

## Governance Solution
- **Catalog**: Make data discoverable
- **Lineage**: Understand data provenance
- **Policies**: Control data access and usage
- **Quality**: Ensure data trustworthiness
- **Compliance**: Meet regulatory requirements
"@

$files["WHY_IT_MATTERS.md"] = @"
# Why Data Governance Matters

## Business Impact
- **Trust**: Stakeholders trust governed data
- **Compliance**: Avoid regulatory fines
- **Efficiency**: Find data 10x faster
- **Risk**: Reduce data breach exposure

## Cost of Poor Governance
| Issue | Average Cost |
|-------|-------------|
| GDPR non-compliance | 4% global revenue or 20M EUR |
| Data breach | $4.45M per incident |
| Data swamps | $10M+ wasted engineering time |
| Bad decisions from bad data | $12.9M annually |

## ROI of Governance
- 70% reduction in data finding time
- 60% fewer compliance incidents
- 40% faster data onboarding
- 3x data engineer productivity
"@

$files["HISTORY.md"] = @"
# History of Data Governance

## Timeline
- **1990s**: Basic data dictionary concepts
- **2002**: Sarbanes-Oxley Act (financial data governance)
- **2004**: First data governance tools (Informatica, Collibra)
- **2012**: Apache Atlas open-sourced
- **2016**: GDPR enacted (effective 2018)
- **2018**: CCPA enacted (effective 2020)
- **2020**: Data mesh popularizes domain ownership
- **2022**: OpenLineage standard for lineage
- **2023**: AI-driven data governance (automated classification)

## Key Milestones
1. 2002: SOX creates formal data governance requirements
2. 2016: GDPR introduces massive privacy compliance
3. 2018: CCPA follows California privacy regulation
4. 2020: Data mesh shifts governance to domains
5. 2022: OpenLineage provides lineage interoperability
"@

$files["MENTAL_MODELS.md"] = @"
# Mental Models for Data Governance

## 1. The City Government
- **Data Catalog** = City map (find anything)
- **Data Lineage** = Building permits (track changes)
- **Access Control** = Building keys (who can enter)
- **Compliance** = Building codes (regulations)
- **Data Quality** = Building inspections

## 2. The Library of Congress
- **Catalog** = Card catalog (find books)
- **Metadata** = ISBN, author, subject (describe books)
- **Lineage** = Publishing history (where books came from)
- **Access** = Reading room access (who can read)
- **Retention** = Archives (how long kept)

## 3. The Ship's Log
- **Data Sources** = Ports of call
- **Transformations** = Course changes
- **Lineage** = Ship's log (full history)
- **Quality** = Seaworthiness inspections
- **Compliance** = Maritime regulations
"@

$files["HOW_IT_WORKS.md"] = @"
# How Data Governance Works

## Data Catalog Implementation
```java
@Component
public class DataCatalogService {

    private final CatalogRepository repository;
    private final MetadataExtractor extractor;

    @EventListener
    public void onTableCreated(TableCreatedEvent event) {
        // Automatically register new tables in catalog
        DataAsset asset = DataAsset.builder()
            .name(event.getTableName())
            .type(AssetType.TABLE)
            .schema(extractor.extractSchema(event))
            .owner(event.getOwner())
            .createdAt(Instant.now())
            .tags(extractTags(event))
            .build();

        repository.save(asset);
        log.info("Registered new asset: {}", asset.getName());
    }

    public List<DataAsset> search(String query, String domain, String owner) {
        Specification<DataAsset> spec = Specification.where(null);

        if (query != null) {
            spec = spec.and((root, q, cb) ->
                cb.like(cb.lower(root.get("name")), "%" + query.toLowerCase() + "%"));
        }
        if (domain != null) {
            spec = spec.and((root, q, cb) ->
                cb.equal(root.get("domain"), domain));
        }
        if (owner != null) {
            spec = spec.and((root, q, cb) ->
                cb.equal(root.get("owner"), owner));
        }
        return repository.findAll(spec);
    }
}
```

## Data Lineage Tracking
```java
@Component
public class LineageTracker {

    private final LineageRepository lineageRepo;

    @EventListener
    public void onPipelineExecution(PipelineExecutionEvent event) {
        // Record lineage edges
        for (InputSource input : event.getInputs()) {
            for (OutputTarget output : event.getOutputs()) {
                LineageEdge edge = LineageEdge.builder()
                    .sourceDataset(input.getDataset())
                    .sourceColumns(input.getColumns())
                    .targetDataset(output.getDataset())
                    .targetColumns(output.getColumns())
                    .transformation(event.getTransformation())
                    .executionId(event.getExecutionId())
                    .timestamp(event.getTimestamp())
                    .build();
                lineageRepo.save(edge);
            }
        }
    }

    public LineageGraph getLineage(String dataset, LineageDirection direction) {
        Set<LineageNode> nodes = new HashSet<>();
        Set<LineageEdge> edges = new HashSet<>();

        Queue<String> queue = new LinkedList<>();
        queue.add(dataset);

        while (!queue.isEmpty()) {
            String current = queue.poll();
            List<LineageEdge> connected = (direction == LineageDirection.UPSTREAM)
                ? lineageRepo.findByTargetDataset(current)
                : lineageRepo.findBySourceDataset(current);

            for (LineageEdge edge : connected) {
                nodes.add(new LineageNode(
                    direction == LineageDirection.UPSTREAM
                        ? edge.getSourceDataset() : edge.getTargetDataset()));
                edges.add(edge);

                queue.add(direction == LineageDirection.UPSTREAM
                    ? edge.getSourceDataset() : edge.getTargetDataset());
            }
        }
        return new LineageGraph(nodes, edges);
    }
}
```

## Policy Enforcement
```java
@Component
public class PolicyEngine {

    private final PolicyRepository policyRepo;
    private final UserContext userContext;

    public AccessDecision evaluate(AccessRequest request) {
        // 1. Get applicable policies
        List<Policy> policies = policyRepo.findByResourceAndActive(
            request.getResource(), true);

        // 2. Evaluate each policy
        for (Policy policy : policies) {
            if (!matchesCondition(policy, request)) continue;

            if (!policy.isAllow()) {
                return AccessDecision.deny(
                    "Denied by policy: " + policy.getName());
            }
        }

        // 3. Check data classification
        DataAsset asset = catalogService.getAsset(request.getResource());
        if (asset.getClassification() == Classification.PII &&
            !userContext.hasRole("PII_ACCESS")) {
            return AccessDecision.deny("PII access requires special role");
        }

        return AccessDecision.allow("Access granted");
    }

    private boolean matchesCondition(Policy policy, AccessRequest request) {
        // Evaluate policy conditions (user, role, time, location, etc.)
        return request.getUser().equals(policy.getUser()) ||
               request.getRole().equals(policy.getRole());
    }
}
```
"@

$files["INTERNALS.md"] = @"
# Data Governance Internals

## Metadata Storage
```java
@Entity
@Table(name = "data_assets")
public class DataAsset {
    @Id private String id;
    private String name;
    private String description;
    private String domain;
    private String owner;
    private String steward;
    @Enumerated(EnumType.STRING)
    private AssetType type;
    @Enumerated(EnumType.STRING)
    private Classification classification;
    private String schemaJson;
    private String tags;
    private Instant createdAt;
    private Instant updatedAt;
    private int qualityScore;
    private long recordCount;
    private long sizeBytes;
}

@Entity
@Table(name = "lineage_edges")
public class LineageEdge {
    @Id private String id;
    private String sourceDataset;
    private String targetDataset;
    private String sourceColumns;  // JSON array
    private String targetColumns;  // JSON array
    private String transformation;
    private String executionId;
    private Instant timestamp;
}

@Entity
@Table(name = "governance_policies")
public class Policy {
    @Id private String id;
    private String name;
    private String description;
    private String resource;  // Dataset, column, or pattern
    private String user;
    private String role;
    private boolean isAllow;
    private boolean active;
    private Instant effectiveFrom;
    private Instant effectiveTo;
    private String createdBy;
    private Instant createdAt;
}
```

## OpenLineage Integration
```java
// OpenLineage standard for emitting lineage
// library: io.openlineage:openlineage-java
OpenLineageClient client = OpenLineageClient.builder()
    .transport(transport)
    .build();

OpenLineage.RunEvent event = openLineage.newRunEventBuilder()
    .eventType(EventType.COMPLETE)
    .run(openLineage.newRun(runId))
    .job(openLineage.newJob("my-job", "spark"))
    .inputs(openLineage.newDataset("source-table", "postgresql"))
    .outputs(openLineage.newDataset("target-table", "delta-lake"))
    .build();

client.emit(event);
```
"@

$files["MATH_FOUNDATION.md"] = @"
# Math Foundation for Data Governance

## Compliance Risk
```
RiskScore = Sigma(Regulation * Likelihood * Impact)
NonComplianceCost = Probability * FineAmount + RemediationCost

GDPR Fine = max(10M EUR, 2% global revenue) or max(20M EUR, 4% global revenue)
```

## Data Classification Coverage
```
ClassifiedData = TaggedData + AutoDiscoveredData
DiscoveredRate = NewClassified / TotalNewData
ClassificationAccuracy = CorrectlyClassified / TotalClassified

Target: >95% discovery rate, >99% accuracy
```

## Catalog Coverage
```
CatalogCoverage = CatalogedAssets / TotalAssets * 100
LineageCoverage = TrackedAssets / CatalogedAssets * 100
PolicyCoverage = GovernedData / TotalData * 100

DataSwampRatio = 1 - CatalogCoverage
Target: CatalogCoverage > 90%
```

## Data Lifecycle Costs
```
StorageCost = Size * CostPerGB * RetentionDays
ArchiveCost = Size * ArchiveCostPerGB * ArchiveDays
DeletionSavings = Size * CostPerGB * EliminationDays

OptimalRetention = Cost(Storage) - Benefit(DataValue)
TotalDataFootprint = Sigma(AllDatasets) over time
```
"@

$files["VISUAL_GUIDE.md"] = @"
# Visual Guide to Data Governance

## Data Governance Framework
```
+-------------------------------------------------------+
|                    DATA GOVERNANCE                      |
+----------+---------+----------+---------+-------------+
| Catalog  | Lineage | Policies | Quality | Compliance  |
+----------+---------+----------+---------+-------------+
|          |         |          |         |             |
| Find     | Track   | Control  | Trust   | Regulations |
| data     | origin  | access   | metrics | adherence   |
| assets   | & usage |          |         |             |
+----------+---------+----------+---------+-------------+
```

## Lineage Visualization
```
Source Systems        ETL Pipeline         Data Warehouse
+----------+     +--------------+     +----------------+
| MySQL    |     |              |     | fact_orders    |
| orders   |---->| Spark Job    |---->| dim_customer   |
+----------+     | daily_prices |     | dim_product    |
                 |              |     +----------------+
+----------+     +--------------+            |
| CRM API  |          |                     v
| customers|----------+              +----------------+
+----------+     +--------------+     | BI Dashboard  |
                 | Data Quality |     | Revenue KPIs  |
+----------+     | Validation   |---->| Customer 360  |
| CSV Files |     +--------------+     +----------------+
| products  |----------+
+----------+
```

## Compliance Data Flow
```
+------------------+     +------------------+     +------------------+
| PII Detection    |     | Access Control   |     | Audit Log        |
| - Regex patterns |---->| - Role-based     |---->| - All access     |
| - ML classifier  |     | - Column masking |     |   recorded       |
| - Manual tags    |     | - Encryption     |     | - Retention      |
+------------------+     +------------------+     +------------------+
        |                        |                        |
        v                        v                        v
+-----------------------------------------------------------+
|                  Compliance Report                          |
| GDPR: All PII tagged, access logged, right to delete       |
| CCPA: Consumer data mapped, opt-out mechanism in place    |
| SOX: Financial data lineage complete, access audited      |
+-----------------------------------------------------------+
```
"@

$files["CODE_DEEP_DIVE.md"] = @"
# Code Deep Dive: Data Governance Platform

## Complete Governance Platform
```java
@SpringBootApplication
@EnableJpaAuditing
@EnableScheduling
public class GovernanceApplication {
    public static void main(String[] args) {
        SpringApplication.run(GovernanceApplication.class, args);
    }

    @Bean
    public MetadataExtractor metadataExtractor(SparkSession spark) {
        return new SparkMetadataExtractor(spark);
    }

    @Bean
    public OpenLineageClient openLineageClient() {
        return OpenLineageClient.builder()
            .transport(new HttpTransport("http://marquez:5000"))
            .build();
    }
}

// REST API for governance operations
@RestController
@RequestMapping("/api/v1/governance")
public class GovernanceApi {

    private final CatalogService catalog;
    private final LineageService lineage;
    private final PolicyService policies;
    private final ComplianceService compliance;

    // Data Catalog Endpoints
    @GetMapping("/catalog")
    public ResponseEntity<Page<DataAsset>> searchCatalog(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String domain,
            @RequestParam(required = false) String owner,
            Pageable pageable) {
        return ResponseEntity.ok(catalog.search(q, domain, owner, pageable));
    }

    @GetMapping("/catalog/{id}")
    public ResponseEntity<DataAsset> getAsset(@PathVariable String id) {
        return ResponseEntity.ok(catalog.getAsset(id));
    }

    @PostMapping("/catalog")
    public ResponseEntity<DataAsset> registerAsset(@RequestBody DataAsset asset) {
        return ResponseEntity.ok(catalog.register(asset));
    }

    @PostMapping("/catalog/bulk")
    public ResponseEntity<List<DataAsset>> bulkRegister(
            @RequestBody List<DataAsset> assets) {
        return ResponseEntity.ok(catalog.bulkRegister(assets));
    }

    // Lineage Endpoints
    @GetMapping("/lineage/{assetId}")
    public ResponseEntity<LineageGraph> getLineage(
            @PathVariable String assetId,
            @RequestParam(defaultValue = "UPSTREAM") LineageDirection direction,
            @RequestParam(defaultValue = "3") int depth) {
        return ResponseEntity.ok(lineage.getLineage(assetId, direction, depth));
    }

    @GetMapping("/lineage/impact/{assetId}")
    public ResponseEntity<List<ImpactAnalysis>> impactAnalysis(
            @PathVariable String assetId) {
        return ResponseEntity.ok(lineage.analyzeImpact(assetId));
    }

    // Policy Endpoints
    @GetMapping("/policies")
    public ResponseEntity<List<Policy>> getPolicies(
            @RequestParam(required = false) String resource) {
        return ResponseEntity.ok(policies.findByResource(resource));
    }

    @PostMapping("/policies/evaluate")
    public ResponseEntity<AccessDecision> evaluatePolicy(
            @RequestBody AccessRequest request) {
        return ResponseEntity.ok(policies.evaluate(request));
    }

    @PostMapping("/policies")
    public ResponseEntity<Policy> createPolicy(@RequestBody Policy policy) {
        return ResponseEntity.ok(policies.create(policy));
    }

    // Compliance Endpoints
    @GetMapping("/compliance/{regulation}")
    public ResponseEntity<ComplianceReport> complianceReport(
            @PathVariable String regulation) {
        return ResponseEntity.ok(compliance.generateReport(regulation));
    }

    @PostMapping("/compliance/gdpr/delete-request")
    public ResponseEntity<Void> handleGdprDeleteRequest(
            @RequestBody GdprRequest request) {
        compliance.handleDeleteRequest(request);
        return ResponseEntity.accepted().build();
    }

    // Quality Endpoints
    @GetMapping("/quality/{datasetId}")
    public ResponseEntity<QualityReport> getQuality(
            @PathVariable String datasetId) {
        return ResponseEntity.ok(catalog.getQualityReport(datasetId));
    }

    @GetMapping("/metrics")
    public ResponseEntity<GovernanceMetrics> getMetrics() {
        return ResponseEntity.ok(catalog.getGovernanceMetrics());
    }
}

// Scheduled governance tasks
@Component
public class GovernanceScheduler {

    @Scheduled(cron = "0 0 2 * * ?") // Daily at 2 AM
    public void runAutoDiscovery() {
        // Discover new data assets
        List<DataAsset> discovered = metadataExtractor.discoverNewAssets();
        catalog.bulkRegister(discovered);
    }

    @Scheduled(cron = "0 0 3 * * ?") // Daily at 3 AM
    public void runPiiDetection() {
        // Scan for unclassified PII
        List<PiiFinding> findings = piiDetector.scanAllAssets();
        for (PiiFinding finding : findings) {
            catalog.updateClassification(
                finding.getAssetId(), Classification.PII);
        }
    }

    @Scheduled(cron = "0 0 4 * * ?") // Daily at 4 AM
    public void generateComplianceReports() {
        for (Regulation reg : Regulation.values()) {
            ComplianceReport report = compliance.generateReport(reg);
            reportRepository.save(report);
        }
    }
}
```
"@

$files["STEP_BY_STEP.md"] = @"
# Step-by-Step Data Governance Setup

## Step 1: Define Data Domains
```yaml
# domains.yaml
domains:
  - name: Sales
    owner: sales-team@company.com
    steward: data-steward@company.com
    description: Sales and revenue data
  - name: Marketing
    owner: marketing-team@company.com
    description: Campaign and customer data
  - name: Finance
    owner: finance-team@company.com
    steward: finance-da@company.com
    description: Financial data (SOX governed)
```

## Step 2: Set Up Data Catalog
```java
@Bean
public DataCatalog catalog() {
    return new ApacheAtlasCatalog("http://atlas:21000");
    // or: return new AmundsenCatalog("neo4j://amundsen:7687");
    // or: return new DataHubCatalog("http://datahub:8080");
}
```

## Step 3: Configure Lineage
```properties
# lineage.properties
lineage.openlineage.url=http://marquez:5000
lineage.spark.listener.enabled=true
lineage.auto.capture=true
lineage.retention.days=90
```

## Step 4: Define Policies
```java
@PostConstruct
public void initDefaultPolicies() {
    policyService.create(Policy.builder()
        .name("PII Access Restriction")
        .resource("*.pii.*")
        .role("PII_ACCESS")
        .isAllow(true)
        .description("Only users with PII_ACCESS role can view PII data")
        .active(true)
        .build());

    policyService.create(Policy.builder()
        .name("Finance Data Audit")
        .resource("finance.*")
        .condition("data_classification == 'SOX'")
        .isAllow(true)
        .description("All access to finance data is audited")
        .active(true)
        .build());
}
```

## Step 5: Compliance Setup
```java
@Bean
public PiiDetector piiDetector() {
    return new PiiDetector(Arrays.asList(
        new RegexPiiRule("EMAIL", "[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}"),
        new RegexPiiRule("SSN", "\\d{3}-\\d{2}-\\d{4}"),
        new RegexPiiRule("CREDIT_CARD", "\\d{4}-\\d{4}-\\d{4}-\\d{4}"),
        new MLPiiClassifier("pii-model.bin")
    ));
}
```

## Step 6: Setup Monitoring
```java
@Bean
public GovernanceDashboard dashboard() {
    return new GovernanceDashboard(metricRegistry);
}

// Metrics to track:
// governance.catalog.coverage
// governance.lineage.coverage
// governance.policy.enforcement.count
// governance.compliance.score
// governance.pii.discovery.rate
// governance.quality.score
```
"@

$files["COMMON_MISTAKES.md"] = @"
# Common Data Governance Mistakes

## 1. Governance as a One-Time Project
```java
// WRONG - set up catalog once, never update
catalogService.importAllAssets();

// RIGHT - continuous discovery and updates
@Scheduled(cron = "0 0 2 * * ?")
public void continuousDiscovery() {
    catalogService.discoverAndUpdate();
}
```

## 2. Too Restrictive
```java
// WRONG - block everything
policyService.create(Policy.denyAll());

// RIGHT - enable with guardrails
policyService.create(Policy.builder()
    .effect(Effect.ALLOW)
    .condition("role != 'ANALYST' OR classification != 'PII'")
    .build());
```

## 3. Ignoring Business Metadata
```java
// WRONG - only technical metadata
// Table: fact_orders (name, type, size)

// RIGHT - include business context
// Table: fact_orders
// Business Name: Daily Order Summary
// Description: Aggregated order metrics by day
// Domain: Sales
// Owner: John Smith
// Steward: Jane Doe
```

## 4. No Automated Enforcement
```java
// WRONG - manual review process
// "Please review this data access request"

// RIGHT - automated policy evaluation
AccessDecision decision = policyEngine.evaluate(request);
// decision.isAllowed() returns immediately
```
"@

$files["DEBUGGING.md"] = @"
# Debugging Data Governance

## Common Issues

### Missing Catalog Entries
```java
// Check if auto-discovery is running
@Scheduled(cron = "0 0 2 * * ?")
public void autoDiscovery() {
    List<DataAsset> assets = metadataExtractor.discover();
    log.info("Discovered {} new assets", assets.size());
}
```

### Broken Lineage
```java
// Verify Spark lineage listener
spark.conf().set("spark.extraListeners",
    "io.openlineage.spark.agent.OpenLineageSparkListener");

// Check lineage database
lineageRepo.findAll().forEach(edge -> {
    log.info("{} -> {} via {}", edge.getSource(),
        edge.getTarget(), edge.getTransformation());
});
```

### Policy Not Working
```java
// Test policy evaluation
AccessDecision result = policyEngine.evaluate(
    AccessRequest.builder()
        .user("test_user")
        .resource("finance.revenue")
        .action(Action.READ)
        .build());
log.info("Policy result: {}", result);
```

### Compliance Issues
```java
// Find untagged PII
piiDetector.scanUnclassified()
    .forEach(finding -> log.warn("Found PII in {}: {}",
        finding.getAssetId(), finding.getColumn()));
```
"@

$files["REFACTORING.md"] = @"
# Refactoring Data Governance

## Before: No Governance
```java
// Anyone can access any data
spark.sql("SELECT * FROM customers").show();
```

## After: Policy Enforced
```java
// Access control at the catalog level
spark.sql("SELECT * FROM governed.customers").show();
// Automatically filtered by policies
```

## Before: Manual Documentation
```sql
-- README.txt: "This table contains orders..."
-- (out of date within days)
```

## After: Automated Catalog
```java
// Schema, description, lineage auto-captured
catalog.getAsset("fact_orders");
// Returns: schema, owner, domain, lineage, quality score
```

## Before: No Lineage
```
// "Where does this data come from?"
// 2-hour investigation
```

## After: Automated Lineage
```
lineageService.getLineage("fact_orders");
// Returns: CRM -> ETL -> fact_orders -> BI Dashboard
```
"@

$files["PERFORMANCE.md"] = @"
# Data Governance Performance

## Catalog Optimization
```java
// Index catalog for fast search
@Entity
@Table(name = "data_assets", indexes = {
    @Index(name = "idx_name", columnList = "name"),
    @Index(name = "idx_domain", columnList = "domain"),
    @Index(name = "idx_owner", columnList = "owner"),
    @Index(name = "idx_classification", columnList = "classification")
})
public class DataAsset { ... }
```

## Lineage Query Performance
```java
// Cache lineage graphs for popular assets
@Cacheable(value = "lineage", key = "#assetId + ':' + #direction")
public LineageGraph getLineage(String assetId, LineageDirection direction) {
    return computeLineage(assetId, direction);
}

// Batch lineage updates
@EventListener
public void onBatchComplete(BatchExecutionEvent event) {
    List<LineageEdge> edges = event.getEdges();
    lineageRepo.saveAll(edges); // Batch insert
}
```

## Policy Evaluation Performance
```java
// Cache policy decisions
@Cacheable(value = "policies", key = "#request.cacheKey()")
public AccessDecision evaluate(AccessRequest request) {
    return evaluatePolicies(request);
}

// Policy evaluation with decision trees
// Instead of O(n) policy iteration, use indexed policy matching
```

## Metadata Storage
```java
// Use read-optimized storage
// Technical metadata: Hive Metastore / AWS Glue
// Business metadata: Neo4j / Elasticsearch
// Operational metadata: TimescaleDB / InfluxDB
```
"@

$files["SECURITY.md"] = @"
# Data Governance Security

## Authentication & Authorization
```java
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) {
        http.authorizeRequests()
            .antMatchers("/api/v1/governance/policies/**")
            .hasRole("GOVERNANCE_ADMIN")
            .antMatchers("/api/v1/governance/compliance/**")
            .hasRole("COMPLIANCE_OFFICER")
            .antMatchers("/api/v1/governance/catalog/**")
            .hasAnyRole("DATA_ENGINEER", "DATA_ANALYST")
            .anyRequest().authenticated()
            .and().oauth2ResourceServer().jwt();
        return http.build();
    }
}
```

## Data Masking
```java
// Dynamic data masking based on classification
@Aspect
@Component
public class DataMaskingAspect {
    @Around("@annotation(MaskedQuery)")
    public Object maskResults(ProceedingJoinPoint pjp) throws Throwable {
        Object result = pjp.proceed();
        if (result instanceof Dataset) {
            Dataset<Row> data = (Dataset<Row>) result;
            // Apply masking based on user's role
            if (!userContext.hasRole("PII_ACCESS")) {
                data = data.withColumn("email",
                    functions.regexp_replace(col("email"),
                        "(?<=.{3}).(?=.*@)", "*"));
                data = data.withColumn("phone",
                    functions.regexp_replace(col("phone"),
                        "\\d{3}", "***"));
            }
            return data;
        }
        return result;
    }
}
```

## Audit Logging
```java
@Aspect
@Component
public class AuditAspect {
    @Around("@annotation(Audited)")
    public Object audit(ProceedingJoinPoint pjp) throws Throwable {
        String user = SecurityContextHolder.getContext()
            .getAuthentication().getName();
        String method = pjp.getSignature().getName();
        Object[] args = pjp.getArgs();

        auditService.log(AuditEntry.builder()
            .user(user)
            .action(method)
            .resource(args.length > 0 ? args[0].toString() : null)
            .timestamp(Instant.now())
            .build());

        return pjp.proceed();
    }
}
```

## Encryption
```java
// Encrypt sensitive metadata
@Entity
public class DataAsset {
    @Convert(converter = EncryptionConverter.class)
    private String description; // Encrypted at rest

    @Convert(converter = EncryptionConverter.class)
    private String schemaJson; // Contains column descriptions
}
```
"@

$files["ARCHITECTURE.md"] = @"
# Data Governance Architecture

## Governance Platform Architecture
```
+----------------------------------------------------------+
|                   Governance Platform                      |
+------------+-------------+-------------+------------------+
| Data       | Data        | Policy      | Compliance       |
| Catalog    | Lineage     | Engine      | Engine           |
| - Search   | - Tracking  | - Evaluate  | - GDPR           |
| - Browse   | - Impact    | - Enforce   | - CCPA           |
| - Register | - Debug     | - Audit     | - SOX            |
+------------+-------------+-------------+------------------+
|            |             |             |                  |
+------------+-------------+-------------+------------------+
|              Metadata Storage Layer                        |
| PostgreSQL  | Neo4j       | Elasticsearch | Kafka         |
+----------------------------------------------------------+
|              Data Collection Layer                         |
| Spark Listeners | Kafka Connect | JDBC | REST APIs        |
+----------------------------------------------------------+
```

## Spring Boot Integration
```java
@SpringBootApplication
@EnableGovernance
public class GovernanceApplication {
    public static void main(String[] args) {
        SpringApplication.run(GovernanceApplication.class, args);
    }

    @Bean
    public DataPlatformMetadataExtractor metadataExtractor() {
        return new DataPlatformMetadataExtractor(
            Environment.getCatalogs(),
            Environment.getLineageListeners()
        );
    }
}
```

## Integration with Data Platforms
```yaml
# Integration with common tools
governance:
  catalog:
    type: datahub  # datahub, amundsen, atlas, collibra
    url: http://datahub:8080
  lineage:
    openlineage:
      enabled: true
      url: http://marquez:5000
  quality:
    great-expectations:
      datasource: spark
  policies:
    ranger:
      url: http://ranger:6080
  metadata:
    hive:
      metastore: thrift://hive-metastore:9083
```
"@

$files["EXERCISES.md"] = @"
# Data Governance Exercises

## Exercise 1: Data Catalog
Build a simple data catalog with search functionality and metadata management.

## Exercise 2: Lineage Tracking
Implement lineage capture for a Spark ETL pipeline using OpenLineage.

## Exercise 3: Policy Enforcement
Create a policy engine that evaluates access requests based on user role and data classification.

## Exercise 4: PII Detection
Build a classifier that automatically identifies PII columns in datasets.

## Exercise 5: Compliance Report
Generate a GDPR compliance report showing all PII data and access history.
"@

$files["QUIZ.md"] = @"
# Data Governance Quiz

## Question 1
What is the primary purpose of a data catalog?
- A) Store data
- B) Make data discoverable and searchable
- C) Transform data
- D) Run queries

## Question 2
What does data lineage track?
- A) Who accessed the data
- B) Data origin, transformations, and movement
- C) How old the data is
- D) Data size

## Question 3
Which regulation has fines up to 4% of global revenue?
- A) CCPA
- B) HIPAA
- C) GDPR
- D) SOX

## Question 4
What is the difference between a data owner and a data steward?
- A) They are the same
- B) Owner is accountable, steward manages daily
- C) Steward is accountable, owner manages
- D) Owner is technical, steward is business

## Answer Key
1: B, 2: B, 3: C, 4: B
"@

$files["FLASHCARDS.md"] = @"
# Data Governance Flashcards

## Card 1
**Front**: What is a data catalog?
**Back**: A centralized inventory of data assets with search, discovery, and metadata management.

## Card 2
**Front**: What is data lineage?
**Back**: Tracking the origin, transformations, and movement of data from source to consumption.

## Card 3
**Front**: What is GDPR?
**Back**: General Data Protection Regulation - EU regulation protecting personal data with requirements for consent, access, and deletion.

## Card 4
**Front**: What is a data domain?
**Back**: A logical grouping of data assets owned by a specific business team (e.g., Sales, Finance).

## Card 5
**Front**: What is data classification?
**Back**: Categorizing data by sensitivity (Public, Internal, Confidential, PII) for appropriate access control.

## Card 6
**Front**: What is OpenLineage?
**Back**: An open standard for collecting and sharing data lineage metadata across different tools.

## Card 7
**Front**: What is the difference between technical and business metadata?
**Back**: Technical metadata describes schema and structure. Business metadata describes meaning and context.
"@

$files["INTERVIEW.md"] = @"
# Data Governance Interview Questions

## Beginner
**Q**: What is data governance?
**A**: Data governance is the practice of managing data assets through policies, processes, and technologies to ensure data is accurate, available, secure, and compliant.

## Intermediate
**Q**: Explain data lineage and why it matters.
**A**: Data lineage tracks where data comes from, how it's transformed, and where it goes. It's critical for debugging, impact analysis, regulatory compliance, and building trust in data.

## Advanced
**Q**: How would you implement data governance in a data mesh architecture?
**A**: Each domain owns its data and governance. Provide shared tools (catalog, lineage, policy engine) as a platform. Domains implement domain-specific policies within their boundaries. Central team provides standards, tooling, and cross-domain governance.

## Senior
**Q**: Design a governance strategy for a company with 1000+ data assets across 20 domains.
**A**: Phase 1: Automated catalog discovery, PII detection, basic lineage. Phase 2: Domain ownership, quality metrics, policy engine. Phase 3: Automated compliance reporting, anomaly detection on governance metrics, self-service data access with policy enforcement.
"@

$files["REFLECTION.md"] = @"
# Data Governance Reflection

## Key Learnings
- Governance enables data trust and compliance
- Automation is essential for governance at scale
- Balance security with accessibility
- Governance is an ongoing process, not a project

## Questions to Explore
1. How does your organization balance data democratization with governance?
2. What's the right level of governance for your data maturity?
3. How do you measure the success of governance initiatives?
"@

$files["REFERENCES.md"] = @"
# Data Governance References

## Books
- "Data Governance: How to Design, Deploy and Sustain an Effective Data Governance Program" by John Ladley
- "Non-Invasive Data Governance" by Robert Seiner
- "The Data Catalog" by Daniel Kline and David Loshin

## Tools
- Apache Atlas: https://atlas.apache.org
- DataHub: https://datahubproject.io
- Amundsen: https://www.amundsen.io
- Apache Ranger: https://ranger.apache.org

## Standards
- OpenLineage: https://openlineage.io
- Data Catalog Vocabulary (DCAT): https://www.w3.org/TR/vocab-dcat-3/

## Regulations
- GDPR: https://gdpr-info.eu
- CCPA: https://oag.ca.gov/privacy/ccpa
- HIPAA: https://www.hhs.gov/hipaa
"@

foreach ($file in $files.Keys) {
    $content = $files[$file]
    $path = Join-Path $base $file
    $content | Out-File -FilePath $path -Encoding utf8
    Write-Host "Created: $path"
}
Write-Host "Lab 12 complete"
