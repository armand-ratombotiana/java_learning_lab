# Code Deep Dive: Query Optimization

## EXPLAIN ANALYZE in Java

```java
@Service
public class QueryAnalyzer {
    private final JdbcTemplate jdbcTemplate;

    public void analyzeQuery(String sql, Object... params) {
        // Get execution plan
        String explainSql = "EXPLAIN (ANALYZE, BUFFERS, FORMAT JSON) " + sql;
        String plan = jdbcTemplate.queryForObject(explainSql, String.class, params);
        log.info("Execution plan: {}", plan);

        // Parse plan to extract key metrics
        JsonNode root = new ObjectMapper().readTree(plan);
        JsonNode planNode = root.get(0).get("Plan");
        double totalCost = planNode.get("Total Cost").asDouble();
        double actualTime = planNode.get("Actual Total Time").asDouble();

        if (planNode.get("Node Type").asText().equals("Seq Scan")) {
            log.warn("Full table scan detected! Consider adding an index.");
        }
        if (actualTime > 1000) {
            log.warn("Query exceeds 1s threshold: {}ms", actualTime);
        }
    }
}
```

## Fixing N+1 with EntityGraph

```java
// Before: N+1
@Entity
public class Post {
    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY)
    private List<Comment> comments;
}

// After: Use @EntityGraph
@NamedEntityGraph(name = "Post.comments", attributeNodes = @NamedAttributeNode("comments"))
@Entity
public class Post {
    @OneToMany(mappedBy = "post")
    private List<Comment> comments;
}

public interface PostRepository extends JpaRepository<Post, Long> {
    @EntityGraph("Post.comments")
    List<Post> findAll();
}
```
