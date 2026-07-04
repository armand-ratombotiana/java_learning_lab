# Configuration Management Performance

## Ansible Performance Optimization
- **SSH pipelining**: Reduces SSH connections per task (`pipelining = True`).
- **ControlPersist**: Reuse SSH connections (`ssh_args = -o ControlMaster=auto -o ControlPersist=60s`).
- **Fact caching**: Cache facts with Redis or JSON file (`gathering = smart`).
- **Forks**: Increase parallel execution (`forks = 50`).
- **Mitogen**: Alternative connection plugin (2-5x speedup, now merged into ansible-core).
- **Tags**: Run only relevant tasks (`--tags deploy`).

## Scaling CM
- **Inventory partitioning**: Split inventory into functional groups.
- **Pull vs push**: Pull model (Puppet/Chef) scales better for 10,000+ nodes.
- **Batch execution**: Use serial (`serial: 10`) for rolling updates.
- **Async tasks**: Long operations (package compile) can run async with polling.

## Puppet Performance
- **Environments**: Isolate manifests per environment.
- **File sync**: Efficient file distribution across Puppet Masters.
- **PuppetDB**: Query optimization for large node populations.
- **Compilation**: Multiple Master compilers for large deployments.

## Chef Performance
- **Ohai optimization**: Disable unnecessary plugins.
- **Chef Solo**: Run without Chef Server for small deployments.
- **Cookbook caching**: Local caching of cookbooks.

## Monitoring CM Performance
- **Run time**: Track playbook/puppet/chef run duration.
- **Failure rate**: Percentage of nodes with failed runs.
- **Drift**: Number of nodes deviating from desired state.
