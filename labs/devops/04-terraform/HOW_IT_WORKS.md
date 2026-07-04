# How Terraform Works

## Core Execution Flow
1. **Parse**: Read all `.tf` and `.tf.json` files in directory.
2. **Build Graph**: Create dependency graph from resource references.
3. **Refresh**: Query current state of resources via providers (or read from state file).
4. **Diff**: Compare current state with desired state (config).
5. **Plan**: Generate ordered set of create/update/delete operations.
6. **Apply**: Execute plan, updating state file after each operation.

## Provider Architecture
```
Terraform Core → gRPC → Provider Plugin (binary)
                           │
                    ┌──────┴──────┐
                    │  Cloud API  │
                    │  (AWS/GCP)  │
                    └─────────────┘
```

## State Management
- **Local**: `terraform.tfstate` file in working directory.
- **Remote**: S3, GCS, Azure Storage, Consul, Terraform Cloud.
- **State locking**: Prevent concurrent modifications (DynamoDB, Consul, PG).
- **State commands**: `terraform state list`, `terraform state show`, `terraform state mv`.

## Resource Lifecycle
```
Create → Update (in-place) → Delete
           │
     Recreate (force new)
```
