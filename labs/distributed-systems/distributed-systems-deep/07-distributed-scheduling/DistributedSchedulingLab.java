package com.distributedsystems.deep.lab07;

import java.security.MessageDigest;
import java.util.*;
import java.util.stream.*;

/**
 * DistributedSchedulingLab — implements consistent hashing, rendezvous hashing,
 * and power-of-two-choices scheduling.
 */
public class DistributedSchedulingLab {

    static class ConsistentHashRing {
        private final TreeMap<Integer, String> ring = new TreeMap<>();
        private final int vnodes;
        private final MessageDigest md5;
        ConsistentHashRing(int vnodes) throws Exception { this.vnodes = vnodes; this.md5 = MessageDigest.getInstance("MD5"); }
        void addNode(String node) { for (int i = 0; i < vnodes; i++) ring.put(hash(node + "#" + i), node); }
        void removeNode(String node) { for (int i = 0; i < vnodes; i++) ring.remove(hash(node + "#" + i)); }
        String getNode(String key) { if (ring.isEmpty()) return null;
            int h = hash(key); var e = ring.ceilingEntry(h); if (e == null) e = ring.firstEntry(); return e.getValue(); }
        int hash(String s) { byte[] d = md5.digest(s.getBytes()); return ((d[0] & 0xFF) << 24) | ((d[1] & 0xFF) << 16) | ((d[2] & 0xFF) << 8) | (d[3] & 0xFF); }
    }

    static class RendezvousHash {
        final Set<String> nodes = new HashSet<>();
        void addNode(String node) { nodes.add(node); }
        void removeNode(String node) { nodes.remove(node); }
        String getNode(String key) {
            String best = null; long bestHash = -1;
            for (var node : nodes) { long h = (key + "_" + node).hashCode() & 0xFFFFFFFFL; if (h > bestHash) { bestHash = h; best = node; } }
            return best;
        }
    }

    static class PowerOfTwoChoices {
        final Map<String, Integer> loads = new HashMap<>();
        void addNode(String node) { loads.put(node, 0); }
        String assign(List<String> candidates) {
            var rnd = new Random();
            String c1 = candidates.get(rnd.nextInt(candidates.size())), c2 = candidates.get(rnd.nextInt(candidates.size()));
            String chosen = loads.get(c1) <= loads.get(c2) ? c1 : c2;
            loads.merge(chosen, 1, Integer::sum); return chosen;
        }
    }

    public static void main(String[] args) throws Exception {
        var ring = new ConsistentHashRing(150);
        ring.addNode("A"); ring.addNode("B"); ring.addNode("C");
        Map<String,Integer> d = new HashMap<>();
        for (int i = 0; i < 100_000; i++) d.merge(ring.getNode("key-" + i), 1, Integer::sum);
        System.out.println("CH distribution: " + d);

        var rvh = new RendezvousHash(); rvh.addNode("A"); rvh.addNode("B"); rvh.addNode("C");
        Map<String,Integer> rd = new HashMap<>();
        for (int i = 0; i < 100_000; i++) rd.merge(rvh.getNode("key-" + i), 1, Integer::sum);
        System.out.println("HRW distribution: " + rd);

        var potc = new PowerOfTwoChoices(); potc.addNode("W1"); potc.addNode("W2"); potc.addNode("W3");
        for (int i = 0; i < 1000; i++) potc.assign(List.of("W1","W2","W3"));
        System.out.println("PoTC loads: " + potc.loads);
    }
}