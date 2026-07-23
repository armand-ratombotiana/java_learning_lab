# LEETCODE_SOLUTIONS — MongoDB

## NoSQL / Aggregation Solutions

| LeetCode Concept | MongoDB Equivalent | Notes |
|-----------------|--------------------|-------|
| GROUP BY | `$group` stage | Aggregation pipeline |
| HAVING | `$match` after `$group` | Filter aggregated results |
| JOIN | `$lookup` | Foreign collection join |
| ORDER BY | `$sort` | Sort documents |
| LIMIT/OFFSET | `$limit` / `$skip` | Pagination |
| WHERE | `$match` | Filter documents |
| SELECT fields | `$project` | Field selection |
| Window functions | `$setWindowFields` | MongoDB 5.0+ |

### Example: TOTAL GROUP BY Equivalent
```javascript
db.orders.aggregate([
  { $match: { status: "completed" } },
  { $group: { _id: "$customerId", total: { $sum: "$amount" } } },
  { $match: { total: { $gte: 1000 } } },
  { $sort: { total: -1 } }
])
```
