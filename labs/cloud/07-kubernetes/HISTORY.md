# History of Kubernetes

## Timeline

| Year | Milestone |
|------|-----------|
| 2003 | Google develops **Borg** — internal cluster management system |
| 2008 | Omega — improved Borg scheduler (based on Paxos) |
| 2014 | **Kubernetes v1.0** announced (based on Borg/Omega learnings) |
| 2015 | Kubernetes v1.0 released; donated to CNCF (Cloud Native Computing Foundation) |
| 2016 | K8s wins container orchestration war (vs Swarm, Mesos, Nomad) |
| 2017 | Kubernetes becomes de facto standard; **EKS** (AWS) and **AKS** (Azure) launch |
| 2018 | K8s graduates from CNCF incubation; Helm v3 (no Tiller); Istio 1.0 |
| 2019 | etcd graduated; K8s 1.16 (CRDs become GA); AWS EKS GA |
| 2020 | K8s 1.20 (removes Docker as container runtime — now uses containerd) |
| 2021 | K8s 1.22 (first release with no Dockershim); **EKS Anywhere** |
| 2022 | K8s 1.24 (Dockershim removed); PodSecurityPolicy replaced by PSA |
| 2023 | K8s 1.27 (swap support beta); K8s 1.28 (mixed version proxy alpha) |
| 2024 | K8s 1.29; EKS Auto Mode; sidecar containers GA |

## Key Insight
Kubernetes is the Linux of cloud computing: an open-source kernel for distributed systems. Just as Linux abstracted hardware, Kubernetes abstracts clusters. The API is the interface; everything else is a plugin.
