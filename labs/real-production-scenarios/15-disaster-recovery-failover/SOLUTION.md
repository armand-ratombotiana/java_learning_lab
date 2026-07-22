# Solution: Disaster Recovery Failover Fix

## Step 1: Automated Failover with Route53 ARC

```bash
# Route53 Application Recovery Controller (ARC) setup
aws route53-recovery-control create-cluster --cluster-name acmecorp-dr-cluster

# Create control panel
aws route53-recovery-control create-control-panel \
  --cluster-arn arn:aws:route53-recovery-control::123456789:cluster/acmecorp-dr-cluster \
  --control-panel-name acmecorp-production

# Create routing control for failover
aws route53-recovery-control create-routing-control \
  --control-panel-arn <control-panel-arn> \
  --routing-control-name primary-us-east-1

# Create safety rule (prevent accidental failover)
aws route53-recovery-control create-safety-rule \
  --control-panel-arn <control-panel-arn> \
  --asserted-routing-controls <routing-control-arn> \
  --wait-period-ms 300000  # 5 minute wait
```

## Step 2: RDS Cross-Region Replication with Monitoring

```java
package com.acmecorp.infra.dr;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.Duration;
import java.time.Instant;
import java.util.Properties;

public class RDSReplicationLagMonitor {

    private static final long LAG_WARNING_SECONDS = 5;
    private static final long LAG_CRITICAL_SECONDS = 30;

    private final String primaryJdbcUrl;
    private final String replicaJdbcUrl;
    private final Properties connectionProps;

    public RDSReplicationLagMonitor(String primaryEndpoint,
                                    String replicaEndpoint,
                                    String username,
                                    String password) {
        this.primaryJdbcUrl = "jdbc:mysql://" + primaryEndpoint + ":3306";
        this.replicaJdbcUrl = "jdbc:mysql://" + replicaEndpoint + ":3306";
        this.connectionProps = new Properties();
        this.connectionProps.setProperty("user", username);
        this.connectionProps.setProperty("password", password);
        this.connectionProps.setProperty("connectTimeout", "5000");
        this.connectionProps.setProperty("socketTimeout", "10000");
    }

    public static class ReplicationLagResult {
        private final long lagSeconds;
        private final String replicaStatus;
        private final Instant checkTime;
        private final boolean healthy;

        public ReplicationLagResult(long lagSeconds, String replicaStatus) {
            this.lagSeconds = lagSeconds;
            this.replicaStatus = replicaStatus;
            this.checkTime = Instant.now();
            this.healthy = lagSeconds <= LAG_WARNING_SECONDS;
        }

        public long getLagSeconds() { return lagSeconds; }
        public String getReplicaStatus() { return replicaStatus; }
        public Instant getCheckTime() { return checkTime; }
        public boolean isHealthy() { return healthy; }

        @Override
        public String toString() {
            return String.format("Lag: %ds | Status: %s | Healthy: %b",
                lagSeconds, replicaStatus, healthy);
        }
    }

    public ReplicationLagResult checkReplicationLag() {
        try (Connection replicaConn = DriverManager.getConnection(
                replicaJdbcUrl, connectionProps);
             Statement stmt = replicaConn.createStatement()) {

            ResultSet rs = stmt.executeQuery("SHOW SLAVE STATUS");
            if (rs.next()) {
                long lagSeconds = rs.getLong("Seconds_Behind_Master");
                String ioRunning = rs.getString("Slave_IO_Running");
                String sqlRunning = rs.getString("Slave_SQL_Running");
                String status = String.format("IO: %s, SQL: %s",
                    ioRunning, sqlRunning);

                return new ReplicationLagResult(lagSeconds, status);
            }
            return new ReplicationLagResult(-1, "No replica status available");
        } catch (Exception e) {
            throw new RuntimeException(
                "Failed to check replication lag", e);
        }
    }

    public void monitorLoop(int intervalSeconds) {
        while (true) {
            try {
                ReplicationLagResult result = checkReplicationLag();
                System.out.println(Instant.now() + ": " + result);

                if (result.getLagSeconds() > LAG_CRITICAL_SECONDS) {
                    System.err.println("CRITICAL: Replication lag > " +
                        LAG_CRITICAL_SECONDS + "s: " + result.getLagSeconds() + "s");
                    // Send alert
                } else if (result.getLagSeconds() > LAG_WARNING_SECONDS) {
                    System.out.println("WARNING: Replication lag > " +
                        LAG_WARNING_SECONDS + "s: " + result.getLagSeconds() + "s");
                }

                Thread.sleep(intervalSeconds * 1000L);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                System.err.println("Error checking replication lag: " +
                    e.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        RDSReplicationLagMonitor monitor = new RDSReplicationLagMonitor(
            "primary.acmecorp.com",
            "replica.acmecorp.com",
            "monitor_user",
            System.getenv("DB_PASSWORD")
        );
        monitor.monitorLoop(10); // Check every 10 seconds
    }
}
```

## Step 3: CloudFormation DR Template with Drift Detection

