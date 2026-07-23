# Mock Interview: Multi-Model Databases (Lab 12)

**Role:** Database Architect (Senior)  
**Duration:** 35 minutes  
**Difficulty Progression:** Easy → Medium → Hard

---

## Round 1: Easy (5-10 minutes)

**Interviewer:** What are multi-model databases and what are examples?

**Candidate:** Multi-model databases support multiple data models (relational, document, graph, key-value) in a single database engine. Examples:
- **Oracle Database 23c:** Relational + JSON + Graph (property graph, RDF) + Spatial + XML
- **PostgreSQL:** Relational + JSON (JSONB) + key-value (hstore) + full-text + geospatial (PostGIS)
- **Azure Cosmos DB:** Document + Graph (Gremlin) + Key-value + Column-family + Table
- **ArangoDB:** Document + Graph + Key-value
- **Redis Stack:** Key-value + JSON + Graph + Search + Time Series

**Interviewer:** What would you choose between Oracle 23c's JSON-relational duality and MongoDB for a mixed workload?

**Candidate:** Oracle 23c JSON-relational duality views allow the same data to be accessed as both relational tables and JSON documents. Choose Oracle when:
- You already have Oracle infrastructure
- Need strong consistency and ACID transactions
- Need both relational and document access to same data
- Complex querying across relational and JSON data

Choose MongoDB when:
- Document-first design is preferred
- Schema flexibility is critical
- Horizontal scaling (sharding) is main requirement
- Starting greenfield with limited budget

---

## Round 2: Medium (10-15 minutes)

**Interviewer:** How does Oracle 23c's JSON-relational duality work?

**Candidate:** A duality view creates a virtual document-like representation of relational data:
```sql
CREATE JSON RELATIONAL DUALITY VIEW customers_dv AS
SELECT JSON {
    'customerId'  : c.customer_id,
    'name'        : c.name,
    'email'       : c.email,
    'orders'      : [ SELECT JSON {
        'orderId'    : o.order_id,
        'orderDate'  : o.order_date,
        'total'      : o.total
    } FROM orders o WHERE o.customer_id = c.customer_id ],
    'addresses'   : [ SELECT JSON {
        'addressId'  : a.address_id,
        'type'       : a.type,
        'street'     : a.street,
        'city'       : a.city
    } FROM addresses a WHERE a.customer_id = c.customer_id ]
}
FROM customers c;
```

You can query with SQL or with JSON dot notation. Updates to the view automatically propagate to underlying tables.

---

## Round 3: Hard (15-20 minutes)

**Interviewer:** Design a data model using multi-model capabilities for a product catalog that needs: relational queries (inventory, pricing), document flexibility (product specifications vary by category), and graph traversal (recommendations based on product relationships).

**Candidate:** 

**Oracle 23c multi-model design:**

**Relational model (regulatory, inventory, pricing):**
```sql
CREATE TABLE products (
    product_id NUMBER PRIMARY KEY,
    sku VARCHAR2(50) UNIQUE NOT NULL,
    name VARCHAR2(200) NOT NULL,
    category_id NUMBER REFERENCES categories(category_id),
    base_price NUMBER(10,2),
    current_inventory NUMBER,
    status VARCHAR2(20)
);

CREATE TABLE categories (
    category_id NUMBER PRIMARY KEY,
    name VARCHAR2(100),
    parent_category_id NUMBER REFERENCES categories(category_id)
);
```

**Document model (flexible specifications):**
```sql
-- Product specifications as JSON (varies by category)
ALTER TABLE products ADD (specifications JSON);

-- Query example: Find laptops with 16GB+ RAM
SELECT p.name, p.base_price
FROM products p
WHERE p.category_id = 'ELECTRONICS'
AND JSON_EXISTS(p.specifications, '$?(@.ram >= 16 && @.storageType == "SSD")');
```

**Graph model (recommendations):**
```sql
-- Property graph for product relationships
CREATE PROPERTY GRAPH product_graph
  VERTEX TABLES (products, categories)
  EDGE TABLES (
    related_products AS RELATED_PRODUCTS
      SOURCE KEY(product_id) REFERENCES products(product_id)
      DESTINATION KEY(related_product_id) REFERENCES products(product_id)
  );

-- Graph query: "Customers who bought this also bought..."
SELECT p1.name AS product, 
       COLLECT(p3.name ORDER BY p3.popularity DESC) AS recommendations
FROM product_graph
MATCH (p1) - [related] -> (p2) <- [related] - (p3)
WHERE p1.product_id = :input_product_id
  AND p3.product_id != p1.product_id
GROUP BY p1.name;
```

---

## Interviewer Feedback

**Strengths:** Excellent multi-model understanding, practical Oracle 23c duality design, combined models effectively  
**Areas to Improve:** Could discuss ArangoDB's multi-model capabilities as comparison  
**Verdict:** Strong Hire

---

*Databases Lab 12 MOCK_INTERVIEW.md*
