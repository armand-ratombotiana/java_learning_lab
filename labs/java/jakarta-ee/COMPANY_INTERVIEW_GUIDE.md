# Company Interview Guide: Jakarta EE / Java EE

This guide covers which companies ask Jakarta EE questions, their specific technology stacks, interview processes, and sample questions.

---

## Table of Contents

1. [Oracle](#1-oracle)
2. [IBM](#2-ibm)
3. [Red Hat](#3-red-hat)
4. [SAP](#4-sap)
5. [Consulting Firms (Capgemini, Accenture, TCS, Infosys)](#5-consulting-firms)
6. [Banking & Financial Services](#6-banking--financial-services)
7. [Insurance Companies](#7-insurance-companies)
8. [Telecommunications](#8-telecommunications)
9. [Government & Defense](#9-government--defense)
10. [Product Companies (Adobe, Salesforce, SAP)](#10-product-companies)
11. [Cloud Providers (AWS, Azure, GCP — Jakarta EE roles)](#11-cloud-providers)
12. [General Interview Strategy by Seniority Level](#12-general-interview-strategy)

---

## 1. Oracle

### Company Profile

- **Products**: WebLogic Server (application server), EclipseLink (JPA provider), Oracle ADF (Application Development Framework), Oracle Tuxedo, Coherence (distributed cache).
- **Role types**: WebLogic Administrator, Java EE Developer (ADF-centric), Middleware Engineer, Oracle Cloud Java Developer.

### Interview Process

| Round | Focus | Duration |
|-------|-------|----------|
| Phone screen | General Java + experience | 30–45 min |
| Technical round 1 | Core Java + Jakarta EE fundamentals | 60 min |
| Technical round 2 | WebLogic-specific + JPA/EclipseLink deep dive | 60 min |
| System design | Architecture with Oracle products | 60 min |
| Manager round | Behavioral + leadership | 45 min |

### Core Topics Tested

#### WebLogic Server
- Domain configuration (Admin Server, Managed Servers, clusters)
- Deployment descriptors: `weblogic.xml`, `weblogic-ejb-jar.xml`, `weblogic-application.xml`
- Data source configuration (Active GridLink for RAC, Multi Data Sources)
- JMS configuration (JMS servers, destinations, connection factories, store-and-forward)
- JTA transaction recovery, XA, and the WebLogic Transaction Manager
- Clustering: replication groups, session persistence, whole server migration
- Security realms, authentication providers (LDAP, Active Directory, SAML)
- Node Manager, start scripts, and administration console

#### EclipseLink (JPA Provider)
- EclipseLink-specific extensions: `@DescriptorCustomizer`, `@AdditionalCriteria`, `@Mutable`
- Cache coordination across cluster (invalidation, replication, distributed)
- Inheritance strategies — EclipseLink optimizations
- Query hints (`eclipselink.query.timeout`, `eclipselink.batch`, `eclipselink.join-fetch`)
- Historical sessions, flashback queries
- EJB JPA integration in WebLogic

#### Oracle ADF
- ADF Business Components (Entity Objects, View Objects, Application Modules)
- ADF binding layer (PageDef files, iterator bindings, attribute bindings)
- ADF task flows (bounded vs unbounded, router, method call activities)
- ADF Faces components (rich client components, skinning, partial page rendering)
- Security with ADF: ADF Security, permission grants, programmatic authorization

### Sample Interview Questions

1. "How does WebLogic handle session persistence for HTTP sessions? Compare in-memory replication vs JDBC persistence vs Coherence-based persistence."
2. "Explain the WebLogic classloader hierarchy. How do you avoid ClassCastException between application classes?"
3. "What is the difference between `weblogic-ejb-jar.xml` deployment descriptor and standard `ejb-jar.xml`? Give specific examples of WebLogic-specific EJB features."
4. "How does EclipseLink cache coordination work in a WebLogic cluster? What happens when a local cache is invalidated?"
5. "You have a high-volume OLTP application on WebLogic. How do you tune the thread pool, JDBC connection pool, and JMS connection factory?"
6. "What is XA transaction recovery in WebLogic? Explain the process: non-persistent vs persistent stores, cron jobs, and the recovery service."
7. "Design a multi-data source configuration for Oracle RAC. How does GridLink handle planned and unplanned database outages?"

### Preparation Strategy

- Download WebLogic and deploy a simple EJB + JPA application.
- Read Oracle's WebLogic Performance and Tuning Guide.
- Practice configuring JMS, data sources, and security realms via both console and WLST (WebLogic Scripting Tool).
- Study the `weblogic.xml` deployment descriptor elements.

---

## 2. IBM

### Company Profile

- **Products**: WebSphere Application Server (traditional), WebSphere Liberty / OpenLiberty, IBM HTTP Server, MQ Series, CICS.
- **Role types**: WebSphere Administrator, Java EE Developer (WebSphere), Integration Developer, Cloud Pak for Applications.

### Interview Process

| Round | Focus | Duration |
|-------|-------|----------|
| Initial screen | Background, core Java | 30–45 min |
| Technical interview | WebSphere, EJB, JMS, transactions | 60–90 min |
| Architecture / design | Distributed systems with WebSphere products | 60 min |
| Customer-facing round (for consulting roles) | Communication, presentation | 45 min |

### Core Topics Tested

#### WebSphere Traditional
- Deployment Manager (DMGR), Node Agent, Application Server architecture
- Cell, node, server hierarchy
- SIBus (Service Integration Bus) for messaging
- WebSphere classloader policies (PARENT_FIRST, PARENT_LAST, APPLICATION)
- Session management and session persistence
- JVM tuning: heap size, GC policy, thread pool
- IBM HTTP Server plugin configuration

#### OpenLiberty / WebSphere Liberty
- Server.xml configuration (features, variables, application)
- Features: `servlet-6.0`, `jpa-3.0`, `cdi-4.0`, `restfulWS-3.0`
- Zero-migration architecture
- MicroProfile support (config, fault tolerance, health, metrics, open tracing)
- Liberty collective (controller, member, metrics)
- Docker/OpenShift deployment

#### Java EE Topics
- EJB 3.x (stateless, stateful, singleton, MDB)
- JMS and MQ Series integration
- JTA transactions and distributed transaction recovery
- JPA with OpenJPA or EclipseLink (IBM uses both)

### Sample Interview Questions

1. "Explain the WebSphere classloader hierarchy for an enterprise application. What happens when multiple versions of the same library are used by different modules?"
2. "How does OpenLiberty's 'zero-migration' architecture work? How can you upgrade the runtime without changing your application?"
3. "Describe the WebSphere Network Deployment topology: cells, nodes, deployment managers. How do you perform in-place upgrades without downtime?"
4. "What is SIBus and how does it differ from a traditional JMS provider? How do you configure message-driven beans against SIBus destinations?"
5. "Explain how you would deploy a Jakarta EE application across multiple Liberty servers in a collective for high availability."
6. "How do you debug a classloader issue in WebSphere? What tools does IBM provide (IBM Support Assistant, memory dumps, heap analysis)?"
7. "Your EJB application suffers from performance issues in WebSphere. How do you approach analysis and tuning?"

### Preparation Strategy

- Download OpenLiberty and deploy a simple REST + JPA application (it's very fast to start).
- Read the OpenLiberty guide on MicroProfile and Jakarta EE features.
- For traditional WebSphere roles, understand the admin console, wsadmin scripts, and deployment manager architecture.
- Study the IBM Knowledge Center for WebSphere classloaders and SIBus.

---

## 3. Red Hat

### Company Profile

- **Products**: JBoss Enterprise Application Platform (EAP), WildFly, Hibernate, Narayana (transaction manager), Infinispan (distributed cache).
- **Role types**: JBoss Administrator, Java EE Developer (Red Hat stack), Middleware Consultant, Solutions Architect.

### Interview Process

| Round | Focus | Duration |
|-------|-------|----------|
| Technical phone | Core Java, Hibernate, basic Jakarta EE | 45–60 min |
| Coding round | Live coding or take-home | 60–90 min |
| Deep dive | Hibernate internals, JBoss configuration | 60 min |
| System design | Architecture with JBoss products | 60 min |

### Core Topics Tested

#### JBoss / WildFly
- Standalone vs domain mode
- Management CLI (`jboss-cli.sh`/`.bat`)
- Subsystems: datasources, messaging-activemq, undertow, ejb3, jpa
- Logging configuration (log levels, handlers, formatters)
- Security: Elytron (new security subsystem in WildFly / JBoss EAP 7+)
- Transaction subsystem with Narayana
- Deployment: WAR, EAR, exploded deployments

#### Hibernate (deeper than typical JPA)
- Session management, first-level and second-level caching
- Hibernate-specific optimizations: batch fetching, `@BatchSize`, `@Fetch`
- Dirty checking and how Hibernate detects changes
- `Session.flush()` and `Session.clear()` — when to use them
- Hibernate interceptor and event system (`@PreUpdate`, `@PostLoad`, etc.)
- N+1 query detection and solution strategies
- Hibernate types: custom types, `UserType`, `CompositeUserType`
- Hibernate envers (auditing/versioning)

#### CDI and Weld
- Weld is the CDI RI — deep questions about bean discovery, alternatives, stereotypes
- `@Alternative`, `@Specializes`, `@Priority`
- Portable extensions (SPI for extending CDI containers)
- Events, observers, transactions

### Sample Interview Questions

1. "Explain the difference between Hibernate's Session.get() and Session.load(). Under what circumstances would `load()` throw an exception?"
2. "How does Hibernate's dirty checking work internally? What is the role of the snapshot in the persistence context?"
3. "Describe the JBoss EAP domain mode. How does the domain controller propagate configuration changes to slave nodes?"
4. "What is the WildFly Elytron security subsystem? How does it differ from the legacy PicketBox security in JBoss EAP 6?"
5. "Explain Hibernate's second-level cache regions and the different concurrency strategies (READ_ONLY, READ_WRITE, NONSTRICT_READ_WRITE, TRANSACTIONAL)."
6. "Your JBoss application has a memory leak. Walk me through how you'd diagnose it using JBoss management tools."
7. "How would you configure an XA transaction with JBoss connecting to two different databases using Narayana?"

### Preparation Strategy

- Download WildFly and deploy an application — it's one of the easiest Java EE servers to set up.
- Practice the Management CLI commands for datasource, messaging, and security configuration.
- Read the Hibernate documentation on caching, fetching strategies, and transactions.
- For JBoss EAP, focus on domain mode clustering.

---

## 4. SAP

### Company Profile

- **Products**: SAP NetWeaver Application Server Java, SAP Cloud Platform (Neo / Cloud Foundry), SAP JCo (SAP Java Connector).
- **Role types**: SAP Java Developer, SAP PI/PO Developer (Java mapping), SAP Cloud Platform Developer.

### Interview Process

| Round | Focus | Duration |
|-------|-------|----------|
| Initial screen | Core Java + SAP basics | 30 min |
| Technical | NetWeaver AS Java, EJB, JPA | 60 min |
| SAP integration | JCo, RFC, BAPI, IDoc | 60 min |
| Architectural | Cloud platform extension | 45 min |

### Core Topics Tested

#### SAP NetWeaver AS Java
- SAP-specific deployment descriptors (`web-j2ee-engine.xml`)
- Visual Composer, Web Dynpro Java (legacy)
- SAP J2EE Engine architecture (dispatchers, server nodes, central services)
- Java dictionary / OpenSQL for JPA
- SAP-specific cluster services (session failover, JMS clustering)

#### SAP Integration
- JCo (SAP Java Connector) for RFC calls from Java to ABAP
- BAPI invocation from EJBs
- IDoc processing (inbound/outbound) with JMS + MDB
- SAP Cloud Platform Cloud Foundry — Java apps on SAP BTP

### Sample Interview Questions

1. "How does SAP NetWeaver AS Java cluster differ from a standard Jakarta EE cluster?"
2. "What is JCo? Describe the architecture: JCo Server vs JCo Destination."
3. "You need to call an ABAP BAPI from a Jakarta EE application. Describe the steps and the JCo configuration required."
4. "How would you process incoming IDocs in a Jakarta EE application using JMS message-driven beans?"
5. "Explain the SAP Cloud Platform extension pattern: extending an S/4HANA system with a Java microservice."

---

## 5. Consulting Firms

### Capgemini, Accenture, TCS, Infosys, Wipro, Cognizant, HCL, Tech Mahindra

### Company Profile

- **Engagement types**: Legacy application maintenance, migration (Java EE to Spring Boot / cloud), greenfield enterprise applications, digital transformation.
- **Role types**: Senior Java Developer, Technical Architect, Solution Architect, Modernization Lead.

### Interview Process

| Round | Focus | Duration |
|-------|-------|----------|
| HR screen | Generic, rate expectations | 20 min |
| Technical round 1 | Core Java + Jakarta EE deep dive | 60 min |
| Technical round 2 | Architecture, migration, modernization | 60 min |
| Manager / client round | Domain knowledge, communication | 45 min |
| Client interview (for consulting roles) | Specific to client tech stack | 60 min |

### Core Topics Tested

#### Architectural Knowledge
- Monolithic vs microservice architecture assessment
- Strangler pattern for legacy migration
- When to re-platform vs re-architect vs rewrite
- Cloud migration strategies (rehost, replatform, refactor, repurchase, retire, retain)
- Technology stack evaluation (AS-IS vs TO-BE)

#### Jakarta EE Topics (Maintenance Focus)
- EJB (especially stateful and session facades)
- JMS, MQ Series, and integration patterns
- JPA with legacy Hibernate mapping files
- Servlet filters and listeners
- `web.xml`, `ejb-jar.xml`, `application.xml` deployment descriptors

#### Modernization Topics
- Jakarta EE 8 → Jakarta EE 10 migration (`javax.*` → `jakarta.*`)
- Migrating EJBs to CDI + @Transactional
- Replacing JMS MDBs with Spring JMS / Kafka
- Decomposing a monolith: bounded contexts, domain events, CQRS
- Containerization of Java EE applications (Docker, OpenShift)

### Sample Interview Questions

1. "You have a Java EE 5 application with 300 EJBs, deployed on WebSphere, using Oracle DB. The client wants to migrate to the cloud. Walk me through your assessment and recommendation."
2. "Describe the strangler fig pattern for slowly migrating a legacy Java EE application to a new architecture. What are the risks?"
3. "A client's application has performance issues with JPA — N+1 queries, slow reads. How do you analyze and fix this without rewriting the entire persistence layer?"
4. "Compare three modernization strategies: rehost, replatform, refactor. When would you recommend each for a Java EE application?"
5. "You need to containerize a legacy Java EE application (WildFly, Oracle RAC, JMS). What challenges do you anticipate and how do you solve them?"
6. "How would you gradually migrate a monolithic EJB-based application to Spring Boot microservices, keeping the system operational during the transition?"

### Preparation Strategy

- Be able to discuss both Jakarta EE and Spring ecosystems fluently.
- Practice the "consultant's answer": give the trade-offs, not just the solution.
- Study migration patterns: strangler fig, anti-corruption layer, event-driven migration.
- Be ready to draw architecture diagrams (whiteboard or digital).

---

## 6. Banking & Financial Services

### JPMorgan Chase, Goldman Sachs, Morgan Stanley, Citigroup, Bank of America, Barclays, UBS, Credit Suisse, Deutsche Bank

### Company Profile

- **Tech stacks**: Mix of Java EE (WebSphere, WebLogic) and modern Spring Boot. Heavy use of JMS (IBM MQ, Solace, Tibco), distributed transactions, real-time processing.
- **Role types**: Java Developer (Trading Systems), Risk Systems Developer, Payment Integration Engineer, Core Banking Developer.

### Interview Process

| Round | Focus | Duration |
|-------|-------|----------|
| Technical phone | Java + JMS + transactions | 45–60 min |
| Coding challenge | Often HackerRank or similar | 60–90 min |
| On-site technical | Deep dive into JMS, JTA, concurrency | 60 min |
| System design | High-throughput trading or payment system | 60 min |
| Behavioral | Team fit, working under pressure | 45 min |

### Core Topics Tested

#### JMS and Messaging (Critical)
- IBM MQ, Solace, Tibco EMS, Kafka
- JMS 1.1 vs JMS 2.0 API differences
- Persistent vs non-persistent messages
- Durable vs non-durable subscriptions
- Message selectors (`JMSCorrelationID`, custom properties)
- Dead letter queues and poison message handling
- Message ordering guarantees (or lack thereof)
- XA transactions across JMS and JPA

#### High-Volume / Low-Latency
- Connection pooling for JMS and JPA
- Thread safety and re-entrant locking
- Async processing with MDBs and thread pools
- Caching strategies (distributed cache for reference data)
- Avoiding GC pauses (object pooling, off-heap buffering)
- Event-driven architecture with messaging

#### Risk and Compliance (Domain Knowledge)
- Trade lifecycle (order → execution → settlement)
- Market data processing
- Regulatory reporting
- Pricing and risk calculations
- Reconciliation systems

### Sample Interview Questions

1. "Design a payment processing system that guarantees exactly-once semantics for JMS messages. Consider the failure scenarios: consumer crash, broker failure, DB failure."
2. "You have a JMS queue connected to IBM MQ. Your MDB processes orders and persists them to Oracle. If the database is slow, JMS messages begin accumulating. How do you design for backpressure?"
3. "Explain how XA transactions work with JMS and JPA. What happens if the transaction manager crashes after the prepare phase?"
4. "Your trading system requires sub-millisecond JMS message delivery. What configurations at the JMS provider, application server, and application level affect latency?"
5. "How do you handle poison messages on an IBM MQ queue? Walk me through the full flow: detection → redelivery → backout queue → alerting."
6. "Design a distributed transaction system for a funds transfer between two banks. Each bank uses its own Jakarta EE application, and the transfer must be atomic."

### Preparation Strategy

- Deep dive into JMS — queues, topics, durable subscriptions, message selectors. This is the #1 topic for banking interviews.
- Understand JTA, XA, two-phase commit, transaction recovery, and the `UserTransaction` API.
- Study at least one JMS provider: IBM MQ is the most common in banking, followed by Solace and Tibco.
- Read about Apache Kafka — many banks are migrating or adding Kafka alongside traditional JMS.

---

## 7. Insurance Companies

### Allianz, AXA, MetLife, Prudential, AIG, Liberty Mutual, State Farm, Zurich Insurance

### Company Profile

- **Tech stacks**: Heavier Java EE legacy (WebSphere, WebLogic), slow adoption of newer tech. Strong focus on integration (JMS, MQ) and rules engines.
- **Role types**: Java EE Developer, Integration Architect, Policy Administration Developer, Claims Systems Developer.

### Core Topics

- Policy administration systems (often COTS with Java EE customization)
- Claims processing workflow (BPEL, state machines)
- BPM integration with JBPM or IBM BPM
- Integration with mainframes via JMS
- Data-heavy batch processing (Spring Batch or custom EJB batch)

### Sample Questions

1. "A policy administration system processes millions of policies daily. How do you design the batch processing architecture using Jakarta EE?"
2. "Integration between a claims system (Java EE) and a mainframe policy system uses MQ. Describe how you'd handle message format transformations, error handling, and reconciliation."

---

## 8. Telecommunications

### AT&T, Verizon, T-Mobile, Vodafone, BT, Deutsche Telekom

### Company Profile

- **Tech stacks**: Heavy investment in SOA with Java EE, JMS, web services. Gradual migration to cloud-native.
- **Role types**: BSS/OSS Developer, Integration Engineer, Service Activation Developer.

### Core Topics

#### BSS/OSS (Business/Operations Support Systems)
- Service activation and orchestration
- Inventory management
- Billing and mediation systems
- Network element integration (TL1, SNMP, CORBA — but Java EE wraps these)
- Subscriber data management (SDM, HLR/HSS integration)

#### SOA and Integration
- Enterprise Service Bus (ESB) patterns with Java EE
- MDBs for event-driven service activation
- JMS for northbound and southbound interfaces
- Web services (SOAP, REST) for customer-facing portals
- TM Forum Frameworx standards (eTOM, SID, TAM)

### Sample Questions

1. "A service activation request comes in via JMS. The activation workflow involves 3 external systems, and total response time should be under 2 seconds. Design the EJB architecture."
2. "Your telco has a 15-year-old Java EE provisioning system. How would you modernize it while maintaining 99.999% uptime during the transition?"

---

## 9. Government & Defense

### Companies: Lockheed Martin, Raytheon, Northrop Grumman, CGI Federal, General Dynamics, SAIC, Leidos

### Company Profile

- **Tech stacks**: Java EE (older versions), often heavily customized, strict security requirements. Slow adoption of new standards.
- **Role types**: Software Engineer (Java), Systems Integrator, Security Engineer.

### Core Topics

- Jakarta EE security (JAAS, role-based access control)
- Multi-level security (MLS) and cross-domain solutions
- Deployment in classified environments (air-gapped, no internet)
- Interoperability with legacy systems (Ada, CORBA, C++ → bridged via JNI/JMS)
- PKI, certificate-based authentication with Java EE

### Sample Questions

1. "How do you configure mutual TLS authentication between a Java EE client and server in a closed network with no access to public CA?"
2. "Your application needs to handle data at different classification levels (Top Secret, Secret, Unclassified) within the same WebSphere instance. How do you design the security architecture?"

---

## 10. Product Companies

### Adobe, Salesforce, ServiceNow, Workday, SAP (Cloud), Intuit

### Company Profile

- **Tech stacks**: These companies have largely moved to custom stacks or Spring Boot. However, they still ask Jakarta EE questions for integration patterns, design principles, and legacy knowledge.
- **Role types**: Platform Engineer, Integration Developer.

### Sample Questions

1. "Compare dependency injection in CDI vs Spring. When would you prefer CDI over Spring IoC?"
2. "How does JTA's transaction management differ from Spring's `@Transactional`? When would JTA be necessary?"
3. "Explain how you'd implement an event-driven system using CDI events vs Spring ApplicationEvents. What are the limitations of each?"

---

## 11. Cloud Providers

### AWS, Azure, GCP

### Company Profile

- **Role types**: Solutions Architect (Java/Enterprise focus), Migration Specialist, DevOps/Cloud Engineer for Jakarta EE.
- **Focus on**: Cloud migration of enterprise Java workloads, containerization, serverless.

### Jakarta EE on Cloud Platforms

#### AWS
- Elastic Beanstalk with Tomcat
- AWS Fargate / ECS for containerized WildFly or WebLogic
- AWS MQ (ActiveMQ / RabbitMQ) — not fully JMS compatible but close
- RDS for Oracle/PostgreSQL + JPA
- AWS DMS for database migration

#### Azure
- Azure App Service with Tomcat or WildFly
- Azure Red Hat OpenShift (ARO) for WildFly
- Azure Service Bus (JMS 2.0 compatible!) — actually supports JMS
- Azure SQL Database / SQL Server + JPA
- Azure Migrate for application discovery

#### Google Cloud
- GKE (Google Kubernetes Engine) + Tomcat/WildFly
- Google Cloud Tasks for async processing
- Cloud SQL + JPA
- Migrate for Anthos (for containerizing applications)

### Sample Questions

1. "Migrate a WebLogic EJB application to AWS. Would you use Elastic Beanstalk or ECS? How would you handle the EJB clustering and JMS requirements?"
2. "Your customer wants to deploy a Jakarta EE application on Azure Kubernetes Service. How do you handle session persistence, JMS, and XA transactions in a Kubernetes environment?"

---

## 12. General Interview Strategy by Seniority Level

### Junior (0–3 years)
- Focus on servlets, JSP, basic JPA
- Know the lifecycle methods
- Be able to write a simple REST endpoint with JAX-RS
- Know the difference between GET/POST/PUT/DELETE

### Mid-Level (3–6 years)
- EJB types and when to use each
- JPA relationships, JPQL, Criteria API
- CDI scopes and producers
- JMS queues vs topics
- Bean validation

### Senior (6–10 years)
- Transaction management (JTA, XA, @Transactional)
- Security (JAAS, identity stores, OIDC)
- Performance tuning (connection pools, thread pools, JPA optimization)
- Caching (L1, L2, distributed cache)
- Messaging patterns (competing consumers, dead letter queues, durable subscriptions)

### Architect (10+ years)
- Migration strategies (Java EE → Jakarta EE → Spring Boot → Cloud)
- Trade-off analysis between Jakarta EE and Spring ecosystems
- Distributed systems design with JMS, JTA, and EJBs
- Cloud migration of enterprise Java workloads
- Team mentoring and technology roadmap planning