```yaml
# dr-template.yaml
AWSTemplateFormatVersion: '2010-09-09'
Description: 'AcmeCorp DR Infrastructure — us-west-2'
Parameters:
  Environment:
    Type: String
    Default: dr
    AllowedValues: [dr, production]
  VpcCIDR:
    Type: String
    Default: 10.0.0.0/16
  DBInstanceClass:
    Type: String
    Default: db.r6g.large
  DBAllocatedStorage:
    Type: Number
    Default: 500
  WebServerAMI:
    Type: String
    Description: "Latest Amazon Linux 2 AMI"

Resources:
  # Database
  RDSInstance:
    Type: AWS::RDS::DBInstance
    Properties:
      DBInstanceClass: !Ref DBInstanceClass
      AllocatedStorage: !Ref DBAllocatedStorage
      Engine: MySQL
      EngineVersion: "8.0"
      MasterUsername: !Sub '{{resolve:ssm:/${Environment}/db/username}}'
      MasterUserPassword: !Sub '{{resolve:ssm:/${Environment}/db/password}}'
      MultiAZ: true
      StorageType: gp3
      BackupRetentionPeriod: 35
      DeletionProtection: true
      EnablePerformanceInsights: true
      PerformanceInsightsRetentionPeriod: 7

  # Application Load Balancer
  ApplicationLoadBalancer:
    Type: AWS::ElasticLoadBalancingV2::LoadBalancer
    Properties:
      Scheme: internet-facing
      Subnets: !Split [',', !ImportValue DRPublicSubnets]

  # ECS Cluster
  ECSCluster:
    Type: AWS::ECS::Cluster
    Properties:
      ClusterName: !Sub ${Environment}-cluster
      CapacityProviders: [FARGATE, FARGATE_SPOT]
      ClusterSettings:
        - Name: containerInsights
          Value: enabled

Outputs:
  RDSAddress:
    Value: !GetAtt RDSInstance.Endpoint.Address
    Export:
      Name: !Sub ${Environment}-rds-address
  ALBDNSName:
    Value: !GetAtt ApplicationLoadBalancer.DNSName
    Export:
      Name: !Sub ${Environment}-alb-dnsname
```

### Drift Detection Script

```bash
#!/bin/bash
# check-drift.sh — CloudFormation drift detection

STACK_NAME="acmecorp-production-dr"
REGION="us-west-2"

echo "=== CloudFormation Drift Detection ==="
date

# Check for existing drift detection
DRIFT_ID=$(aws cloudformation detect-stack-drift \
  --stack-name $STACK_NAME \
  --region $REGION \
  --query 'StackDriftDetectionId' \
  --output text)

echo "Drift detection ID: $DRIFT_ID"

# Wait for completion
sleep 10

# Get drift status
STATUS=$(aws cloudformation describe-stack-drift-detection-status \
  --stack-drift-detection-id $DRIFT_ID \
  --region $REGION \
  --query 'StackDriftStatus' \
  --output text)

echo "Drift status: $STATUS"

if [ "$STATUS" != "IN_SYNC" ]; then
  echo "WARNING: Stack drift detected! Details:"
  aws cloudformation describe-stack-resource-drifts \
    --stack-name $STACK_NAME \
    --region $REGION \
    --query 'StackResourceDrifts[?StackResourceDriftStatus!=`IN_SYNC`]'
  
  # Send alert
  curl -X POST -H "Content-type: application/json" \
    --data "{\"text\":\":warning: CloudFormation drift detected in $STACK_NAME — status: $STATUS\"}" \
    $SLACK_WEBHOOK
fi

echo "=== Drift Check Complete ==="
```

## Step 4: Multi-Region Health Check and Failover

