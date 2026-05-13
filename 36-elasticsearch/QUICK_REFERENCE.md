# Quick Reference: Elasticsearch

<div align="center">

![Module](https://img.shields.io/badge/Module-36-blue?style=for-the-badge)
![Topic](https://img.shields.io/badge/Topic-Elasticsearch-green?style=for-the-badge)

**Quick lookup guide for Elasticsearch Query DSL**

</div>

---

## 📋 Query Types

| Category | Purpose |
|----------|---------|
| **Leaf Queries** | Match specific fields (match, term, range) |
| **Compound Queries** | Combine queries (bool, dis_max) |
| **Joining Queries** | Parent-child relationships |
| **Full-text Queries** | Text analysis (match, query_string) |
| **Term Queries** | Exact values (term, terms) |

---

## 🔑 Query DSL

### Match All / Term Queries
```json
// Match all
{ "query": { "match_all": {} } }

// Term query (exact)
{ "query": { "term": { "status": "active" } } }

// Terms query (multiple exact)
{ "query": { "terms": { "status": ["active", "pending"] } } }
```

### Match Queries
```json
// Simple match
{ "query": { "match": { "title": "search terms" } } }

// Match phrase
{ "query": { "match_phrase": { "title": "quick brown fox" } } }

// Multi-match
{ "query": { 
  "multi_match": {
    "query": "search",
    "fields": ["title", "content", "description"]
  }
}}
```

### Boolean Queries
```json
{
  "query": {
    "bool": {
      "must":   [{ "match": { "title": "elasticsearch" } }],
      "filter": [{ "term": { "status": "published" } }],
      "must_not": [{ "term": { "deleted": true } }],
      "should": [{ "match": { "content": "guide" } }],
      "minimum_should_match": 1
    }
  }
}
```

### Range Queries
```json
{
  "query": {
    "range": {
      "price": {
        "gte": 10,
        "lte": 100,
        "lt": 50,
        "gt": 0
      }
    }
  }
}
```

### Nested Queries
```json
{
  "query": {
    "nested": {
      "path": "comments",
      "query": {
        "match": { "comments.text": "awesome" }
      }
    }
  }
}
```

### Term Aggregations
```json
{
  "aggs": {
    "status_counts": {
      "terms": { "field": "status" }
    },
    "avg_price": {
      "avg": { "field": "price" }
    },
    "price_ranges": {
      "range": {
        "field": "price",
        "ranges": [
          { "to": 50 },
          { "from": 50, "to": 100 },
          { "from": 100 }
        ]
      }
    }
  }
}
```

---

## 💻 Java API

### High Level REST Client
```java
RestHighLevelClient client = new RestHighLevelClient(
    RestClient.builder(new HttpHost("localhost", 9200)));

// Search
SearchRequest searchRequest = new SearchRequest("index");
searchRequest.source(new SearchSourceBuilder()
    .query(QueryBuilders.matchQuery("title", "search"))
    .from(0).size(10));

SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);
```

### SearchBuilder
```java
SearchSourceBuilder source = new SearchSourceBuilder();
source.query(QueryBuilders.boolQuery()
    .must(QueryBuilders.matchQuery("title", "elasticsearch"))
    .filter(QueryBuilders.termQuery("status", "published")));

source.sort("price", SortOrder.ASC);
source.from(0).size(20);
source.highlight(new HighlightBuilder().field("content"));
```

### Index Document
```java
IndexRequest request = new IndexRequest("index");
request.id("1");
request.source(jsonData, XContentType.JSON);
IndexResponse response = client.index(request, RequestOptions.DEFAULT);
```

### Bulk Operations
```java
BulkRequest bulkRequest = new BulkRequest();
bulkRequest.add(new IndexRequest("index").id("1").source("field", "value"));
bulkRequest.add(new UpdateRequest("index", "2").doc("field", "newValue"));
bulkRequest.add(new DeleteRequest("index", "3"));

BulkResponse response = client.bulk(bulkRequest, RequestOptions.DEFAULT);
```

---

## 📊 Common Patterns

### Pagination
```json
{
  "query": { "match_all": {} },
  "from": 20,
  "size": 10,
  "sort": [{ "date": "desc" }]
}
```

### Highlight
```json
{
  "query": { "match": { "content": "search" } },
  "highlight": {
    "fields": { "content": {} },
    "pre_tags": ["<em>"],
    "post_tags": ["</em>"]
  }
}
```

### Scoring Control
```json
{
  "query": {
    "function_score": {
      "query": { "match": { "content": "search" } },
      "functions": [
        { "filter": { "term": { "category": "premium" } }, "boost": 2 },
        { "field_value_factor": { "field": "popularity", "factor": 1.2 } }
      ]
    }
  }
}
```

---

## ✅ Best Practices

- Use filters for non-scoring queries
- Avoid wildcards at the beginning
- Use nested queries for objects
- Index only needed fields
- Use pagination for large results

### ❌ DON'T
- Don't use query_string on user input
- Don't search on analyzed fields for exact matches
- Don't ignore index mappings

---

<div align="center">

[Back to Module →](./IMPLEMENTATION.md)

[Take Quizzes →](./PROJECTS.md)

</div>