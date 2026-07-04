# Mental Models for Stacks & Queues

## Stack: The Stack of Plates

A spring-loaded stack of plates. You can only:
- Add a plate on top (push)
- Remove the top plate (pop)
- Look at the top plate (peek)

You cannot remove a plate from the middle without removing all plates above it first.

## Stack: The Pez Dispenser

Open the top, insert candies one by one. The first candy you put in is the last one to come out. The last candy you put in pops out first.

## Queue: The Ticket Line

People join at the back (enqueue). The person at the front is served next (dequeue). If you arrive first, you are served first. No cutting in line.

## Queue: The Pipe

Water enters one end of a pipe and exits the other end in the same order. The first water molecule in is the first one out.

## Deque: The To-Do List with Front and Back

You can add items to the front (urgent) or the back (non-urgent). You can complete items from the front (if urgent) or the back (if deferring).

## Priority Queue: The Emergency Room

Patients arrive at different times but are treated by severity of condition, not arrival order. A heart attack patient (high priority) goes before a paper cut (low priority), even if the paper cut arrived first.

## Java Implementation Model

```
ArrayDeque as Stack:
  ┌────┬────┬────┬────┬────┬────┐
  │    │    │  C │  B │  A │    │
  └────┴────┴────┴────┴────┴────┘
            ↑         ↑
          head      tail
  push → increment tail (circular)
  pop  → decrement tail

ArrayDeque as Queue:
  ┌────┬────┬────┬────┬────┬────┐
  │    │  A │  B │  C │  D │    │
  └────┴────┴────┴────┴────┴────┘
            ↑              ↑
          head           tail
  addLast → increment tail, write
  removeFirst → read head, increment head
```
