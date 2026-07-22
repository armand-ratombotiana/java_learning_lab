# Interview Questions: LinkedList Deep Dive

## Company-Specific Focus

### Google
- LinkedList: doubly linked list implementation of both List and Deque
- Node structure: item, prev, next pointers
- Indexed operations: O(n) traversal to find element by index

### Microsoft
- LinkedList vs C# LinkedList<T>: similar data structure
- Performance comparison: LinkedList vs ArrayList in different scenarios

### Amazon
- LinkedList for queue/deque operations: addFirst, addLast, removeFirst
- Memory overhead: each node has 2 extra references (prev, next) plus object header
- Frequent insertion/deletion at middle vs ArrayList shift cost

### Meta
- LinkedList as a queue: FIFO with add/remove/poll/peek
- LinkedList iteration: slightly slower than ArrayList due to node dereferencing
- Avoid LinkedList for indexed access: O(n) get(index)

### Apple
- LinkedList memory overhead vs ArrayList: prefers ArrayList for most use cases
- Deque operations: when to use LinkedList vs ArrayDeque

### Oracle
- LinkedList JCF specification: doubly linked list implementation
- Fail-fast iterator: modCount tracking for concurrent modification detection

## LeetCode-Related Questions
| LC Problem | Difficulty | Companies | Notes |
|------------|------------|-----------|-------|
| 206 Reverse Linked List | Easy | Amazon, Google, Apple, Meta | Iterative vs recursive reversal |
| 21 Merge Two Sorted Lists | Easy | Amazon, Microsoft, Google | Merge two linked lists |
| 141 Linked List Cycle | Easy | Amazon, Google, Apple | Floyd's cycle detection |
| 19 Remove Nth Node From End of List | Medium | Google, Amazon | Two-pointer technique |
| 23 Merge k Sorted Lists | Hard | Amazon, Google, Apple | Priority queue with list nodes |
| 138 Copy List with Random Pointer | Medium | Google, Amazon, Apple | HashMap-based node mapping |
| 25 Reverse Nodes in k-Group | Hard | Google, Amazon, Microsoft | Recursive group reversal |
| 142 Linked List Cycle II | Medium | Amazon, Google, Apple | Floyd's algorithm + entry detection |
| 148 Sort List | Medium | Google, Amazon, Microsoft | Merge sort on linked list |
| 234 Palindrome Linked List | Easy | Amazon, Google, Apple | Reverse and compare halves |

## Real Production Scenarios
- **Twitter**: Using LinkedList for a message queue caused GC pressure due to many node objects
- **LinkedIn**: Migrating from LinkedList to ArrayDeque for a high-frequency queue reduced GC by 40%

## Interview Patterns & Tips
- **Two-pointer**: Fast/slow pointer for cycle detection, K-th from end, middle of list
- **Dummy head**: Simplify edge cases for insertions and deletions at head
- **Recursion vs iteration**: Recursion can cause stack overflow for long lists
- **Memory**: LinkedList has higher memory overhead per element than ArrayList

## Deep Dive Questions
- **Node memory**: How many bytes does a LinkedList node consume with compressed OOPs?
- **Performance**: Why is ArrayList faster than LinkedList for most operations?
- **Deque**: Why is ArrayDeque preferred over LinkedList as a stack/queue?
- **GC**: Why does LinkedList cause more GC pressure than ArrayList?
- **Random access**: Why does LinkedList.get(index) take O(n) time?