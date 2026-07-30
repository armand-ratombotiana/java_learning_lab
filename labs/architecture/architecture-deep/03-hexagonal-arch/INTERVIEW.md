# Interview Questions — Hexagonal Architecture

## Q1: What is a port vs an adapter?
**A:** A port is a Java interface defining a boundary (e.g., `AccountRepository`). An adapter is an implementation (e.g., `JpaAccountRepository`) that plugs into the port.

## Q2: How does hexagonal architecture enforce dependency inversion?
**A:** Domain core defines the port interfaces; adapters depend on ports, not the other way around. The core never imports adapter classes.

## Q3: What are driving vs driven adapters?
**A:** Driving adapters (inbound) call into the core (e.g., REST controller). Driven adapters (outbound) are called by the core (e.g., database repository).

## Q4: Can you have multiple adapters for one port?
**A:** Yes — e.g., an in-memory repository for tests and a PostgreSQL repository for production, both implementing `AccountRepository`.
