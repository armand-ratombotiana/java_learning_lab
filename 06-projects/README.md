# Java Projects Module

## Overview

This module covers project organization, build tools, and best practices for building production-ready Java applications.

## Topics Covered

### Build Tools
- Maven project structure and lifecycle
- Gradle configuration and tasks
- Plugin management
- Dependency management

### Project Structure
- Standard Maven/Gradle layout
- Multi-module projects
- Source code organization
- Resource management

### Best Practices
- Clean code organization
- Test organization and patterns
- Documentation standards
- Build optimization

## Sub-modules

### 1. E-commerce Inventory System
A comprehensive inventory management system demonstrating:
- Multi-module Maven project structure
- Service layer architecture
- Database integration patterns
- Unit and integration testing

### 2. Data Pipeline
A batch processing system showcasing:
- ETL pipeline implementation
- Scheduled jobs with Quartz
- File processing and transformation
- Error handling and recovery

## Getting Started

Each sub-module contains:
- Source code in `src/main/java`
- Tests in `src/test`
- Maven/Gradle build configuration

Build and run:
```bash
cd 01-ecommerce-inventory
mvn clean compile exec:java
```