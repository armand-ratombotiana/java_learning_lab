# Mathematical Foundation: Modules

## Directed Acyclic Graph (DAG)
A module graph is a DAG where nodes are modules and edges are `requires` dependencies. For modules M1...Mn, there is an edge Mi -> Mj if Mi requires Mj. The graph must contain no cycles.

## Transitive Closure
Given a set of root modules R, the resolved module graph is the transitive closure of the `requires` relation starting from R. Let M be the set of all modules and R ⊆ M be root modules. The resolved set is {m ∈ M | there exists a path from some r ∈ R to m in the dependency graph}.

## Reflexive Transitive Closure
The `requires transitive` relation extends the dependency graph. If module A requires transitive B, then any consumer of A automatically reads B. This is equivalent to computing the reflexive transitive closure of the dependency graph.

## Module Resolution
The resolution algorithm:
1. Start with root modules in queue Q
2. While Q is not empty: pop module m, find all modules m requires, add unvisited ones to Q
3. The visited set is the resolved module graph
4. Verify no cycles exist (detect back edges during traversal)

## Configuration Integrity
A configuration is valid if:
- All required modules exist in the module graph
- No module has two parents exporting the same package (split package detection)
- Service provider modules are reachable from consumer modules
- No qualified exports target non-existent modules (warning at compile time)

## Custom Runtime Size
If a JDK has K total modules and an application uses M modules directly, the custom runtime will contain approximately M × (1 + avg_dependencies) modules after transitive closure. jlink also includes services transitively.
