# Step by Step: Linked List Operations

## AddFirst on Empty List

```
State: head=null, tail=null, size=0

addFirst(10):
  1. Node(10) created: data=10, next=null
  2. newNode.next = head → null
  3. head = newNode
  4. tail is null → tail = head
  5. size=1
  Result: head→[10]←tail
```

## AddFirst on Non-Empty List

```
State: head→[10], tail→[10], size=1

addFirst(20):
  1. Node(20) created: data=20, next=null
  2. newNode.next = head → [10]
  3. head = newNode
  4. tail is not null → skip
  5. size=2
  Result: head→[20]→[10]←tail
```

## AddLast on Non-Empty List

```
State: head→[10]→[20], tail→[20], size=2

addLast(30):
  1. Node(30) created: data=30, next=null
  2. tail is not null → tail.next = newNode
  3. tail = newNode
  4. size=3
  Result: head→[10]→[20]→[30]←tail
```

## Remove by Value

```
State: head→[10]→[20]→[30], tail→[30], size=3

remove(20):
  1. head.data (10) ≠ 20 → continue
  2. current = head ([10])
  3. current.next.data (20) == 20 → match
  4. current.next = current.next.next ([30])
  5. current.next.next is null → tail = current.next
     Actually: current.next is now [30], which is the new tail
  6. size=2
  Result: head→[10]→[30]←tail
```

## Reverse In-Place

```
State: head→[10]→[20]→[30]→null

reverse():
  prev=null, current=head=[10]
  
  Iteration 1:
    next = current.next = [20]
    current.next = prev = null
    prev = current = [10]
    current = next = [20]
  List so far: null←[10]  [20]→[30]→null
  
  Iteration 2:
    next = current.next = [30]
    current.next = prev = [10]
    prev = current = [20]
    current = next = [30]
  List so far: null←[10]←[20]  [30]→null
  
  Iteration 3:
    next = current.next = null
    current.next = prev = [20]
    prev = current = [30]
    current = next = null
  List so far: null←[10]←[20]←[30]
  
  head = prev = [30]
  Result: head→[30]→[20]→[10]→null, tail=[10]
```
