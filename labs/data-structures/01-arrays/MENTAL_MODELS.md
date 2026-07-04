# Mental Models for Arrays

## The Parking Lot

An array is a row of numbered parking spaces. Each space holds exactly one car (element). You know the address of the lot (base pointer) and can walk directly to space #i. To add a car between spaces, everyone shifts down.

## The Mailbox Row

A row of mailboxes mounted on a single wall (contiguous memory). Mailbox #3 is exactly one box-width past mailbox #2. The wall is the memory block; each box is a slot identified by its offset.

## The Filing Cabinet

A single drawer with labeled folders in order (index 0, 1, 2...). You can open any folder instantly if you know its position. To insert a new folder, you must shift all folders after it. Removing a folder leaves a gap that must be closed by shifting.

## Java-Specific Model

```
Stack (reference)  →  Heap (array object)
    arr                  ┌──────────┐
                         │ length=5 │
                         │ arr[0]   │
                         │ arr[1]   │
                         │ arr[2]   │
                         │ arr[3]   │
                         │ arr[4]   │
                         └──────────┘
```

The reference variable sits on the stack; the array object (header + elements) lives on the heap.

## The Dynamic Array Party

Start with room for 4 guests. When the 5th arrives, rent a room twice as big (capacity 8), have everyone walk to the new room, then invite the new guest. Most of the time, adding costs O(1) — only the occasional move is expensive.
