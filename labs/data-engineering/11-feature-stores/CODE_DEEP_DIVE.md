# Code Deep Dive: Feature Engineering

## User Feature Computation
```java
public class FeatureEngineeringPipeline {
    public Dataset<Row> computeUserFeatures(Dataset<Row> transactions) {
        return transactions
            .groupBy("user_id")
            .agg(
                count("*").as("total_orders"),
                sum("amount").as("total_revenue"),
                avg("amount").as("avg_order_value"),
                stddev("amount").as("std_order_value"),
                max("amount").as("max_order_value"),
                datediff(current_date(), max("order_date")).as("days_since_last")
            );
    }
}
```

## Serving API
```java
@RestController
@RequestMapping("/api/features")
public class FeatureController {
    @GetMapping("/online/{type}/{id}")
    public ResponseEntity<Map<String, Object>> getFeatures(
            @PathVariable String type, @PathVariable String id,
            @RequestParam List<String> features) {
        return ResponseEntity.ok(featureStore.getFeatures(type, id, features));
    }
}
```
