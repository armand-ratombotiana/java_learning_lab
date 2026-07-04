# Terraform Architecture

## Component Architecture
```
┌────────────────────────────────────────────────────────────┐
│                      Terraform CLI                          │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐  │
│  │ Config   │  │ State    │  │ Graph    │  │ Provider │  │
│  │ Loader   │  │ Manager  │  │ Builder  │  │ Registry │  │
│  └──────────┘  └──────────┘  └──────────┘  └──────────┘  │
└──────────────────────┬─────────────────────────────────────┘
                       │ gRPC
┌──────────────────────▼─────────────────────────────────────┐
│                    Provider Plugins                         │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐  │
│  │ AWS      │  │ GCP      │  │ Azure    │  │ K8s      │  │
│  │ Provider │  │ Provider │  │ Provider │  │ Provider │  │
│  └──────────┘  └──────────┘  └──────────┘  └──────────┘  │
└────────────────────────────────────────────────────────────┘
```

## Directory Layout Convention
```
├── main.tf           # Main resource definitions
├── variables.tf      # Input variables
├── outputs.tf        # Output values
├── provider.tf       # Provider configuration
├── terraform.tfvars  # Variable values (gitignored)
├── modules/
│   ├── networking/
│   ├── compute/
│   └── database/
└── environments/
    ├── dev/
    ├── staging/
    └── prod/
```
