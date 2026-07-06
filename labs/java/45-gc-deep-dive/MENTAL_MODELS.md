# Mental Models for Garbage Collection

## GC as a Janitor Service
Think of the GC as an office janitor. The janitor (collector) cleans up (collects garbage) while employees work (application runs). Serial GC is a single janitor who cleans on weekends (stop-the-world). Parallel GC is a team of janitors who clean on weekends but faster. G1 is a janitor who cleans one office at a time (incremental). ZGC is a janitor who cleans while people work, invisible to everyone.

## Heap Generations as Age Groups
Young generation is like a nursery for infants (most objects die young). Survivor spaces are like daycare for toddlers (survived one collection). Old generation is like the adult population (long-lived objects). This model explains why copying collectors work well for young gen — most babies go home (die) so moving the few survivors is cheap.

## G1 Regions as a City Grid
G1 divides the heap into regions like a city grid. Each region is a neighborhood. Garbage collection is like the city picking the trashiest neighborhoods first (G1 = garbage-first). Remembered sets are maps showing which neighborhoods have cross-town references. SATB (Snapshot-At-The-Beginning) is like taking a photo of the city at the start of the day — everyone in the photo must survive, even if they move.

## ZGC Colored Pointers as Library Color Tags
Imagine a library where books have colored tags on their spines:
- Green: "I'm in the right place" (good reference)
- Red: "I need to be moved" (relocated)
- Yellow: "Check with the librarian" (load barrier needed)

When you pick a book (read an object reference), the color tells you what to do. Most of the time the book is green and you proceed directly. Only red or yellow books need extra steps.

## Mark-Sweep as a Two-Pass Process
Pass 1 (Mark): Like the census — go through every house, knock on doors, find who's alive (reachable). Mark them on a list.
Pass 2 (Sweep): Like the demolition crew — tear down houses not on the list (unreachable = garbage).

## GC Roots as Tree Roots
GC roots are the roots of a tree. The objects are branches and leaves. If a branch is connected to the roots via any path, it's alive. If no path exists from any root, the branch dies and falls off. The GC traces all paths from the roots to find which objects are alive.
