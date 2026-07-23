# Mock Interview: APEX Performance (Lab 08)

**Role:** Oracle APEX Developer (Senior)  
**Duration:** 40 minutes  
**Difficulty Progression:** Easy → Medium → Hard

---

## Round 1: Easy (5-10 minutes)

**Interviewer:** What are the main factors affecting APEX application performance?

**Candidate:** APEX performance is primarily influenced by:
1. **Database query performance:** Poorly optimized SQL, missing indexes, inefficient joins
2. **Session state management:** Unnecessary session state operations, large session data
3. **Region processing:** Complex PL/SQL computations during page rendering
4. **Network latency:** Page size, number of HTTP requests, CDN usage
5. **APEX configuration:** Memory settings, caching configuration
6. **Theme and templates:** Large CSS/JS files, unminified resources
7. **ORDS configuration:** Connection pool size, request timeout

The rule: **95% of APEX performance issues are database-related. Always optimize SQL first.**

**Interviewer:** How do you use the APEX Debug mode to diagnose performance?

**Candidate:** APEX Debug mode provides detailed timing information for page rendering:
1. Enable Debug: Append `?p_debug=YES` to URL or enable in developer toolbar
2. Debug log shows each phase with timing:
   - Page rendering (template, regions, items)
   - Page processing (computations, validations, processes, branches)
   - SQL execution per region
3. Key timings to look for:
   - Long SQL execution times (need query optimization)
   - Slow region rendering (complex logic)
   - Many sequential SQL calls (N+1 pattern)
4. Use **View Log** to see detailed execution plan for each query

---

## Round 2: Medium (10-15 minutes)

**Interviewer:** What caching strategies are available in APEX?

**Candidate:** APEX provides multiple caching layers:

1. **Page Cache:** Cache entire page HTML for anonymous users
   - Configure in Page Attributes → Cache → Cache Mode
   - Options: Cached (for all), Cached (based on user), Dynamic (not cached)

2. **Region Cache:** Cache individual region output
   - Region → Cache → Cache Type (Session State, Session Entry, Application Entry)
   - TTL: Hours, Days, Minutes
   - Conditional caching: Cache by user role, by item value

3. **Application Cache (Collections):** Store and retrieve frequently used data
   - Cache LOVs, reference data, configuration values
   - Less database round-trips

4. **Database Result Cache:** Oracle Database's built-in result caching
   - `SELECT /*+ RESULT_CACHE */ * FROM reference_data`
   - Shared across sessions, invalidated on data changes

5. **ORDS Cache:** HTTP response caching for REST endpoints
   - `Cache-Control` headers
   - ETag-based conditional requests

6. **CDN:** Static file caching (images, CSS, JS, theme files)
   - Serve from CloudFront, Akamai, or OCI CDN

**Interviewer:** When would you use APEX Collections for performance?

**Candidate:** APEX Collections are in-memory session-level storage that can dramatically improve performance by reducing database calls. Use cases:
- **Shopping cart:** Cart items stored in collection, no DB writes until checkout
- **Recently viewed items:** Client-side tracking (write to collection on page view)
- **Bulk operations:** Store selected record IDs, process as batch
- **Report temporary data:** Store intermediate results for multi-step processing
- **Wizard state:** Store multi-form data across wizard steps

Collections live in `APEX_COLLECTIONS` table (session-scoped) and are much faster than repeated queries because they're indexed by session ID and collection name.

---

## Round 3: Hard (15-20 minutes)

**Interviewer:** Your APEX page that displays a customer dashboard (with charts, recent orders, payment history) takes 12 seconds to load. Walk through your performance optimization process.

**Candidate:** 

**Step 1 — Diagnose:**
Enable Debug mode and look at timings:
```
Phase: Render Page (12.3s)
├── Region: Charts (3.2s)
│   └── Query: sales_by_month — 2.8s (FULL TABLE SCAN on 10M rows)
├── Region: Recent Orders (4.1s)
│   └── Query: recent_orders — 3.9s (NESTED LOOP, no index on status column)
├── Region: Payment History (2.8s)
│   └── Query: payment_history — 2.5s (missing date index)
└── Page Processing (2.2s)
    └── 3 computations + 2 processes (unnecessary for read-only page)
```

**Step 2 — Optimize queries:**
```sql
-- Original (3.2s):
SELECT TO_CHAR(order_date, 'YYYY-MM') as month, SUM(total_amount)
FROM orders
WHERE order_date >= ADD_MONTHS(SYSDATE, -12)
GROUP BY TO_CHAR(order_date, 'YYYY-MM')
ORDER BY month;

-- Optimized (0.1s) — added composite index (order_date, total_amount)
CREATE INDEX idx_orders_date_amount ON orders(order_date, total_amount);
```

**Step 3 — Add caching:**
```sql
-- Use result cache for the dashboard (data changes infrequently)
SELECT /*+ RESULT_CACHE */ 
    TO_CHAR(order_date, 'YYYY-MM') as month, SUM(total_amount)
FROM orders
WHERE order_date >= ADD_MONTHS(SYSDATE, -12)
GROUP BY TO_CHAR(order_date, 'YYYY-MM')
ORDER BY month;
```

**Step 4 — Remove unnecessary processing:**
- For a read-only dashboard, set all computations and processes to conditionally execute only when "Submit" clicked
- Or better: create separate report region and disable page processing for initial load

**Step 5 — Defer non-critical regions:**
- Payment history: Load via AJAX after page render
- Use `Lazy Loading` region attribute: Render the HTML placeholder, load data via AJAX callback

**Step 6 — Materialized view:**
```sql
CREATE MATERIALIZED VIEW mv_customer_dashboard
REFRESH COMPLETE ON SCHEDULE EVERY 1 HOUR
AS
SELECT customer_id, order_month, total_spent, order_count
FROM orders JOIN customers ON ...
```

**Result:** Page load time reduced from 12s to ~1.5s (0.3s initial render + 1.2s deferred AJAX).

**Interviewer:** How do you handle large datasets in Interactive Grids for optimal performance?

**Candidate:** For Interactive Grids with 100K+ rows:
1. **Pagination:** Force pagination (not virtual scrolling for huge datasets). Set "Paginate" = True.
2. **Server-side processing:** Enable "Where Clause" optimization, server-side aggregation
3. **Column filtering:** Enable "Column Filtering" for client-side using data from DB
4. **Limited initial query:** Start with "Row Limiting Clause" or quick WHERE filter
5. **Local vs remote filtering:** For >100K rows, ensure ALL filtering is server-side
6. **Avoid complex computations:** Move to database queries, not APEX computations
7. **Materialized paths:** Pre-compute hierarchical data in materialized views
8. **Partitioning:** For tables with millions of rows, use Oracle Partitioning
9. **Index strategy:** 
   - Index all columns in WHERE clauses
   - Use function-based indexes for transformed columns (UPPER(name))
   - BITMAP indexes for low-cardinality columns (status, region)

---

## Interviewer Feedback

**Strengths:** Excellent diagnostic process, practical optimization techniques, strong understanding of APEX caching  
**Areas to Improve:** Could discuss APEX 23.x declarative performance improvements  
**Verdict:** Strong Hire

---

*Lab 08 MOCK_INTERVIEW.md — Part of Oracle APEX Academy Interview Preparation*
