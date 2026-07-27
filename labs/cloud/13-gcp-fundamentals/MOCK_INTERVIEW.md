# Mock Interview — GCP Fundamentals

## Format
- **Duration**: 45 minutes
- **Type**: Technical + Architecture
- **Difficulty**: Associate Level

## Warm-Up (5 min)

Q1: Explain the GCP resource hierarchy: Organization → Folder → Project → Resource.

Q2: What is the difference between Compute Engine, GKE, Cloud Run, and Cloud Functions?

## Technical Questions (20 min)

### Question 1: GKE Cluster Design (10 min)
Design a GKE cluster for a Java microservices application. Requirements:
- 12 microservices with StatefulSets (PostgreSQL, Redis)
- 99.95% uptime SLA
- Auto-scaling (100-1000 pods)
- Ingress with SSL termination
- Namespace isolation per team (5 teams)
- Cost optimization (preemptible nodes for non-critical)

### Question 2: GCP Data Pipeline (10 min)
Design a data pipeline that:
- Ingests streaming events from IoT devices (10K events/sec)
- Cleanses and enriches events
- Stores raw data in Cloud Storage
- Makes data available for real-time queries
- Generates daily analytics reports

**Choose services**: Pub/Sub, Dataflow, BigQuery, Cloud Storage, Cloud Functions

## Behavioral Question (10 min)

**Question**: Tell me about a time you used GCP (or another cloud) for a data-intensive workload. How did you manage costs and performance?

## System Design Whiteboard (10 min)

**Problem**: Design a globally distributed content platform on GCP:
- Users upload videos (up to 2GB)
- Videos transcoded to 3 resolutions
- Content delivered globally with low latency
- Users can comment and rate in real-time
- Analytics on viewing patterns

## Evaluation Criteria

| Area | Excellent | Good | Needs Improvement |
|------|-----------|------|-------------------|
| GKE | Autopilot, node pools, preemptible, workload identity | Basic cluster | No K8s on GCP |
| Data | Pub/Sub, Dataflow, BigQuery, data lifecycle | Basic pipeline | No streaming concept |
| Networking | Cloud LB, CDN, VPC, Cloud NAT, firewall | Basic VPC | Single network only |
| Identity | Cloud IAM, service accounts, roles, hierarchy | Basic IAM | No IAM structure |
| Serverless | Cloud Run vs Cloud Functions vs App Engine | Basic functions | No serverless on GCP |

## Sample Solution Outline

### GKE Cluster Design
- **Mode**: GKE Standard (Autopilot for simpler ops, but less control over node pools)
- **Node pools**: 
  - Pool 1: e2-standard-4 (3-10 nodes, regular) — for API services
  - Pool 2: n2-highmem-4 (2-5 nodes, regular) — for databases
  - Pool 3: e2-standard-2 (0-20 nodes, preemptible) — for batch jobs
- **Autoscaling**: Cluster autoscaler + HPA per service (CPU/memory/custom metrics)
- **Networking**: 
  - Cloud NAT for outbound (preemptible nodes need NAT)
  - ManagedCertificate for Ingress SSL
  - NetworkPolicy for micro-segmentation
- **Isolation**: Namespaces per team with ResourceQuotas and NetworkPolicies
- **Storage**: 
  - Stateful workloads: Regional Persistent Disk (for HA)
  - Workload Identity for GCP service access (no static keys)

### Data Pipeline
```
IoT Devices → Pub/Sub (10K/s) → Dataflow (Streaming) → BigQuery (real-time)
                                    │
                                    └→ Cloud Storage (raw, partitioned)
                                            │
                                            → BigQuery (batch loads)
                                            → Dataflow (daily aggregation) → Looker
```
- **Pub/Sub**: Topic per device type, subscription with exactly-once delivery
- **Dataflow**: Apache Beam pipeline for enrichment, windowing 1-min for aggregation
- **BigQuery**: Streaming buffer for real-time, partitioned tables for cost control
- **Cloud Storage**: Lifecycle: Standard → Nearline (30d) → Archive (365d)

### Global Video Platform
- **Upload**: Cloud Storage bucket (multi-region), signed URLs for direct upload
- **Transcoding**: Transcoder API (or Dataflow for custom processing)
- **Storage**: Cloud CDN → Cloud Storage (multi-region bucket)
- **Comments/Ratings**: Firestore (real-time sync, noSQL)
- **Auth**: Firebase Authentication
- **API**: Cloud Endpoints / Apigee for API management
- **Analytics**: Pub/Sub → Dataflow → BigQuery → Looker
- **Cost optimization**: 
  - Cloud CDN reduces origin load
  - Lifecycle policies move old content to colder storage
  - Spot/preemptible VMs for transcoding workers
