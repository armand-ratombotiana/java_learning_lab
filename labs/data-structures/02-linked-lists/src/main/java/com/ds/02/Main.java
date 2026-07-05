package com.ds02;

import java.util.Iterator;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== SinglyLinkedList Demo ===");
        SinglyLinkedList<Integer> sll = new SinglyLinkedList<>();
        sll.addFirst(3);
        sll.addFirst(2);
        sll.addFirst(1);
        sll.addLast(4);
        sll.addLast(5);
        System.out.println("List: " + sll);
        System.out.println("Size: " + sll.size());
        System.out.println("Get index 2: " + sll.get(2));
        System.out.println("Contains 4: " + sll.contains(4));
        sll.remove(4);
        System.out.println("Remove 4: " + sll);
        sll.reverse();
        System.out.println("Reversed: " + sll);
        System.out.println("Remove first: " + sll.removeFirst());
        System.out.println("Remove last: " + sll.removeLast());
        System.out.println("After removes: " + sll);

        System.out.println("\n=== DoublyLinkedList Demo ===");
        DoublyLinkedList<String> dll = new DoublyLinkedList<>();
        dll.addLast("a");
        dll.addLast("b");
        dll.addLast("c");
        dll.addFirst("z");
        System.out.println("List: " + dll);
        System.out.println("Descending: ");
        Iterator<String> dit = dll.descendingIterator();
        while (dit.hasNext()) System.out.print(dit.next() + " ");
        System.out.println();
        dll.reverse();
        System.out.println("Reversed: " + dll);
        dll.add(2, "X");
        System.out.println("Add X at index 2: " + dll);
        dll.remove(1);
        System.out.println("Remove index 1: " + dll);

        System.out.println("\n=== CircularLinkedList Demo ===");
        CircularLinkedList<Integer> cll = new CircularLinkedList<>();
        cll.addLast(1);
        cll.addLast(2);
        cll.addLast(3);
        cll.addFirst(0);
        System.out.println("List: " + cll);
        System.out.println("First: " + cll.getFirst() + ", Last: " + cll.getLast());
        System.out.println("Contains 2: " + cll.contains(2));
        System.out.println("Remove first: " + cll.removeFirst());
        System.out.println("Remove last: " + cll.removeLast());
        System.out.println("After removes: " + cll);
        System.out.println("Is circular: " + cll.isCircular());
    }
}
