# History: Connection Pooling

- **1990s** – Applications open/close connections per request (pre-pooling era)
- **1999** – JDBC 2.0 Optional Package introduces `ConnectionPoolDataSource`
- **2002** – Jakarta Commons DBCP (Tomcat DBCP) becomes popular
- **2005** – c3p0 gains traction with its automatic pool sizing
- **2008** – BoneCP created in response to DBCP performance issues
- **2010** – Tomcat 7 upgrades to Tomcat DBCP 2
- **2012** – Brett Wooldridge creates HikariCP ("light" in Japanese)
- **2014** – HikariCP becomes fastest pool, widely adopted
- **2017** – Spring Boot 2.0 makes HikariCP the default connection pool
- **2019** – HikariCP 3.4: Java 8+, micro-optimizations
- **2023** – HikariCP 5.x: Java 17+, Virtual Thread support
