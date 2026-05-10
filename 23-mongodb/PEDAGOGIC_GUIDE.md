# MongoDB - Pedagogic Guide

---

## Learning Objectives

By the end of this module, learners will be able to:

1. **Understand Document Model**
   - Explain MongoDB vs relational databases
   - Design document structures
   - Choose embedding vs references

2. **Implement CRUD Operations**
   - Use MongoRepository for basic operations
   - Write custom queries with @Query
   - Handle embedded documents

3. **Build Aggregation Pipelines**
   - Create multi-stage pipelines
   - Use $match, $group, $project effectively
   - Implement complex aggregations

4. **Optimize Performance**
   - Design appropriate indexes
   - Use explain for query analysis
   - Implement sharding strategies

---

## Teaching Sequence

### Phase 1: Fundamentals (2-3 hours)

**Topic 1: MongoDB Basics**
- Lecture: Document model and BSON
- Demo: Create database, collection, insert documents
- Exercise: Model an e-commerce catalog

**Topic 2: CRUD Operations**
- Lecture: Basic operations vs SQL
- Demo: Implement CRUD with Spring Data MongoDB
- Exercise: Build product catalog operations

### Phase 2: Document Modeling (2-3 hours)

**Topic 3: Schema Design**
- Lecture: Embedding vs referencing
- Demo: Model order with embedded items
- Exercise: Compare different models

**Topic 4: Indexing**
- Lecture: Index types and usage
- Demo: Create single and compound indexes
- Exercise: Optimize query performance

### Phase 3: Aggregation (3-4 hours)

**Topic 5: Aggregation Pipeline**
- Lecture: Pipeline stages and operators
- Demo: Build sales analytics pipeline
- Exercise: Create revenue dashboard

**Topic 6: Advanced Aggregation**
- Lecture: $facet, $lookup, $graphLookup
- Demo: Implement multi-stage analytics
- Exercise: Build recommendation engine

### Phase 4: Advanced Patterns (2-3 hours)

**Topic 7: Change Streams**
- Lecture: Real-time data processing
- Demo: Implement real-time notifications
- Exercise: Build activity feed

**Topic 8: Transactions**
- Lecture: Multi-document transactions
- Demo: Implement atomic operations
- Exercise: Build order processing

---

## Hands-On Projects

### Mini-Project: Product Catalog
**Duration**: 4-5 hours
**Focus**: Core MongoDB concepts

Learning outcomes:
- Document modeling
- CRUD with MongoRepository
- Custom queries
- Basic aggregations

### Real-World Project: E-Commerce Platform
**Duration**: 12+ hours
**Focus**: Production patterns

Learning outcomes:
- Complex document structures
- Advanced aggregation pipelines
- Change streams
- Transactions
- Schema validation

---

## Assessment Criteria

### Must Have (Core)
- [ ] Create documents with embedded objects
- [ ] Implement CRUD with MongoRepository
- [ ] Use @Query for custom queries
- [ ] Create basic indexes

### Should Have (Intermediate)
- [ ] Build aggregation pipelines
- [ ] Use $lookup for joins
- [ ] Implement text search
- [ ] Optimize with explain()

### Nice to Have (Advanced)
- [ ] Use change streams
- [ ] Implement multi-document transactions
- [ ] Configure sharding
- [ ] Design schema validation rules

---

## Common Pitfalls

1. **Over-embedding**
   - Don't embed everything
   - Consider document size limits (16MB)
   - Watch for update anomalies

2. **Missing Indexes**
   - Always check query patterns
   - Use compound indexes wisely
   - Monitor index size

3. **Aggregation Performance**
   - Limit early with $match
   - Avoid $unwind on large arrays
   - Use $facet wisely

4. **Transaction Overhead**
   - Transactions are expensive
   - Use only when necessary
   - Consider other patterns first

---

## Discussion Questions

1. When would you choose MongoDB over PostgreSQL?
2. How do you decide between embedding and referencing?
3. What are the limitations of MongoDB aggregations?
4. How do you handle schema evolution in production?

---

## Extension Activities

1. **Performance Challenge**: Query response under 100ms
2. **Scaling Challenge**: Handle 10M documents
3. **Design Challenge**: Model a social network

---

## Additional Resources

- MongoDB: The Definitive Guide
- Practical MongoDB Aggregation
- Spring Data MongoDB Reference
- MongoDB Schema Design Patterns

