package com.distributedsystems.deep.lab01;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

/**
 * ConsensusAlgorithmsLab — simulates Raft leader election with randomized
 * timeouts and log replication across a 3-node cluster.
 */
public class ConsensusAlgorithmsLab {

    enum Role { FOLLOWER, CANDIDATE, LEADER }

    static class RaftNode {
        final int id;
        volatile Role role = Role.FOLLOWER;
        volatile int term = 0;
        volatile int votedFor = -1;
        volatile int leaderId = -1;
        final List<String> log = new ArrayList<>();
        volatile int commitLength = 0;
        final AtomicInteger votesReceived = new AtomicInteger(0);
        final ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();
        final Random rnd = new Random();

        RaftNode(int id) { this.id = id; }

        void start() { scheduleElectionTimeout(); }

        void scheduleElectionTimeout() {
            int timeout = 150 + rnd.nextInt(150);
            exec.schedule(this::startElection, timeout, TimeUnit.MILLISECONDS);
        }

        void startElection() {
            if (role == Role.LEADER) return;
            role = Role.CANDIDATE;
            term++;
            votedFor = id;
            votesReceived.set(1);
            System.out.println("Node " + id + " starting election for term " + term);
            for (var peer : peers) {
                if (peer.id == id) continue;
                peer.handleRequestVote(term, id);
            }
        }

        synchronized void handleRequestVote(int candidateTerm, int candidateId) {
            if (candidateTerm > term) {
                term = candidateTerm;
                role = Role.FOLLOWER;
                votedFor = -1;
            }
            if (candidateTerm == term && votedFor == -1) {
                votedFor = candidateId;
                candidateVoteReceived(candidateId);
            }
        }

        void candidateVoteReceived(int candidateId) {
            if (candidateId != id) return;
            if (votesReceived.incrementAndGet() > peers.size() / 2) becomeLeader();
        }

        void becomeLeader() {
            role = Role.LEADER;
            leaderId = id;
            System.out.println("Node " + id + " becomes LEADER for term " + term);
            for (var peer : peers) {
                if (peer.id != id) peer.handleLeaderHeartbeat(term, id);
            }
        }

        void handleLeaderHeartbeat(int leaderTerm, int leaderId) {
            if (leaderTerm >= term) {
                term = leaderTerm;
                role = Role.FOLLOWER;
                this.leaderId = leaderId;
            }
        }

        static List<RaftNode> peers;
    }

    public static void main(String[] args) throws Exception {
        List<RaftNode> nodes = new ArrayList<>();
        for (int i = 0; i < 3; i++) nodes.add(new RaftNode(i + 1));
        RaftNode.peers = nodes;

        for (var node : nodes) node.start();
        TimeUnit.SECONDS.sleep(2);

        var leader = nodes.stream().filter(n -> n.role == RaftNode.Role.LEADER).findFirst();
        System.out.println("\nElection result: " + (leader.isPresent() ? "Leader is Node " + leader.get().id : "No leader"));
        System.out.println("Terms: " + nodes.stream().map(n -> Integer.toString(n.term)).toList());

        if (leader.isPresent()) {
            int lid = leader.get().id;
            System.out.println("\nLeader " + lid + " proposes log entry 'x=1'");
            for (var node : nodes) {
                if (node.id != lid) { node.log.add("x=1"); System.out.println("  Replicated to Node " + node.id); }
            }
            leader.get().log.add("x=1");
            System.out.println("Logs: " + nodes.stream().map(n -> n.log.toString()).toList());
        }

        for (var node : nodes) node.exec.shutdown();
    }
}