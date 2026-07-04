# Helm Flashcards

**Q: What is Helm?**
A: Kubernetes package manager (CNCF graduated project).

**Q: What is a Chart?**
A: A Helm package containing K8s resource templates and metadata.

**Q: What is a Release?**
A: A running instance of a chart in a Kubernetes cluster.

**Q: What is a Repository in Helm?**
A: Collection of published charts with index.yaml.

**Q: What is values.yaml?**
A: Default configuration values file for a chart.

**Q: What is the three-way merge?**
A: Helm compares prev release, current manifest, and live state.

**Q: What is a Helm Hook?**
A: Resource that runs at specific lifecycle events (pre/post install/upgrade).

**Q: What is _helpers.tpl?**
A: File for reusable named Go template definitions.

**Q: What does `helm lint` do?**
A: Validates chart format and template syntax.

**Q: How does Helm 3 store release data?**
A: As Kubernetes Secrets in the release namespace.
