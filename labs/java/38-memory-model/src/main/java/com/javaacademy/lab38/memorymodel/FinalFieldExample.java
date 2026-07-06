package com.javaacademy.lab38.memorymodel;

public class FinalFieldExample {

    private final int x;
    private final int y;
    private static FinalFieldExample instance;

    public FinalFieldExample() {
        x = 1;
        y = 2;
    }

    public void writer() {
        instance = new FinalFieldExample();
    }

    public int readerX() {
        FinalFieldExample inst = instance;
        if (inst != null) return inst.x;
        return -1;
    }

    public int readerY() {
        FinalFieldExample inst = instance;
        if (inst != null) return inst.y;
        return -1;
    }

    public static final class SafeFinalWrapper {
        private final int value;

        public SafeFinalWrapper(int value) {
            this.value = value;
        }

        public int getValue() { return value; }
    }

    private volatile SafeFinalWrapper safeInstance;

    public void safeWriter(int value) {
        safeInstance = new SafeFinalWrapper(value);
    }

    public int safeReader() {
        SafeFinalWrapper w = safeInstance;
        return w != null ? w.getValue() : -1;
    }

    public static FinalFieldExample getInstance() { return instance; }
    public int getX() { return x; }
    public int getY() { return y; }
}
