# Common Mistakes with Snowflake Data Cloud

1. Warehouse Overprovisioning: Using X-Large for simple queries; start with X-Small and scale based on actual needs
2. No Auto-Suspend: Warehouse runs 24/7 burning credits; set AUTO_SUSPEND = 300 seconds for dev environments
3. Ignoring Clustering: Large tables without clustering keys cause full scans; monitor clustering depth for >1TB tables
4. Overusing Time Travel: 90-day retention on high-churn tables incurs significant costs; use shorter retention for transient data
5. No Zero-Copy Clone for Testing: Testing schema changes on production risks data loss; use CLONE for safe testing
