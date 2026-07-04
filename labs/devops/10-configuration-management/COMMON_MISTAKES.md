# Common Configuration Management Mistakes

1. **Not testing playbooks** — syntax errors or unintended changes in production.
2. **Missing idempotency** — tasks that change state every run (e.g., append to file without checking).
3. **Hardcoding values** — use variables and group_vars/host_vars.
4. **No fact caching** — gathering facts for 1000 hosts every run is slow.
5. **Ignoring order dependencies** — tasks may need explicit ordering (especially for services).
6. **One monolithic playbook** — break into roles and plays.
7. **No version control** — CM code should be in git with PR reviews.
8. **Exposing secrets** — use Ansible Vault, HashiCorp Vault, or encrypted variables.
9. **Not using tags** — can't run specific parts of long playbooks.
10. **Large inventory files** — use dynamic inventory from cloud providers.
11. **Forgetting handlers** — service restart doesn't happen after config change.
12. **Mixing snake_case and CamelCase** — inconsistent variable naming causes confusion.
