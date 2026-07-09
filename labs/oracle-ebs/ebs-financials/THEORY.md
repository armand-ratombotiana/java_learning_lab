# Theory: EBS Financials

## 1. Core Concepts

### 1.1 Overview

In Oracle E-Business Suite R12.2, EBS Financials represents a critical functional area. The technology stack includes Oracle Database 19c, Oracle Fusion Middleware, and the EBS application tier. Each component interacts through well-defined APIs and database views.

### 1.2 Key Principles

1. Separation of Concerns - The EBS architecture divides presentation, business logic, and data layers.
2. Multi-Org Access Control (MOAC) - Enables a single EBS instance to serve multiple operating units.
3. Edition-Based Redefinition - R12.2 uses EBR to support online patching, minimizing downtime.

### 1.3 Database Objects

- APPS schema - The runtime schema containing all EBS code
- FND tables - Foundation tables used by all products
- Product-specific tables (GL_, AP_, AR_, PO_, INV_)

### 1.4 Concurrent Processing

EBS uses concurrent managers to run background requests. Each request has a phase (Pending, Running, Completed) and a status (Normal, Warning, Error).

## 2. Key Technologies

| Technology | Purpose |
|------------|---------|
| Oracle Forms | Desktop UI |
| OA Framework | Web-based UI |
| Oracle Workflow | Business process automation |
| Oracle Reports | Reporting engine |
| ADOP | Online patching |

## 3. Summary

This lab builds a solid theoretical foundation for understanding EBS Financials within the broader EBS ecosystem. All subsequent labs will reference these concepts.
