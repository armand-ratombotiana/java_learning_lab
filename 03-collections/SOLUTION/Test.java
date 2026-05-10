package com.learning.lab.module03.solution;

import java.util.*;

public class Test {
    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) {
        System.out.println("=== Module 03: Collections - Comprehensive Tests ===\n");

        testArrayList();
        testLinkedList();
        testVector();
        testStack();
        testHashSet();
        testTreeSet();
        testHashMap();
        testTreeMap();
        testPriorityQueue();
        testArrayDeque();
        testCollectionsUtility();
        testCustomList();
        testCustomMap();

        printSummary();
    }

    private static void testArrayList() {
        System.out.println("--- Testing ArrayList ---");

        test("ArrayList add elements", () -> {
            List<String> list = new ArrayList<>();
            list.add("A");
            list.add("B");
            assert list.size() == 2 : "Size should be 2";
            assert list.get(0).equals("A") : "First element should be A";
        });

        test("ArrayList add at index", () -> {
            List<String> list = new ArrayList<>(List.of("A", "B", "C"));
            list.add(1, "X");
            assert list.get(1).equals("X") : "Should insert at index";
            assert list.size() == 4 : "Size should be 4";
        });

        test("ArrayList remove", () -> {
            List<String> list = new ArrayList<>(List.of("A", "B", "C"));
            list.remove("B");
            assert list.size() == 2 : "Size should be 2";
            assert !list.contains("B") : "Should not contain B";
        });

        test("ArrayList contains", () -> {
            List<String> list = new ArrayList<>(List.of("A", "B", "C"));
            assert list.contains("B") : "Should contain B";
            assert !list.contains("D") : "Should not contain D";
        });

        test("ArrayList clear", () -> {
            List<String> list = new ArrayList<>(List.of("A", "B"));
            list.clear();
            assert list.isEmpty() : "Should be empty";
        });

        test("ArrayList removeIf", () -> {
            List<Integer> list = new ArrayList<>(List.of(1, 2, 3, 4, 5));
            list.removeIf(n -> n % 2 == 0);
            assert list.size() == 3 : "Should have 3 elements";
            assert list.equals(List.of(1, 3, 5)) : "Should contain only odds";
        });

        System.out.println();
    }

    private static void testLinkedList() {
        System.out.println("--- Testing LinkedList ---");

        test("LinkedList addFirst/addLast", () -> {
            LinkedList<String> list = new LinkedList<>();
            list.addFirst("First");
            list.addLast("Last");
            assert list.getFirst().equals("First") : "First should be First";
            assert list.getLast().equals("Last") : "Last should be Last";
        });

        test("LinkedList push/pop", () -> {
            LinkedList<String> list = new LinkedList<>();
            list.push("A");
            list.push("B");
            assert list.pop().equals("B") : "Should pop B";
            assert list.size() == 1 : "Should have 1 element";
        });

        test("LinkedList removeFirst/Last", () -> {
            LinkedList<String> list = new LinkedList<>(List.of("A", "B", "C"));
            list.removeFirst();
            list.removeLast();
            assert list.size() == 1 : "Should have 1 element";
            assert list.get(0).equals("B") : "Should contain B";
        });

        System.out.println();
    }

    private static void testVector() {
        System.out.println("--- Testing Vector ---");

        test("Vector basic operations", () -> {
            Vector<Integer> vector = new Vector<>();
            vector.add(1);
            vector.add(2);
            assert vector.size() == 2 : "Size should be 2";
            assert vector.get(0) == 1 : "First element should be 1";
        });

        test("Vector capacity", () -> {
            Vector<Integer> vector = new Vector<>(5, 2);
            for (int i = 0; i < 6; i++) vector.add(i);
            assert vector.capacity() > 5 : "Capacity should grow";
        });

        test("Vector enumeration", () -> {
            Vector<String> vector = new Vector<>(List.of("A", "B", "C"));
            Enumeration<String> e = vector.elements();
            int count = 0;
            while (e.hasMoreElements()) {
                e.nextElement();
                count++;
            }
            assert count == 3 : "Should enumerate 3 elements";
        });

        System.out.println();
    }

    private static void testStack() {
        System.out.println("--- Testing Stack ---");

        test("Stack push/pop", () -> {
            Stack<Integer> stack = new Stack<>();
            stack.push(1);
            stack.push(2);
            assert stack.pop() == 2 : "Should pop 2";
            assert stack.pop() == 1 : "Should pop 1";
        });

        test("Stack peek", () -> {
            Stack<Integer> stack = new Stack<>();
            stack.push(1);
            stack.push(2);
            assert stack.peek() == 2 : "Peek should return 2";
            assert stack.size() == 2 : "Size should not change";
        });

        test("Stack empty/search", () -> {
            Stack<String> stack = new Stack<>();
            stack.push("A");
            stack.push("B");
            stack.push("C");
            assert !stack.empty() : "Should not be empty";
            assert stack.search("B") == 2 : "B should be at position 2";
            assert stack.search("Z") == -1 : "Z should not be found";
        });

        System.out.println();
    }

    private static void testHashSet() {
        System.out.println("--- Testing HashSet ---");

        test("HashSet no duplicates", () -> {
            Set<String> set = new HashSet<>();
            set.add("A");
            set.add("B");
            set.add("A");
            assert set.size() == 2 : "Should not contain duplicates";
        });

        test("HashSet contains", () -> {
            Set<String> set = new HashSet<>(Set.of("A", "B", "C"));
            assert set.contains("B") : "Should contain B";
            assert !set.contains("D") : "Should not contain D";
        });

        test("HashSet remove", () -> {
            Set<String> set = new HashSet<>(Set.of("A", "B", "C"));
            set.remove("B");
            assert set.size() == 2 : "Size should be 2";
            assert !set.contains("B") : "Should not contain B";
        });

        test("HashSet iteration", () -> {
            Set<Integer> set = new HashSet<>(Set.of(1, 2, 3));
            int sum = 0;
            for (Integer i : set) sum += i;
            assert sum == 6 : "Sum should be 6";
        });

        System.out.println();
    }

    private static void testTreeSet() {
        System.out.println("--- Testing TreeSet ---");

        test("TreeSet sorted order", () -> {
            TreeSet<Integer> set = new TreeSet<>();
            set.add(3);
            set.add(1);
            set.add(2);
            List<Integer> list = set.stream().toList();
            assert list.equals(List.of(1, 2, 3)) : "Should be sorted";
        });

        test("TreeSet first/last", () -> {
            TreeSet<Integer> set = new TreeSet<>(Set.of(5, 2, 8, 1));
            assert set.first() == 1 : "First should be 1";
            assert set.last() == 8 : "Last should be 8";
        });

        test("TreeSet subSet", () -> {
            TreeSet<Integer> set = new TreeSet<>(Set.of(1, 2, 3, 4, 5));
            Set<Integer> sub = set.subSet(2, 4);
            assert sub.size() == 2 : "Subset size should be 2";
        });

        test("TreeSet lower/higher", () -> {
            TreeSet<Integer> set = new TreeSet<>(Set.of(1, 3, 5, 7));
            assert set.lower(5) == 3 : "Lower than 5 should be 3";
            assert set.higher(5) == 7 : "Higher than 5 should be 7";
        });

        System.out.println();
    }

    private static void testHashMap() {
        System.out.println("--- Testing HashMap ---");

        test("HashMap put/get", () -> {
            Map<String, Integer> map = new HashMap<>();
            map.put("A", 1);
            map.put("B", 2);
            assert map.get("A") == 1 : "Should get 1";
            assert map.get("B") == 2 : "Should get 2";
        });

        test("HashMap overwrite", () -> {
            Map<String, Integer> map = new HashMap<>();
            map.put("A", 1);
            map.put("A", 2);
            assert map.get("A") == 2 : "Should be overwritten";
            assert map.size() == 1 : "Size should be 1";
        });

        test("HashMap containsKey/Value", () -> {
            Map<String, Integer> map = new HashMap<>(Map.of("A", 1, "B", 2));
            assert map.containsKey("A") : "Should contain key A";
            assert map.containsValue(2) : "Should contain value 2";
        });

        test("HashMap remove", () -> {
            Map<String, Integer> map = new HashMap<>(Map.of("A", 1, "B", 2));
            map.remove("A");
            assert map.size() == 1 : "Size should be 1";
            assert !map.containsKey("A") : "Should not contain A";
        });

        test("HashMap iteration", () -> {
            Map<String, Integer> map = new HashMap<>(Map.of("A", 1, "B", 2));
            int sum = 0;
            for (Integer v : map.values()) sum += v;
            assert sum == 3 : "Sum should be 3";
        });

        System.out.println();
    }

    private static void testTreeMap() {
        System.out.println("--- Testing TreeMap ---");

        test("TreeMap sorted keys", () -> {
            TreeMap<Integer, String> map = new TreeMap<>();
            map.put(3, "C");
            map.put(1, "A");
            map.put(2, "B");
            List<Integer> keys = map.keySet().stream().toList();
            assert keys.equals(List.of(1, 2, 3)) : "Keys should be sorted";
        });

        test("TreeMap first/last key", () -> {
            TreeMap<Integer, String> map = new TreeMap<>(Map.of(5, "E", 2, "B", 8, "H"));
            assert map.firstKey() == 2 : "First key should be 2";
            assert map.lastKey() == 8 : "Last key should be 8";
        });

        test("TreeMap subMap", () -> {
            TreeMap<Integer, String> map = new TreeMap<>(Map.of(1, "A", 2, "B", 3, "C", 4, "D", 5, "E"));
            Map<Integer, String> sub = map.subMap(2, 4);
            assert sub.size() == 2 : "SubMap size should be 2";
        });

        System.out.println();
    }

    private static void testPriorityQueue() {
        System.out.println("--- Testing PriorityQueue ---");

        test("PriorityQueue order", () -> {
            PriorityQueue<Integer> pq = new PriorityQueue<>();
            pq.add(3);
            pq.add(1);
            pq.add(2);
            assert pq.poll() == 1 : "First should be smallest";
            assert pq.poll() == 2 : "Second should be 2";
        });

        test("PriorityQueue peek", () -> {
            PriorityQueue<Integer> pq = new PriorityQueue<>(List.of(5, 2, 8));
            assert pq.peek() == 2 : "Peek should return smallest";
        });

        test("PriorityQueue with comparator", () -> {
            PriorityQueue<Integer> pq = new PriorityQueue<>(Comparator.reverseOrder());
            pq.add(1);
            pq.add(2);
            pq.add(3);
            assert pq.poll() == 3 : "First should be largest";
        });

        System.out.println();
    }

    private static void testArrayDeque() {
        System.out.println("--- Testing ArrayDeque ---");

        test("ArrayDeque addFirst/Last", () -> {
            ArrayDeque<String> deque = new ArrayDeque<>();
            deque.addFirst("A");
            deque.addLast("C");
            deque.addFirst("B");
            assert deque.getFirst().equals("B") : "First should be B";
            assert deque.getLast().equals("C") : "Last should be C";
        });

        test("ArrayDeque pollFirst/Last", () -> {
            ArrayDeque<String> deque = new ArrayDeque<>(List.of("A", "B", "C"));
            assert deque.pollFirst().equals("A") : "Should poll A";
            assert deque.pollLast().equals("C") : "Should poll C";
            assert deque.size() == 1 : "Size should be 1";
        });

        System.out.println();
    }

    private static void testCollectionsUtility() {
        System.out.println("--- Testing Collections Utility ---");

        test("Collections sort", () -> {
            List<Integer> list = new ArrayList<>(List.of(3, 1, 2));
            Collections.sort(list);
            assert list.equals(List.of(1, 2, 3)) : "Should be sorted";
        });

        test("Collections reverse", () -> {
            List<Integer> list = new ArrayList<>(List.of(1, 2, 3));
            Collections.reverse(list);
            assert list.equals(List.of(3, 2, 1)) : "Should be reversed";
        });

        test("Collections shuffle", () -> {
            List<Integer> list = new ArrayList<>(List.of(1, 2, 3, 4, 5));
            List<Integer> original = new ArrayList<>(list);
            Collections.shuffle(list);
            assert list.size() == original.size() : "Size should be same";
        });

        test("Collections synchronized", () -> {
            List<String> syncList = Collections.synchronizedList(new ArrayList<>());
            Set<String> syncSet = Collections.synchronizedSet(new HashSet<>());
            Map<String, Integer> syncMap = Collections.synchronizedMap(new HashMap<>());
            assert syncList != null && syncSet != null && syncMap != null : "Should not be null";
        });

        test("Collections unmodifiable", () -> {
            List<Integer> list = new ArrayList<>(List.of(1, 2, 3));
            List<Integer> unmod = Collections.unmodifiableList(list);
            boolean thrown = false;
            try {
                unmod.add(4);
            } catch (UnsupportedOperationException e) {
                thrown = true;
            }
            assert thrown : "Should throw on modification";
        });

        System.out.println();
    }

    private static void testCustomList() {
        System.out.println("--- Testing Custom List ---");

        test("CustomList add/get", () -> {
            CustomList<String> list = new ArrayListCustom<>();
            list.add("A");
            list.add("B");
            assert list.get(0).equals("A") : "First should be A";
            assert list.get(1).equals("B") : "Second should be B";
        });

        test("CustomList add at index", () -> {
            CustomList<String> list = new ArrayListCustom<>();
            list.add("A");
            list.add("B");
            list.add(1, "X");
            assert list.get(1).equals("X") : "Should insert at index";
        });

        test("CustomList size/contains", () -> {
            CustomList<String> list = new ArrayListCustom<>();
            list.add("A");
            list.add("B");
            assert list.size() == 2 : "Size should be 2";
            assert list.contains("A") : "Should contain A";
            assert !list.contains("C") : "Should not contain C";
        });

        test("CustomList remove", () -> {
            CustomList<String> list = new ArrayListCustom<>();
            list.add("A");
            list.add("B");
            list.remove("A");
            assert list.size() == 1 : "Size should be 1";
            assert list.get(0).equals("B") : "Should contain B";
        });

        System.out.println();
    }

    private static void testCustomMap() {
        System.out.println("--- Testing Custom Map ---");

        test("CustomMap put/get", () -> {
            CustomMap<String, Integer> map = new HashMapCustom<>();
            map.put("A", 1);
            map.put("B", 2);
            assert map.get("A") == 1 : "Should get 1";
            assert map.get("B") == 2 : "Should get 2";
        });

        test("CustomMap overwrite", () -> {
            CustomMap<String, Integer> map = new HashMapCustom<>();
            map.put("A", 1);
            map.put("A", 2);
            assert map.get("A") == 2 : "Should be overwritten";
        });

        test("CustomMap containsKey", () -> {
            CustomMap<String, Integer> map = new HashMapCustom<>();
            map.put("A", 1);
            assert map.containsKey("A") : "Should contain A";
            assert !map.containsKey("B") : "Should not contain B";
        });

        test("CustomMap remove", () -> {
            CustomMap<String, Integer> map = new HashMapCustom<>();
            map.put("A", 1);
            map.put("B", 2);
            map.remove("A");
            assert map.size() == 1 : "Size should be 1";
            assert map.get("A") == null : "A should be removed";
        });

        test("CustomMap size", () -> {
            CustomMap<String, Integer> map = new HashMapCustom<>();
            map.put("A", 1);
            map.put("B", 2);
            map.put("C", 3);
            assert map.size() == 3 : "Size should be 3";
        });

        System.out.println();
    }

    private static void test(String name, Runnable test) {
        try {
            test.run();
            System.out.println("  PASS: " + name);
            passed++;
        } catch (AssertionError | Exception e) {
            System.out.println("  FAIL: " + name + " - " + e.getMessage());
            failed++;
        }
    }

    private static void printSummary() {
        System.out.println("=== Test Summary ===");
        System.out.println("Passed: " + passed);
        System.out.println("Failed: " + failed);
        System.out.println("Total: " + (passed + failed));
        System.out.println("===================");
    }
}