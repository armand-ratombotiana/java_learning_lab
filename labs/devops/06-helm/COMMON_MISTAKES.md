# Common Helm Mistakes

1. **Modifying release-managed resources with kubectl** — Helm reverts on next upgrade.
2. **Not pinning chart versions** — `helm upgrade` with `--version` omitted pulls latest.
3. **Templating issues** — indentation problems in `range`/`with` blocks.
4. **Storing secrets in values.yaml** — charts are distributed; use `--set` or external secrets.
5. **Ignoring NOTES.txt** — users need instructions after install.
6. **Missing _helpers.tpl** — duplicating labels and selectors across templates.
7. **Using `latest` tag** — chart will always trigger upgrade; pin image tags.
8. **Not testing upgrades** — `helm upgrade --dry-run` before applying.
9. **Complex values structures** — flat values are easier to override with `--set`.
10. **No dependencies lock** — `helm dependency build` should be run after `update`.
11. **Overriding subchart values incorrectly** — use correct path: `parent.child.key`.
12. **Forgetting `helm lint`** — catches many syntax and validation errors.
