# Internals of Persistent Data Structures

## Node Structure

`java
class PersistentNode<E> {
    E value;
    PersistentNode<E> next;  // immutable, shared
}
`

## Structural Sharing Verification

Two persistent lists share nodes if:
`
list1 = PersistentList.empty().add(1).add(2)
list2 = list1.add(3)
assert list1.tail() == list2.tail().tail()  // shared!
`

## Efficiency

List add: O(1) â€” create one new node, point to old list
List head: O(1) â€” return head pointer
List tail: O(1) â€” return tail pointer
List get by index: O(n) â€” must traverse

Persistent BST insert: O(log n) â€” create O(log n) new nodes
Persistent BST search: O(log n) â€” same as ephemeral
