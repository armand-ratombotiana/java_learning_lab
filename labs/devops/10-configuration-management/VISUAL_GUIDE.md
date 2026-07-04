# Visual Guide to Configuration Management

## Ansible Architecture
```
┌──────────────────────────────────────────────────────┐
│                   Control Node                        │
│  ┌──────────┐  ┌──────────┐  ┌──────────────────┐  │
│  │ Playbook │  │ Inventory│  │ Roles / Modules  │  │
│  └──────────┘  └──────────┘  └──────────────────┘  │
└────────────────────────┬─────────────────────────────┘
                         │ SSH
┌────────────────────────▼─────────────────────────────┐
│                   Managed Nodes                        │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐           │
│  │  Node 1  │  │  Node 2  │  │  Node 3  │  ...      │
│  │ (Ubuntu) │  │  (CentOS)│  │ (Windows)│           │
│  └──────────┘  └──────────┘  └──────────┘           │
└──────────────────────────────────────────────────────┘
```

## Playbook Execution Flow
```
ansible-playbook webserver.yml
    │
    ▼
Read inventory → Group hosts → Gather facts
    │
    ▼
Play 1: All webservers
    ├── Task 1: Install nginx (package module)
    ├── Task 2: Copy nginx.conf (template module)
    ├── Task 3: Start nginx (service module)
    └── Handler: restart nginx (on notify)
    │
    ▼
Play 2: Database servers
    ├── Task 1: Install PostgreSQL
    ├── Task 2: Configure pg_hba.conf
    └── Task 3: Start PostgreSQL
    │
    ▼
Results: ok=10 changed=3 failed=0
```

## Idempotent Task Flow
```
Check current state
    │
    ├── State matches desired → OK (no change)
    │
    └── State differs → Apply change → Report changed
```
