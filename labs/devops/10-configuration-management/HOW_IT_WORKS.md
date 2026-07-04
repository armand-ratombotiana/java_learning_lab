# How Configuration Management Works

## Ansible Push Model
```
Control Node ──SSH──▶ Managed Node 1
(playbook)    ──SSH──▶ Managed Node 2
              ──SSH──▶ Managed Node 3

1. Control node parses playbook YAML.
2. Resolves tasks, modules, and variables.
3. Connects to managed nodes via SSH (WinRM for Windows).
4. Transfers Python modules to target.
5. Executes modules on target (no agent required).
6. Returns results to control node.
```

## Puppet Pull Model
```
Puppet Master ──catalog──▶ Puppet Agent (Node 1)
(compiles        ◀──facts── Puppet Agent (Node 2)
 manifests)                 Puppet Agent (Node 3)

1. Agent sends facts to Master.
2. Master compiles catalog from manifests + facts.
3. Agent receives catalog.
4. Agent applies resources to system.
5. Agent reports results to Master.
```

## Chef Pull Model
```
Chef Server ──cookbook──▶ Chef Client (Node 1)
(stores         ◀──ohai─── Chef Client (Node 2)
 cookbooks)                 Chef Client (Node 3)

1. Client runs ohai (collects system data).
2. Client downloads cookbook from Server.
3. Client compiles and converges recipes.
4. Client reports run status to Server.
```
