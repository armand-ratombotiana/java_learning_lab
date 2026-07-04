# Why Query Optimization Exists

Query optimization exists because naive database queries can be orders of magnitude slower than optimized ones. A query returning 100 rows can execute in <1ms with proper indexing or take 10+ seconds without one. As data grows, the gap widens exponentially.

Databases cannot automatically optimize all queries because they lack application-level knowledge (which joins are selective, which columns are frequently filtered, which access patterns dominate). Query optimization is the developer's responsibility to understand how the database executes queries and guide it toward efficient plans.
