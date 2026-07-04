# Configuration Management Architecture

## Ansible Architecture
```
┌──────────────────────────────────────────────────────────────┐
│                     Ansible Automation Platform               │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌────────────┐ │
│  │ Inventory│  │ Execution│  │ Playbook │  │ Automation │ │
│  │          │  │ Env      │  │ Content  │  │ Hub        │ │
│  └──────────┘  └──────────┘  └──────────┘  └────────────┘ │
└──────────────────────────┬───────────────────────────────────┘
                           │
┌──────────────────────────▼───────────────────────────────────┐
│                      Managed Infrastructure                   │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐   │
│  │ Servers  │  │ Network  │  │ Cloud    │  │ Containers│  │
│  │ (Linux/  │  │ Devices  │  │ (AWS/GCP/│  │ (Docker, │  │
│  │  Windows)│  │          │  │  Azure)  │  │  K8s)    │  │
│  └──────────┘  └──────────┘  └──────────┘  └──────────┘   │
└──────────────────────────────────────────────────────────────┘
```

## Tool Architecture Comparison
```
Ansible:   Push (SSH)     — agentless, idempotent modules
Puppet:    Pull (agent)   — master/agent, SSL, catalog compilation
Chef:      Pull (agent)   — server/client, cookbook convergence
SaltStack: Push/Pull      — event-driven, remote execution
```
