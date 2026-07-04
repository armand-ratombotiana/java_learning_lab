# History of Helm

- **2015**: Deis (later acquired by Microsoft) created Helm as part of the Kubernetes ecosystem.
- **2016 (Jan)**: Helm 2 architecturally changed; Tiller (server-side component) introduced.
- **2016 (Jun)**: Helm joins CNCF as an incubation project.
- **2018 (Jun)**: Helm graduates from CNCF incubation.
- **2019 (Nov)**: Helm 3 released — removed Tiller, simplified security, adopted three-way merge.
- **2020**: Helm 3 becomes default; OCI registry support added.
- **2021**: Helm 3.7 — OCI registries GA; signed provenance files.
- **2022-2024**: Helm 3.10+ — better JSON schema validation, OCI support improvements, dependency management enhancements.

Key improvement from Helm 2→3: removing Tiller eliminated a major security concern (server-side cluster-admin component with its own RBAC).
