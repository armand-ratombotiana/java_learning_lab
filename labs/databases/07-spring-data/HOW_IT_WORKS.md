# How Spring Data JPA Works

## Startup Phase
1. `@EnableJpaRepositories` triggers `JpaRepositoriesRegistrar`
2. Scans base packages for interfaces extending `Repository`
3. For each interface, creates a `JpaRepositoryFactoryBean`
4. Factory creates a JDK dynamic proxy implementing the interface
5. `SimpleJpaRepository<T, ID>` backs all CRUD method calls
6. Custom method names are parsed into `PartTree` objects

## Query Execution
1. Method invocation hits the proxy's `invoke()` handler
2. `RepositoryQuery` looks up cached or builds a new query
3. `PartTree` parser decomposes method name into predicate tree
4. `QueryCreator` converts the tree to JPQL (or CriteriaQuery)
5. `EntityManager` executes the query and returns mapped entities

## Lifecycle
- Repositories are singleton Spring beans (scoped as singleton)
- `EntityManager` is proxy-scoped to the current transaction
- Each repository method participates in the current transaction
