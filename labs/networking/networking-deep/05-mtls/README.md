# Lab 05 — mTLS (Mutual TLS)

## Overview
Deep dive into mutual TLS: client certificates, certificate chains, revocation strategies, mTLS in service mesh, and SPIFFE identity framework.

## Prerequisites
- Java 21+ development environment
- TLS fundamentals
- Public key infrastructure concepts

## What You Will Learn
- Implement mTLS handshake with mutual certificate exchange
- Build certificate chain validation for client and server
- Model certificate revocation (CRL, OCSP)
- Implement mTLS for service-to-service communication
- Understand SPIFFE identity and SVIDs

## Topics Covered
| Topic | Description |
|-------|-------------|
| Mutual TLS | Client certificate verification, CertificateRequest |
| Certificate Chains | Server chain, client chain, cross-signing |
| Revocation | CRL, OCSP, OCSP stapling, CRLite |
| Service Mesh mTLS | Istio, Linkerd, Consul Connect mesh |
| SPIFFE | SPIFFE ID, SVID, workload attestation |
| SPIFFE Federation | Trust bundles, domain federation |

## Java Package
`com.networking.deep.lab05`
