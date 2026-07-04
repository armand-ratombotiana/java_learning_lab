# Theory: Spring Data JPA

## Repository Abstraction
Spring Data JPA provides a powerful repository abstraction that eliminates boilerplate DAO code.

### Core Interfaces
- **Repository<T, ID>**: Marker interface
- **CrudRepository<T, ID>**: Basic CRUD operations
- **PagingAndSortingRepository<T, ID>**: Pagination and sorting
- **JpaRepository<T, ID>**: Full JPA support with flush, batch operations

### Query Methods
Spring Data JPA generates queries from method names:
- `findByName(String name)` -> WHERE name = ?
- `findByNameContaining(String partial)` -> WHERE name LIKE ?
- `findByAgeBetween(int min, int max)` -> WHERE age BETWEEN ? AND ?
- `findByDepartmentName(String dept)` -> JOIN with department.name = ?

### Entity Mapping
- @Entity: Marks class as JPA entity
- @Table: Specifies table name
- @Id: Primary key
- @GeneratedValue: ID generation strategy
- @Column: Column mapping with constraints
- @OneToMany, @ManyToOne: Relationship mappings
- @JoinColumn: Foreign key specification
