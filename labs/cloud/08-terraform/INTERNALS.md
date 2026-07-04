# Terraform — Internals

## State Management

### State Format (JSON)
```json
{
  "version": 4,
  "terraform_version": "1.7.0",
  "serial": 42,
  "lineage": "abc-def-ghi",
  "outputs": {},
  "resources": [
    {
      "module": "module.vpc",
      "mode": "managed",
      "type": "aws_vpc",
      "name": "this",
      "provider": "provider[\"registry.terraform.io/hashicorp/aws\"].us-east-1",
      "instances": [
        {
          "attributes_flat": {
            "id": "vpc-xxx",
            "cidr_block": "10.0.0.0/16",
            "enable_dns_support": true
          },
          "dependencies": [],
          "depends_on": []
        }
      ]
    }
  ]
}
```

### State Locking (DynamoDB)
```
DynamoDB table: terraform-state-locks
  LockID (PK): "my-terraform-state/prod/java-app/terraform.tfstate"
  Held until: apply completes

Prevents:
  - Two team members running apply simultaneously
  - Plan based on stale state
```

## Terraform Core Architecture

```
Terraform CLI
  │
  ├── Terraform Core
  │   ├── Config Loader (HCL parser)
  │   ├── State Manager (read/write/lock)
  │   ├── Graph Builder (resource dependencies)
  │   └── Graph Walker (execute in order)
  │
  └── Terraform Plugins (separate binaries)
      ├── Provider: hashicorp/aws (translate HCL → AWS API calls)
      ├── Provider: hashicorp/random (local random number gen)
      └── Provisioner: file/remote-exec (post-deploy config)
```

## Provider Internals

### AWS Provider Architecture
```
aws_instance resource in HCL
  │
  ▼
AWS Provider plugin (Go binary)
  │
  ├── CRUD handlers:
  │   ├── Create: ec2.RunInstances
  │   ├── Read:   ec2.DescribeInstances
  │   ├── Update: ec2.ModifyInstanceAttribute
  │   └── Delete: ec2.TerminateInstances
  │
  └── AWS SDK v2 for Go
       │
       ▼
  AWS API (authenticated via env/role)
```

### Provider Registry
- Published at registry.terraform.io
- Signed with HashiCorp GPG key
- Verified checksums ensure integrity
- Versioned with semantic versioning

## Graph Execution

```
Plan = ordered list of graph walks:

Phase 1: Create (parallel where no dependency)
  aws_vpc (no deps) ──► aws_subnet (depends on VPC)
  aws_security_group (no deps ──► aws_instance (depends on subnet + SG)

Phase 2: Update (in-place)
  aws_instance.tags changed → API update call

Phase 3: Destroy (reverse order)
  aws_instance → aws_subnet → aws_vpc
```
