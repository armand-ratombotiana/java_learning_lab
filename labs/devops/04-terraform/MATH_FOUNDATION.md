# Math Foundation for Terraform

N/A — Terraform has limited mathematical foundation.

## Graph Theory
- **Directed Acyclic Graph (DAG)**: Resources form a DAG based on references. Terraform performs topological sort to determine creation/deletion order.
- **Cyclic dependency detection**: Terraform detects and rejects circular dependencies at plan time.

## State Serialization
- **Serial number**: Monotonically increasing integer in state file for conflict detection.
- **Lineage**: UUID identifying state file lineage (changes on `terraform state replace-provider`).

## Plan Diff Algorithm
- Structural comparison of current state attributes vs desired attributes.
- Objects compared recursively; sets compared element-by-element.

## Resource Counting
- `count = N` creates N instances indexed `[0..N-1]`.
- `for_each` on map/set of strings.

No advanced mathematics beyond basic graph theory and data structure comparison.
