# Lab 03 — Serverless Deep

## Overview
Deep dive into serverless computing internals: function lifecycle, cold starts, provisioned concurrency, event filtering, Lambda extensions, and performance optimization.

## Prerequisites
- Java 21+ development environment
- Basic serverless knowledge
- Understanding of event-driven architectures

## What You Will Learn
- Model function invocation lifecycle (init, invoke, shutdown)
- Implement cold start detection and mitigation strategies
- Build provisioned concurrency management
- Design event filtering patterns for SQS, S3, and Kinesis triggers
- Build custom Lambda extensions for observability and secrets

## Topics Covered
| Topic | Description |
|-------|-------------|
| Function Internals | Sandbox lifecycle, runtime API, execution environment |
| Cold Starts | Init phase overhead, snapstart, runtime hooks |
| Provisioned Concurrency | Pre-warmed environments, scaling plans, cost trade-offs |
| Event Filtering | Message filtering, event patterns, batching |
| Lambda Extensions | Telemetry API, Logs API, custom extensions |
| Performance | Memory tuning, connection reuse, tiered compilation |

## Java Package
`com.cloud.deep.lab03`
