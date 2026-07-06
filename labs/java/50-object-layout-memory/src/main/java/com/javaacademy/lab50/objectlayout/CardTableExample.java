package com.javaacademy.lab50.objectlayout;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Simulates card table marking used by generational GCs (G1, Parallel).
 * The card table tracks which old-gen regions contain references to
 * young-gen objects, enabling efficient remembered set maintenance.
 * This demo models the marking process with a bitmap-based card table.
 */
public class CardTableExample {

    private static final int HEAP_SIZE = 1024 * 1024;   // 1M "cards"
    private static final int CARD_SIZE = 512;            // bytes per card
    private static final int CARD_TABLE_ENTRIES = HEAP_SIZE / CARD_SIZE;

    private final byte[] cardTable = new byte[CARD_TABLE_ENTRIES];
    private final AtomicInteger dirtyCount = new AtomicInteger();

    // Simulate a reference write from old-gen to young-gen
    public void writeReference(long oldGenAddr, long youngGenAddr) {
        int cardIndex = (int)(youngGenAddr / CARD_SIZE);
        if (cardIndex >= 0 && cardIndex < CARD_TABLE_ENTRIES) {
            if (cardTable[cardIndex] == 0) {
                cardTable[cardIndex] = 1; // mark dirty
                dirtyCount.incrementAndGet();
            }
        }
    }

    // Simulate GC card scanning (remembered set refinement)
    public int scanDirtyCards() {
        int scanned = 0;
        for (int i = 0; i < CARD_TABLE_ENTRIES; i++) {
            if (cardTable[i] != 0) {
                cardTable[i] = 0; // clear
                scanned++;
            }
        }
        dirtyCount.set(0);
        return scanned;
    }

    public static void main(String[] args) {
        var ct = new CardTableExample();

        // Simulate many old-to-young references
        for (int i = 0; i < 100_000; i++) {
            long oldAddr = (long)(Math.random() * HEAP_SIZE / 2);
            long youngAddr = HEAP_SIZE / 2 + (long)(Math.random() * HEAP_SIZE / 2);
            ct.writeReference(oldAddr, youngAddr);
        }

        System.out.println("Dirty cards: " + ct.dirtyCount.get());

        int scanned = ct.scanDirtyCards();
        System.out.println("Scanned cards during GC: " + scanned);

        System.out.println("Card table simulation complete.");
    }
}
