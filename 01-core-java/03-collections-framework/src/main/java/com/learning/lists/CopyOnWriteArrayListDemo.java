package com.learning.lists;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Demonstrates CopyOnWriteArrayList - thread-safe list for read-heavy workloads.
 * Immutable snapshots on iteration, copy-on-write for modifications.
 * More efficient than synchronized list for frequent iterations.
 * 
 * @author Java Learning Team
 * @version 1.0
 */
public class CopyOnWriteArrayListDemo {
    
    public void demonstrateCopyOnWriteArrayList() {
        System.out.println("--- COPYONWRITEARRAYLIST DEMONSTRATION ---");
        
        List<String> list = new CopyOnWriteArrayList<>(
            Arrays.asList("Alpha", "Beta", "Gamma")
        );
        
        System.out.println("List: " + list);
        System.out.println("Size: " + list.size());
        
        // Safe iteration even with concurrent modifications
        System.out.println("Iteration (no ConcurrentModificationException):");
        Iterator<String> it = list.iterator();
        list.add("Delta");
        while (it.hasNext()) {
            System.out.print(it.next() + " ");
        }
        System.out.println();
        
        System.out.println("After add: " + list);
        
        // Add and remove
        list.set(0, "ALPHA");
        list.remove("Beta");
        System.out.println("After modifications: " + list);
        
        System.out.println("Thread-safe list demonstrated");
    }
}
