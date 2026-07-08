# Step-by-Step: Working with Snowflake Data Cloud

1. Sign up for Snowflake trial; note account identifier (e.g., xy12345.us-east-1)
2. Download Snowflake JDBC driver (snowflake-jdbc-x.x.x.jar)
3. Configure connection properties: account, user, password, warehouse, database, schema
4. Create virtual warehouse: CREATE WAREHOUSE dev_wh WITH WAREHOUSE_SIZE='X-SMALL' AUTO_SUSPEND=300 AUTO_RESUME=TRUE
5. Load data: CREATE STAGE, CREATE FILE FORMAT, COPY INTO table FROM @stage
6. Define clustering key: ALTER TABLE orders CLUSTER BY (order_date, customer_id)
7. Query with Time Travel: SELECT * FROM orders AT (TIMESTAMP => 'timestamp'::TIMESTAMP)
8. Clone for testing: CREATE DATABASE dev_sales CLONE sales_db
9. Share data: CREATE SHARE, GRANT SELECT TO SHARE, ALTER SHARE SET ACCOUNTS = partner
