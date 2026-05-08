package com.learning.multicloud;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;

public class Lab {

    static class CloudProvider {
        private final String name;
        private final Map<String, Object> resources = new ConcurrentHashMap<>();

        CloudProvider(String name) { this.name = name; }

        String provision(String type, String id) {
            resources.put(id, type);
            return name + ":" + type + ":" + id;
        }

        boolean hasResource(String id) { return resources.containsKey(id); }
        void removeResource(String id) { resources.remove(id); }
        int resourceCount() { return resources.size(); }
        public String toString() { return name + "(" + resourceCount() + " resources)"; }
    }

    static class MultiCloudOrchestrator {
        private final Map<String, CloudProvider> providers = new LinkedHashMap<>();

        MultiCloudOrchestrator addProvider(String name, CloudProvider provider) {
            providers.put(name, provider);
            return this;
        }

        String deployTo(String providerName, String type, String id) {
            var provider = providers.get(providerName);
            if (provider == null) throw new IllegalArgumentException("Unknown provider: " + providerName);
            return provider.provision(type, id);
        }

        void report() {
            System.out.println("  Multi-cloud inventory:");
            providers.forEach((name, provider) ->
                System.out.println("    " + provider));
        }
    }

    public static void main(String[] args) {
        System.out.println("=== Multi-Cloud Lab ===\n");

        cloudProviders();
        multiCloudStrategy();
        abstractionLayer();
        dataSovereignty();
        vendorLockIn();
    }

    static void cloudProviders() {
        System.out.println("--- Cloud Providers Overview ---");
        var orchestrator = new MultiCloudOrchestrator()
            .addProvider("AWS", new CloudProvider("AWS"))
            .addProvider("Azure", new CloudProvider("Azure"))
            .addProvider("GCP", new CloudProvider("GCP"));

        orchestrator.deployTo("AWS", "EC2", "i-1234");
        orchestrator.deployTo("AWS", "S3", "my-bucket");
        orchestrator.deployTo("Azure", "VM", "vm-5678");
        orchestrator.deployTo("GCP", "GCE", "instance-9012");

        orchestrator.report();

        System.out.println("""
  Market share: AWS (~32%), Azure (~23%), GCP (~11%)
  Others: Oracle Cloud, IBM Cloud, Alibaba Cloud, DigitalOcean
  Each has strengths: AWS breadth, Azure enterprise, GCP data/AI
    """);
    }

    static void multiCloudStrategy() {
        System.out.println("\n--- Multi-Cloud Strategies ---");
        System.out.println("""
  1. Active-Active (load balanced across clouds):
     Traffic -> CloudFront -> [AWS 50%] [GCP 50%]
     Benefit: avoids single cloud failure
     Cost: ~2x infrastructure, egress fees

  2. Active-Passive (failover):
     Primary: AWS us-east-1
     Standby: Azure eastus (replicated data)
     Benefit: simpler, lower cost
     Drawback: failover time (RTO > 5min)

  3. Best-of-breed:
     Compute: AWS (EC2, Lambda)
     Data: GCP (BigQuery, Bigtable)
     AI: Azure (OpenAI, Cognitive Services)
     Benefit: best service per category
     Drawback: operational complexity

  4. Cloud-agnostic (abstraction layer):
     Kubernetes + Terraform + multi-cloud SDK
     Same infra on any cloud
     Benefit: portability
     Cost: lowest common denominator
    """);
    }

    static void abstractionLayer() {
        System.out.println("\n--- Abstraction Layer ---");
        System.out.println("""
  Java multi-cloud SDKs:

  Spring Cloud:
    spring-cloud-aws (S3, SQS, RDS, etc.)
    spring-cloud-gcp (Storage, Pub/Sub, Spanner, etc.)
    spring-cloud-azure (Storage, Service Bus, Cosmos DB, etc.)

  Apache jclouds:
    unified API for 30+ cloud providers
    ComputeService, BlobStore, LoadBalancerService

  Terraform (infrastructure as code):
    resource "aws_instance" "web" { ... }
    resource "azurerm_virtual_machine" "web" { ... }
    Terraform Cloud / Enterprise for multi-cloud orchestration

  Kubernetes:
    EKS (AWS), AKS (Azure), GKE (GCP)
    Same deployment manifests across clouds
    Persistent volumes: EBS, Azure Disk, GCE PD
    Ingress controllers: ALB, AGIC, GCLB
    """);
    }

    static void dataSovereignty() {
        System.out.println("\n--- Data Sovereignty & Compliance ---");
        System.out.println("""
  Data must stay within geographic boundaries:
  - GDPR (EU): data must stay in EU or adequacy jurisdiction
  - CCPA (California): consumer privacy rights
  - LGPD (Brazil), PIPEDA (Canada), POPIA (South Africa)

  Impact on multi-cloud:
  - EU data -> EU cloud regions (Frankfurt, Ireland, Paris)
  - US data -> US regions or with Privacy Shield
  - China data -> must use Chinese providers (Alibaba, Tencent)

  Cloud provider regions:
  AWS: 30+ regions worldwide
  Azure: 60+ regions
  GCP: 40+ regions

  Data residency:
  - Choose region at deployment time
  - Data replication across regions = compliance risk
  - Some industries require in-country hosting (banking, gov)

  Solutions: provider-specific regions, Sovereign Cloud offerings
    """);
    }

    static void vendorLockIn() {
        System.out.println("\n--- Vendor Lock-In Mitigation ---");
        System.out.println("""
  Lock-in factors per layer:

  Compute:
  - AWS: EC2, Lambda, ECS (Fargate)
  - Mitigation: Kubernetes (portable), Packer (portable images)

  Storage:
  - AWS: S3 API (de facto standard)
  - Azure: Blob Storage (compatible with S3 via BlobFuse)
  - GCP: Cloud Storage (S3-compatible)
  - Mitigation: S3-compatible API, MinIO for on-prem

  Database:
  - AWS: DynamoDB, Aurora, RDS
  - Azure: Cosmos DB, SQL Database
  - GCP: Spanner, Cloud SQL, Bigtable
  - Mitigation: PostgreSQL/MySQL (portable), Cassandra (portable)

  Messaging:
  - AWS: SQS, SNS, EventBridge
  - Azure: Service Bus, Event Grid
  - GCP: Pub/Sub
  - Mitigation: Apache Kafka (portable), RabbitMQ

  Best practice: use portable technologies where possible
  Accept lock-in on value-added services (competitive advantage)
    """);
    }
}
