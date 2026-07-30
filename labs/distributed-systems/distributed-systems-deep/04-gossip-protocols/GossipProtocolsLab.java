package com.distributedsystems.deep.lab04;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

/**
 * GossipProtocolsLab — implements push-gossip dissemination simulation,
 * SWIM-style failure detection, and convergence measurement.
 */
public class GossipProtocolsLab {

    static class GossipNode implements Runnable {
        final int id;
        final List<GossipNode> peers;
        final Set<String> seen = ConcurrentHashMap.newKeySet();
        volatile boolean alive = true;

        GossipNode(int id, List<GossipNode> peers) { this.id = id; this.peers = peers; }

        void gossip(String message) {
            if (seen.add(message)) {
                var unlucky = new ArrayList<>(peers);
                Collections.shuffle(unlucky, new Random(id));
                for (int i = 0; i < Math.min(3, unlucky.size()); i++) {
                    var peer = unlucky.get(i);
                    if (peer.id != id && peer.alive) peer.gossip(message);
                }
            }
        }

        boolean ping() {
            var target = peers.get(new Random().nextInt(peers.size()));
            if (target.id == id) return true;
            if (!target.alive) return false;
            var indirect = peers.get(new Random().nextInt(peers.size()));
            if (indirect.id == id || indirect.id == target.id) return target.alive;
            return indirect.alive && target.alive;
        }

        @Override
        public void run() {
            while (alive) {
                boolean ok = ping();
                if (!ok) System.out.println("  Node " + id + " suspects a peer is down");
                try { TimeUnit.MILLISECONDS.sleep(500); } catch (InterruptedException e) { break; }
            }
        }
    }

    public static void main(String[] args) throws Exception {
        int N = 50;
        System.out.println("=== Gossip Dissemination with " + N + " nodes ===");
        List<GossipNode> nodes = new ArrayList<>();
        for (int i = 0; i < N; i++) nodes.add(new GossipNode(i, nodes));
        var executors = new ArrayList<ExecutorService>();
        for (var node : nodes) { var e = Executors.newSingleThreadExecutor(); e.submit(node); executors.add(e); }

        String msg = "Hello from node 0";
        nodes.get(0).gossip(msg);
        TimeUnit.SECONDS.sleep(1);
        long reached = nodes.stream().filter(n -> n.seen.contains(msg)).count();
        System.out.println("Nodes reached: " + reached + "/" + N + " after ~1s");

        System.out.println("\n=== SWIM Failure Detection ===");
        nodes.get(5).alive = false;
        System.out.println("Killed Node 5");
        TimeUnit.SECONDS.sleep(1);
        long detected = nodes.stream().filter(n -> !n.ping()).count();
        System.out.println("Nodes that detected failure: " + detected);

        for (var e : executors) e.shutdown();
    }
}