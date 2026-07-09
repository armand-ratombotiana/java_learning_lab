# Architecture: EBS Setup and Configuration

## 1. Component Overview

EBS Setup and Configuration follows standard EBS patterns: presentation (Forms/OAF), business logic (PL/SQL + Java), and data layer.

## 2. Multi-Tier Layout

Client Tier - Application Tier (Forms, OHS, Concurrent Managers) - Database Tier (Oracle 19c)

## 3. Key Interfaces
- Public APIs for integration
- Internal APIs for EBS internal use
- Open Interface Tables for data import

## 4. Deployment Options
- Single node
- Multi-node
- Cluster
- Cloud (OCI)

## 5. Integration Points

- Oracle Workflow
- Oracle Alerts
- Business Event System
- XML Gateway
- SOA/OSB
