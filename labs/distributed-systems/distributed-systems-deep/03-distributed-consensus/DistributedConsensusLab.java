package com.distributedsystems.deep.lab03;

import java.util.*;
import java.util.concurrent.atomic.*;

/**
 * DistributedConsensusLab — simulates the Byzantine Generals Problem,
 * PBFT consensus phases, and FLP impossibility demonstration.
 */
public class DistributedConsensusLab {

    static Random rnd = new Random(42);

    static class ByzantineGenerals {
        static boolean loyalGeneralsReachAgreement(int totalGenerals, int traitors, String order) {
            int loyal = totalGenerals - traitors;
            Map<String, Integer> counts = new HashMap<>();
            for (int i = 0; i < loyal; i++) counts.merge(order, 1, Integer::sum);
            for (int i = 0; i < traitors; i++) {
                String fake = rnd.nextBoolean() ? "ATTACK" : "RETREAT";
                counts.merge(fake, 1, Integer::sum);
            }
            String decision = counts.entrySet().stream().max(Map.Entry.comparingByValue()).get().getKey();
            boolean agreed = decision.equals(order);
            System.out.println("Orders: " + counts + " -> Decision: " + decision + " " + (agreed ? "(correct)" : "(incorrect)"));
            return agreed;
        }
    }

    static class PbftNode {
        final int id;
        boolean faulty;
        int view = 0, seqNum = 0;
        String request = null;
        Set<String> prepares = new HashSet<>();

        PbftNode(int id, boolean faulty) { this.id = id; this.faulty = faulty; }

        boolean prePrepare(String req, int seq, int view) {
            if (faulty && rnd.nextBoolean()) return false;
            this.request = req; this.seqNum = seq; this.view = view;
            return true;
        }

        String prepare() {
            if (faulty && rnd.nextBoolean()) return "PREPARE(" + id + ",99,99)";
            return "PREPARE(" + id + "," + seqNum + "," + view + ")";
        }

        void receivePrepare(String msg) { prepares.add(msg); }
        int prepareCount() { return (int) prepares.stream().filter(m -> m.contains("," + seqNum + "," + view)).count(); }
    }

    public static void main(String[] args) {
        System.out.println("=== Byzantine Generals ===");
        ByzantineGenerals.loyalGeneralsReachAgreement(4, 1, "ATTACK");
        ByzantineGenerals.loyalGeneralsReachAgreement(4, 2, "ATTACK");

        System.out.println("\n=== PBFT Simulation ===");
        var nodes = List.of(
            new PbftNode(0, false), new PbftNode(1, false),
            new PbftNode(2, false), new PbftNode(3, true));
        for (int i = 1; i < nodes.size(); i++) nodes.get(i).prePrepare("SET X=1", 1, 0);
        for (var node : nodes) { String msg = node.prepare(); for (var other : nodes) other.receivePrepare(msg); }
        int q = 3;
        for (var node : nodes) {
            if (!node.faulty) System.out.println("Node " + node.id + " prepares: " + node.prepareCount() + " (need " + q + ")");
        }
        System.out.println("\n=== FLP Impossibility ===");
        System.out.println("No deterministic async consensus can guarantee termination with 1 crash");
    }
}