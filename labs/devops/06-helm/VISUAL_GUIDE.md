# Visual Guide to Helm

## Helm Chart Structure
```
mychart/
  Chart.yaml       ─── Metadata (name, version, dependencies)
  values.yaml      ─── Default config values
  templates/       ─── Go templates that generate K8s YAML
    deployment.yaml
    service.yaml
    _helpers.tpl   ─── Reusable template snippets
    NOTES.txt      ─── Post-install instructions
  charts/          ─── Subchart dependencies (tgz files)
  crds/            ─── CRDs to install before templates
  README.md        ─── Chart documentation
```

## Helm Workflow
```
┌──────────┐   ┌──────────┐   ┌──────────┐   ┌──────────┐
│  helm    │   │  helm    │   │  helm    │   │  helm    │
│  create  │──▶│  lint    │──▶│  install │──▶│  upgrade │
│  (make   │   │  (check  │   │  (deploy │   │  (update │
│   chart) │   │  syntax) │   │   first) │   │   app)   │
└──────────┘   └──────────┘   └──────────┘   └──────────┘
                                    │
                              ┌─────▼─────┐
                              │  helm     │
                              │  rollback │
                              │  (revert) │
                              └───────────┘
```

## Values Override Hierarchy
```
Default values.yaml
    │
    ▼
--values custom.yaml
    │
    ▼
--set key=value
    │
    ▼
--set-string key=stringvalue
    │
Final values (last wins)
```

## Release Lifecycle
```
helm install → Revision 1
helm upgrade → Revision 2
helm upgrade → Revision 3
helm rollback 1 → Revision 4 (resumes from Rev 1 state)
helm uninstall → Deleted (can uninstall --keep-history)
```