```java
package com.acmecorp.infra.dr;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.*;

public class MultiRegionHealthChecker {

    private static final Duration CHECK_TIMEOUT = Duration.ofSeconds(10);
    private static final int FAILURE_THRESHOLD = 3;
    private static final Duration HEALTH_CHECK_INTERVAL = Duration.ofSeconds(30);

    private final HttpClient httpClient;
    private final String primaryHealthEndpoint;
    private final String drHealthEndpoint;
    private final ScheduledExecutorService scheduler;

    public MultiRegionHealthChecker(String primaryEndpoint, String drEndpoint) {
        this.httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();
        this.primaryHealthEndpoint = primaryEndpoint;
        this.drHealthEndpoint = drEndpoint;
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
    }

    public static class RegionHealth {
        private final String region;
        private final boolean healthy;
        private final long latencyMs;
        private final Instant checkTime;
        private final String errorMessage;

        public RegionHealth(String region, boolean healthy,
                           long latencyMs, String errorMessage) {
            this.region = region;
            this.healthy = healthy;
            this.latencyMs = latencyMs;
            this.checkTime = Instant.now();
            this.errorMessage = errorMessage;
        }

        public boolean isHealthy() { return healthy; }
        public String getRegion() { return region; }
        public long getLatencyMs() { return latencyMs; }
        public String getErrorMessage() { return errorMessage; }
    }

    public RegionHealth checkRegion(String endpoint, String regionName) {
        long startTime = System.currentTimeMillis();
        try {
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(endpoint + "/health"))
                .timeout(CHECK_TIMEOUT)
                .GET()
                .build();

            HttpResponse<String> response = httpClient.send(request,
                HttpResponse.BodyHandlers.ofString());

            long elapsed = System.currentTimeMillis() - startTime;
            boolean healthy = response.statusCode() == 200;

            return new RegionHealth(regionName, healthy, elapsed,
                healthy ? null : "HTTP " + response.statusCode());
        } catch (Exception e) {
            long elapsed = System.currentTimeMillis() - startTime;
            return new RegionHealth(regionName, false, elapsed,
                e.getMessage());
        }
    }

    public static class FailoverDecision {
        private final boolean shouldFailover;
        private final String reason;
        private final RegionHealth primaryHealth;
        private final RegionHealth drHealth;

        public FailoverDecision(boolean shouldFailover, String reason,
                               RegionHealth primary, RegionHealth dr) {
            this.shouldFailover = shouldFailover;
            this.reason = reason;
            this.primaryHealth = primary;
            this.drHealth = dr;
        }

        public boolean shouldFailover() { return shouldFailover; }
        public String getReason() { return reason; }
    }

    public FailoverDecision evaluateFailover() {
        RegionHealth primary = checkRegion(primaryHealthEndpoint, "us-east-1");
        RegionHealth dr = checkRegion(drHealthEndpoint, "us-west-2");

        if (!primary.isHealthy() && dr.isHealthy()) {
            return new FailoverDecision(true,
                "Primary region unhealthy, DR region healthy: " +
                "Primary error: " + primary.getErrorMessage(),
                primary, dr);
        } else if (!primary.isHealthy() && !dr.isHealthy()) {
            return new FailoverDecision(false,
                "Both regions unhealthy. Failover not possible.",
                primary, dr);
        }

        return new FailoverDecision(false, "Primary region healthy", primary, dr);
    }

    public void startContinuousMonitoring() {
        scheduler.scheduleAtFixedRate(() -> {
            FailoverDecision decision = evaluateFailover();
            System.out.println(Instant.now() + ": " +
                "Primary: " + decision.primaryHealth.isHealthy() +
                ", DR: " + decision.drHealth.isHealthy());

            if (decision.shouldFailover()) {
                System.out.println("ALERT: Failover recommended! " +
                    decision.getReason());
                // Trigger Route53 ARC failover
                triggerFailover();
            }
        }, 0, HEALTH_CHECK_INTERVAL.toSeconds(), TimeUnit.SECONDS);
    }

    private void triggerFailover() {
        // This would call Route53 ARC API to initiate failover
        System.out.println("Initiating automated failover to us-west-2...");
    }

    public static void main(String[] args) {
        MultiRegionHealthChecker checker = new MultiRegionHealthChecker(
            "https://api.us-east-1.acmecorp.com",
            "https://api.us-west-2.acmecorp.com"
        );
        checker.startContinuousMonitoring();
    }
}
```

## Step 5: DNS Configuration (Low TTL + Failover Routing)

```json
// dns-config.json — Route53 failover routing
{
  "Comment": "Production DNS with failover routing and low TTL",
  "Changes": [
    {
      "Action": "UPSERT",
      "ResourceRecordSet": {
        "Name": "api.acmecorp.com",
        "Type": "A",
        "SetIdentifier": "primary-us-east-1",
        "Region": "us-east-1",
        "Failover": "PRIMARY",
        "TTL": 60,
        "ResourceRecords": [
          { "Value": "203.0.113.10" }
        ],
        "HealthCheckId": "abcdef12-3456-7890-abcd-ef1234567890"
      }
    },
    {
      "Action": "UPSERT",
      "ResourceRecordSet": {
        "Name": "api.acmecorp.com",
        "Type": "A",
        "SetIdentifier": "secondary-us-west-2",
        "Region": "us-west-2",
        "Failover": "SECONDARY",
        "TTL": 60,
        "ResourceRecords": [
          { "Value": "198.51.100.10" }
        ]
      }
    }
  ]
}
```

## Step 6: Verification Commands

```bash
# Check Route53 failover routing
aws route53 list-resource-record-sets \
  --hosted-zone-id ZONEID \
  --query "ResourceRecordSets[?Name=='api.acmecorp.com.']"

# Verify RDS replication lag
mysql -h <replica-endpoint> -e "SHOW SLAVE STATUS\G" | grep Seconds_Behind_Master

# Test DNS propagation
dig api.acmecorp.com +short
nslookup api.acmecorp.com 8.8.8.8

# Check CloudFormation drift
aws cloudformation describe-stack-resource-drifts \
  --stack-name acmecorp-production

# Verify cross-region replication
aws rds describe-db-instances --region us-west-2 \
  --query "DBInstances[?ReadReplicaSourceDBInstanceIdentifier!=null].[DBInstanceIdentifier,ReadReplicaSourceDBInstanceIdentifier]"

# Check health check status
aws route53 list-health-checks --query "HealthChecks[?HealthCheckConfig.FullyQualifiedDomainName=='api.acmecorp.com']"

# Test DR environment
curl -s https://api.us-west-2.acmecorp.com/health
```
