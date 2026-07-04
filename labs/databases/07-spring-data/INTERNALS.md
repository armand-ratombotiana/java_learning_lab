# Internals: Spring Data JPA

## Key Classes

| Class | Role |
|---|---|
| `JpaRepositoryFactory` | Creates proxy instances |
| `SimpleJpaRepository` | Default implementation behind the proxy |
| `PartTree` | Parsed method name representation |
| `PartTreeJpaQuery` | JPQL query from method name |
| `QueryLookupStrategy` | Strategy for resolving query methods |
| `JpaCriteriaQueryContext` | Criteria API query context |

## Proxy Creation Flow
```
@EnableJpaRepositories
  → JpaRepositoriesRegistrar
    → RepositoryBeanDefinitionRegistrarSupport
      → JpaRepositoryFactoryBean
        → JpaRepositoryFactory.getRepository()
          → ProxyFactory(JDK) → JpaRepository proxy
```

## Method Name Parsing
`findByLastNameAndAgeBetween` is parsed into:
- Action: `FIND` (or `COUNT`, `DELETE`, `EXISTS`)
- Distinct: `false`
- Parts: [`lastName(Equals)`, `age(Between)`]
- Order: none

## Query Cache
- `PartTreeJpaQuery` instances are cached in `JpaQueryMethod`'s `queryCache`
- Keyed by `Method` object
- Rebuilt on `@Entity` metadata changes (rare)
