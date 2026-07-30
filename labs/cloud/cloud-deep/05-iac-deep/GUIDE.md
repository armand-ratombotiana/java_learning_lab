# GUIDE — IaC Deep

## Step 1: State Management
```java
public record ResourceState(String id, String type, String name, Map<String,Object> attributes, String module) {}
public record TerraformState(String version, String backend, List<ResourceState> resources) {}
```

## Step 2: Module System
- Define module inputs and outputs
- Resolve module from registry
- Compose modules into higher-level abstractions

## Step 3: Workspace Manager
```java
WorkspaceManager wm = new WorkspaceManager(backend);
wm.select("prod");
wm.apply(module);
```

## Step 4: Remote Backend Abstraction
```java
public interface Backend { void write(TerraformState state); TerraformState read(); boolean lock(String id); void unlock(String id); }
```
Implement S3, Azure Storage, and GCS backends.

## Step 5: CloudFormation / CDK Generator
- Parse template JSON/YAML into resource objects
- Generate CDK constructs from high-level intent

## Step 6: Exercises
1. Implement state locking with DynamoDB-like lease mechanism
2. Build a module dependency graph resolver
3. Create a CDK construct for a 3-tier web application
