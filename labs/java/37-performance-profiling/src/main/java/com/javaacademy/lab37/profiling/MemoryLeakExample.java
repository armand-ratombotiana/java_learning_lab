package com.javaacademy.lab37.profiling;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MemoryLeakExample {

    private static final List<byte[]> leakBucket = new ArrayList<>();
    private final Random random = new Random();

    public void leakMemory(int megabytes) {
        for (int i = 0; i < megabytes; i++) {
            byte[] chunk = new byte[1024 * 1024];
            random.nextBytes(chunk);
            leakBucket.add(chunk);
        }
    }

    public void leakWithListener() {
        List<Runnable> listeners = new ArrayList<>();
        for (int i = 0; i < 10000; i++) {
            final int id = i;
            listeners.add(() -> System.out.println("Listener " + id + " triggered"));
        }
    }

    public void leakStringIntern() {
        for (int i = 0; i < 100000; i++) {
            String s = ("String" + i).intern();
        }
    }

    public int getLeakBucketSize() { return leakBucket.size(); }

    public void clearLeak() { leakBucket.clear(); }
}
