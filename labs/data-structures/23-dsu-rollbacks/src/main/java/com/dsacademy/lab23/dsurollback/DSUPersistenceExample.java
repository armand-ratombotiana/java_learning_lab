package com.dsacademy.lab23.dsurollback;

public class DSUPersistenceExample {

    public static void main(String[] args) {
        DSUWithRollback dsu = new DSUWithRollback(6);

        dsu.union(0, 1);
        dsu.union(2, 3);
        System.out.println("Sets: " + dsu.getSets());
        dsu.snapshot();

        dsu.union(0, 2);
        System.out.println("After merging {0,1} with {2,3}: sets=" + dsu.getSets());
        System.out.println("0 connected to 3: " + dsu.connected(0, 3));

        dsu.rollback();
        System.out.println("After rollback: sets=" + dsu.getSets());
        System.out.println("0 connected to 3: " + dsu.connected(0, 3));

        dsu.union(4, 5);
        dsu.union(3, 4);
        System.out.println("Final sets: " + dsu.getSets());
    }
}
