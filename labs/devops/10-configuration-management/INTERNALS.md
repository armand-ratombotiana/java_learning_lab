# Configuration Management Internals

## Ansible Internals
- **Module execution**: Python scripts pushed via SSH, executed, removed.
- **Fact gathering**: Built-in system information collection via `setup` module.
- **Task execution**: Each task is idempotent; returns `changed`/`ok`/`failed`.
- **Handler**: Runs on `notify` at end of play (service restart, reload).
- **Async**: Long-running tasks with polling or fire-and-forget.

## Puppet Internals
- **Resource Abstraction Layer (RAL)**: Maps resource declarations to system commands.
- **Transaction**: Agent applies resources in dependency order.
- **Noop mode**: Preview changes without applying.
- **PuppetDB**: Stores facts, reports, and catalog data.
- **Environment**: Isolated group of modules and manifests (dev/staging/prod).

## Chef Internals
- **Resource Collection**: All resources compiled before execution.
- **Convergence**: Resources applied in order; each resource ensures desired state.
- **Notifications**: Resources notify other resources (service restart on config change).
- **Search**: Query Chef Server for node/cookbook data.
- **Data Bags**: Global JSON data accessible from recipes.

## Idempotency Implementation
- **Package**: Check if installed before installing.
- **Service**: Check status before starting/stopping.
- **File**: Compare checksum before copying.
- **Template**: Compare rendered template with current file.
- **User**: Check if user exists before creating.
