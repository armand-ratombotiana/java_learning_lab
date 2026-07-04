# Mental Models for Terraform

## 1. The Blueprint (Declarative)

```
Traditional (imperative):           Terraform (declarative):
"Create a VPC, then create          "I want: a VPC with CIDR 10.0.0.0/16,
a subnet, then launch               a subnet with CIDR 10.0.1.0/24,
an EC2 instance in that subnet"     and an EC2 instance in that subnet"

Like blueprints vs construction instructions.
You don't tell the builder "hammer this nail, then saw that board" —
you give them the blueprint, and they figure out the steps.
```

## 2. The Graph (Dependency Graph)

```
Terraform builds a dependency graph from your resources:

  aws_vpc.main ──► aws_subnet.public ──► aws_instance.web
       │                                       │
       │                                  aws_security_group.web
       ▼
  aws_internet_gateway.main ──► aws_route_table.public

Edges = implicit dependencies (via references)
No circular dependencies allowed
Terraform parallelizes independent resources
```

## 3. The State File (Source of Truth)

```
State file = Terraform's memory of what exists

┌──────────────────────────────┐
│  terraform.tfstate            │
│  {                            │
│    "resources": [{           │
│      "type": "aws_instance", │
│      "name": "web",          │
│      "instances": [{         │
│        "id": "i-xxx",        │
│        "attributes": {       │
│          "public_ip": "..."  │
│        }                     │
│      }]                      │
│    }]                        │
│  }                            │
└──────────────────────────────┘

Changes external to Terraform = "drift"
terraform plan shows drift
terraform apply refreshes state
terraform import brings external resources under management
```

## 4. The Resource Lifecycle

```
Create ──► Read (refresh) ──► Update ──► Delete
   │            │               │           │
   │            │               │           │
   ▼            ▼               ▼           ▼
  apply       plan            apply       destroy

- create_before_destroy: new resource first, then old (zero-downtime)
- prevent_destroy: safety lock on critical resources
- ignore_changes: don't track specific attribute changes
```

## 5. The Module (LEGO Brick)

```
Modules = reusable LEGO bricks

Terraform Registry brick:
  terraform-aws-modules/vpc/aws v5.0.0
  └── Creates: VPC, subnets, route tables, IGW, NAT GW (60+ resources)

Your module brick:
  modules/java-app-cluster
  └── Creates: ALB, ASG, EC2, security groups, CloudWatch alarms

Compose bricks in root module:
  module "vpc"          { source = "...", cidr = "10.0.0.0/16" }
  module "java-cluster" { source = "...", vpc_id = module.vpc.id }
```
