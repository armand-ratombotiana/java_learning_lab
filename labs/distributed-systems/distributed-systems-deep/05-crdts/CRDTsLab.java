package com.distributedsystems.deep.lab05;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * CRDTsLab — implements G-Counter, PN-Counter, G-Set, 2P-Set, and LWW-Register
 * with merge resolution.
 */
public class CRDTsLab {

    static class GCounter {
        final int[] values; final int replicaId;
        GCounter(int count, int id) { this.values = new int[count]; this.replicaId = id; }
        void increment() { values[replicaId]++; }
        int value() { return Arrays.stream(values).sum(); }
        void merge(GCounter other) { for (int i = 0; i < values.length; i++) values[i] = Math.max(values[i], other.values[i]); }
    }

    static class PNCounter {
        final GCounter p, n;
        PNCounter(int count, int id) { p = new GCounter(count, id); n = new GCounter(count, id); }
        void increment() { p.increment(); }
        void decrement() { n.increment(); }
        int value() { return p.value() - n.value(); }
        void merge(PNCounter other) { p.merge(other.p); n.merge(other.n); }
    }

    static class GSet { final Set<String> elements = ConcurrentHashMap.newKeySet();
        void add(String e) { elements.add(e); }
        Set<String> value() { return new HashSet<>(elements); }
        void merge(GSet other) { elements.addAll(other.elements); }
    }

    static class TwoPhaseSet {
        final GSet adds = new GSet(), removes = new GSet();
        boolean add(String e) { if (removes.elements.contains(e)) return false; adds.add(e); return true; }
        boolean remove(String e) { if (!adds.elements.contains(e)) return false; removes.add(e); return true; }
        Set<String> value() { var r = new HashSet<>(adds.value()); r.removeAll(removes.value()); return r; }
        void merge(TwoPhaseSet other) { adds.merge(other.adds); removes.merge(other.removes); }
    }

    static class LWWRegister {
        String value; long timestamp;
        LWWRegister(String v, long ts) { value = v; timestamp = ts; }
        void assign(String v, long ts) { if (ts > timestamp) { value = v; timestamp = ts; } }
        void merge(LWWRegister other) { if (other.timestamp > timestamp) { value = other.value; timestamp = other.timestamp; } }
        String value() { return value; }
    }

    public static void main(String[] args) {
        GCounter c1 = new GCounter(3,0), c2 = new GCounter(3,1), c3 = new GCounter(3,2);
        c1.increment(); c1.increment(); c2.increment(); c3.increment(); c3.increment(); c3.increment();
        c1.merge(c2); c1.merge(c3);
        System.out.println("G-Counter (expect 6): " + c1.value());

        PNCounter pc = new PNCounter(2,0); pc.increment(); pc.increment(); pc.decrement();
        System.out.println("PN-Counter (expect 1): " + pc.value());

        TwoPhaseSet s = new TwoPhaseSet(); s.add("a"); s.add("b"); s.remove("b");
        System.out.println("2P-Set after add(a),add(b),remove(b): " + s.value());

        LWWRegister r1 = new LWWRegister("A",100), r2 = new LWWRegister("B",200);
        r1.merge(r2);
        System.out.println("LWW (merge A@100, B@200): " + r1.value());
    }
}