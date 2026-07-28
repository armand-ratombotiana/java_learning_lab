# Lab 01: Java Module System Deep Dive

## Overview
The Java Module System (JPMS, introduced in Java 9) provides strong encapsulation and explicit dependency declarations. This lab covers module descriptors, exports, requires, services, and migration strategies.

## Goals
- Write `module-info.java` descriptors
- Export packages with `exports` and qualified exports
- Declare dependencies with `requires` (including `requires transitive`)
- Use service loading with `provides`/`uses`
- Migrate existing JARs to modules

## Prerequisites
- Java 9+
- Understanding of JAR files and classpath
- Maven/Gradle basics
