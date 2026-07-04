# Spring Data JPA

Spring Data JPA provides repository support for JPA-based data access layers.

## Topics
- Entity mapping with JPA annotations
- Repository interfaces (CrudRepository, JpaRepository)
- Custom query methods
- @Query with JPQL and native SQL
- Entity relationships (@OneToMany, @ManyToMany)
- Pagination and sorting
- Auditing with @CreatedDate, @LastModifiedDate

## Example
```java
@Entity
public class Product {
    @Id @GeneratedValue
    private Long id;
    private String name;
    private double price;
}

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByNameContaining(String name);
    Page<Product> findByPriceBetween(Double min, Double max, Pageable pageable);
}
```
