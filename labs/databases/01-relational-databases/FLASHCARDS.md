# Flashcards: Relational Databases

## Card 1
**Q**: What is a functional dependency?
**A**: X → Y means the value of X uniquely determines the value of Y.

## Card 2
**Q**: What are the 3 ACID properties (besides Atomicity)?
**A**: Consistency, Isolation, Durability.

## Card 3
**Q**: What JPA annotation maps a FK to an entity?
**A**: `@ManyToOne` with `@JoinColumn(name = "fk_column")`.

## Card 4
**Q**: What is MVCC?
**A**: Multi-Version Concurrency Control – each transaction sees a snapshot of data.

## Card 5
**Q**: Name the 4 JPA entity states.
**A**: Transient, Managed, Detached, Removed.

## Card 6
**Q**: What is the difference between INNER JOIN and LEFT JOIN?
**A**: INNER JOIN returns only matching rows; LEFT JOIN returns all left-side rows.

## Card 7
**Q**: What is the N+1 query problem?
**A**: One query fetches N rows, then N additional queries fetch related data.

## Card 8
**Q**: What normalization form prohibits repeating groups?
**A**: 1NF (First Normal Form).
