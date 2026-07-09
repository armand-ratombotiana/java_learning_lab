# Mental Models for Advanced SQL

## The Window Frame Model
PICTURE a sliding window over an ordered list. The window has a starting row (PRECEDING) and an ending row (FOLLOWING). As you move down the list, the window slides. Each row sees the contents of its window. This is the ROWS-based frame model.

For RANGE, imagine the ORDER BY value has a "range" — the window includes all rows with values within that range from the current value.

## The Recursive CTE Spiral Model
PICTURE an expanding spiral. The anchor starts at the center. Each recursive step expands outward one level. The spiral continues until no new territory is discovered.

## The PIVOT Carousel Model
PICTURE a carousel where rows are on the outside and the columns/pivot values are seats. When PIVOT, the carousel rotates 90°, turning rows (riders) into columns (seats), and aggregating the riders at each seat.

## The MERGE Venn Diagram Model
PICTURE two overlapping circles: source (S) and target (T). MERGE:
- S ∩ T: UPDATE existing rows
- S - T: INSERT new rows
- T - S (with DELETE clause): DELETE unmatched rows

## The Execution Plan Tree Model
PICTURE an upside-down tree. The root is the final operation (e.g., SELECT). Branches are operations (JOINS, TABLE ACCESS). Leaves are table scans. Data flows from leaves upward, being transformed at each node.

## The Partition Pruning Model
PICTURE a filing cabinet (table) with labeled drawers (partitions). A query specifies criteria. The optimizer walks to the correct drawer, opens only that drawer, and ignores the rest.

## The Index Model
PICTURE a phone book. B-tree is like the phone book's alphabetical ordering — you can find "Smith" quickly (B-tree range scan). Bitmap is like a tag cloud — each value has a bitmap indicating which rows have it.

## The SQL Profile Model
PICTURE a cheat sheet attached to a query. The profile tells the optimizer "this table actually has 10x more rows than statistics say" or "for this query, prefer hash joins."

## The SPM Model
PICTURE a library of known-good maps. When the optimizer wants to navigate (execute), it checks the library for existing maps. If it finds an accepted map, it uses that. New maps must be verified (evolved) before being added to the accepted library.

## The Adaptive Plan Model
PICTURE a choose-your-own-adventure book. At decision point "A," if cardinality < 1000, choose nested loops; otherwise, hash join. The decision is made at execution time based on actual conditions.

## The MATCH_RECOGNIZE Model
PICTURE a pattern-recognition game. You have a sequence of symbols (rows). You define a PATTERN (like a regex) and conditions for each symbol. MATCH_RECOGNIZE finds all matching subsequences.

## The MODEL Clause Model
PICTURE an Excel spreadsheet. PARTITION BY defines separate worksheets. DIMENSION BY defines row and column headers. MEASURES are the cell values. RULES are formulas that fill or update cells.

## The CONNECT BY Model
PICTURE a family tree. START WITH identifies the root(s). CONNECT BY PRIOR defines the parent-child relationship. LEVEL is the generation number. SYS_CONNECT_BY_PATH shows the full ancestry.

## The Optimizer Statistics Model
PICTURE a library catalog with card entries for each book (table). Each card tells you: number of pages (blocks), average page length (avg row), how many unique topics (NDV), how worn the book is (clustering factor). The optimizer uses these cards to estimate how long different search strategies will take.