# 07 - DNS and Load Balancing

## Overview

This lab covers DNS resolution, round-robin DNS, load balancer types, health checks, and service routing with Java implementations.

## Learning Objectives
- Understand DNS resolution process
- Implement DNS lookups in Java
- Configure load balancers
- Implement health checks
- Understand load balancing algorithms

## Quick Start
```java
// DNS lookup
InetAddress[] addresses = InetAddress.getAllByName("example.com");
for (InetAddress addr : addresses) {
    System.out.println("IP: " + addr.getHostAddress());
}

// Java DNS resolver with timeout
import java.net.*;
import java.util.*;

public class DnsResolver {
    public static List<String> resolve(String hostname) {
        try {
            return Arrays.stream(InetAddress.getAllByName(hostname))
                .map(InetAddress::getHostAddress)
                .toList();
        } catch (UnknownHostException e) {
            return Collections.emptyList();
        }
    }
}
```
