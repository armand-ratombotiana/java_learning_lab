# Availability - WHY IT EXISTS

## Problem Statement
Systems fail. Hardware dies, networks partition, software bugs crash processes. Without intentional design for availability, any single failure takes the entire system down.

## Origin
Availability engineering emerged from telecommunications (5-nines telephony) and was adapted to distributed computing. The 1970s telephone network achieved 99.999% uptime, setting the standard.

## Core Drivers
- **Revenue protection**: Downtime costs money ($5,600/min average for enterprise)
- **User trust**: Unavailable systems lose users to competitors
- **Regulatory compliance**: Healthcare (HIPAA), Finance (SOX), Payments (PCI-DSS)
- **Brand reputation**: High-profile outages make headlines

## Why Not Just Fix Failures Faster?
- Some failures take minutes to detect, hours to fix
- Cascading failures amplify small issues
- Human error causes 40% of outages
- Hardware failure is statistically certain at scale (Google: hundreds of disks fail/year)

## Java Ecosystem
Spring Boot provides Resilience4j for circuit breakers, retries, and bulkheads. Kubernetes ensures self-healing through liveness/readiness probes. Project Reactor handles reactive recovery.
