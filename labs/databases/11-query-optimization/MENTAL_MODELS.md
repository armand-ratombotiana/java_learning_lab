# Mental Models: Query Optimization

## Library Card Catalog
An index is like a library card catalog. Without it, to find a book you must walk every aisle (seq scan). With a catalog + call number (index), you go directly to the shelf. A covering index is like having the book content on the catalog card itself.

## Cooking Recipe
A query is a recipe. EXPLAIN shows you the cooking steps. Seq Scan = "chop all vegetables" vs Index Scan = "only chop the carrots you need". The optimizer chooses the best method based on how many vegetables you need.

## N+1 as Telephone Tag
The N+1 problem is like calling one person to get a list of names, then calling each person individually instead of asking for everyone's contact info at once. One `JOIN` is a conference call.

## Index as Photocopy
An index is a sorted photocopy of specific columns. It's faster to search the photocopy (sorted, compact) than the original book (unsorted, large). But updating the photocopy has overhead – every INSERT/UPDATE must update both the book and the photocopy.
