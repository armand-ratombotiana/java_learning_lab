package com.learning;

/**
 * Main entry point for Collections Framework module.
 * Demonstrates all major collection types and operations.
 * 
 * @author Java Learning Team
 * @version 1.0
 */
public class Main {
    
    /**
     * Main method to demonstrate all collections concepts.
     */
    public static void main(String[] args) {
        System.out.println("=== Collections Framework Module ===\n");
        
        // Lists
        System.out.println("1. LISTS DEMONSTRATIONS:");
        new com.learning.lists.ListInterfaceDemo().demonstrateLists();
        new com.learning.lists.ArrayListDemo().demonstrateArrayList();
        new com.learning.lists.LinkedListDemo().demonstrateLinkedList();
        
        System.out.println("\n2. SETS DEMONSTRATIONS:");
        new com.learning.sets.HashSetDemo().demonstrateHashSet();
        new com.learning.sets.TreeSetDemo().demonstrateTreeSet();
        
        System.out.println("\n3. MAPS DEMONSTRATIONS:");
        new com.learning.maps.HashMapDemo().demonstrateHashMap();
        new com.learning.maps.TreeMapDemo().demonstrateTreeMap();
        
        System.out.println("\n4. QUEUES DEMONSTRATIONS:");
        new com.learning.queues.QueueInterfaceDemo().demonstrateQueues();
        new com.learning.queues.PriorityQueueDemo().demonstratePriorityQueue();
        
        System.out.println("\n5. CUSTOM COLLECTIONS DEMONSTRATIONS:");
        new com.learning.custom.CustomCollectionExample().demonstrateCustomCollection();
        
        System.out.println("\n6. UTILITIES DEMONSTRATIONS:");
        new com.learning.utilities.CollectionsUtilityDemo().demonstrateUtilities();

        System.out.println("\n7. ELITE COLLECTIONS TRAINING:");
        EliteCollectionsTraining.demonstrateEliteCollectionsTraining();

        System.out.println("\n\n=== Collections Framework Module Complete ===");
        System.out.println("All collections demonstrations executed successfully!");
        System.out.println("\n🎓 You've mastered:");
        System.out.println("   • All major collection types (List, Set, Map, Queue)");
        System.out.println("   • Performance characteristics and trade-offs");
        System.out.println("   • 11+ advanced interview problems");
        System.out.println("   • LRU Cache, Sliding Window, Top K patterns");
        System.out.println("\n🚀 Ready for elite-level technical interviews!");
    }
}
