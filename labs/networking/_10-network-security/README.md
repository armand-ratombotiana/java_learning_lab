# 10 - Network Security

## Overview

Network Security covers TLS/SSL, mutual TLS (mTLS), certificate management, network policies, and firewall configuration. This lab provides Java implementations for secure communication.

## Learning Objectives
- Understand TLS handshake and certificate chains
- Implement mTLS in Java
- Manage certificates with Java KeyStore
- Configure network policies and firewalls

## Quick Start
```java
// Creating SSL context
SSLContext sslContext = SSLContext.getInstance("TLSv1.3");
sslContext.init(null, null, new SecureRandom());

// HTTPS client with custom SSL
HttpClient client = HttpClient.newBuilder()
    .sslContext(sslContext)
    .build();
```
