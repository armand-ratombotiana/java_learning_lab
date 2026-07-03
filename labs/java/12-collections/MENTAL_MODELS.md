# Collections — Mental Models

## Model 1: The Library System

- **ArrayList**: A bookshelf where books are in numbered slots (indices). Finding book #5 is instant. Inserting a book at #1 requires shifting everything.
- **LinkedList**: A scavenger hunt. Each book points to the next. Finding book #5 requires reading clues from book #1. Adding a book just rewrites nearby clues.
- **HashSet**: A card catalog organized by hash value. Finding a book is instant if you know its exact identity. No particular order.
- **TreeSet**: A Dewey Decimal system. Books are always sorted. Finding is logarithmic — fast, not instant.
- **HashMap**: An indexed card catalog where each card has a unique key and points to a shelf location.

## Model 2: The Waiting Lines

- **Queue**: A checkout line. First come, first served (FIFO). New people join the back. The cashier serves from the front.
- **Deque**: A line that can be entered or exited from either end. Like a line at a concert VIP entrance — both ends are active.
- **PriorityQueue**: An emergency room triage. The most urgent patient is treated next, regardless of arrival order. Priority determined by a triage score (Comparator).
- **Stack**: A stack of plates. Last plate put on top is the first taken off. LIFO.

## Model 3: The Party

- **Map<K,V>**: A coat check. Each ticket stub (key) corresponds to one coat (value). You can get your coat back with the stub. No two stubs are the same.
- **HashMap**: The attendant remembers the stub number and has a direct cubby for it.
- **TreeMap**: The coats are hung alphabetically by last name. Slower to find, but easy to list in order.
- **LinkedHashMap**: The coats are stored in the order people checked them in. Useful if you want to know who came first.

## Model 4: The Sorted Drawer

- **List**: Like a drawer of index cards. You can add anywhere, get by position, and they're in the order you placed them.
- **ArrayList**: Cards in neat rows — fast to pick any position.
- **LinkedList**: Cards on a chain — fast to add/remove at ends.
- **Set**: A drawer where duplicates are physically impossible.
- **SortedSet**: A drawer with dividers — A-E, F-J, etc. Easy to find any specific card.
