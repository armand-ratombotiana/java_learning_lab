package com.systemdesign.deep.lab05;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * Lab 05: Leader Election — Bully, Raft, ZooKeeper ephemeral-znode style,
 * lease-based leadership, and fencing tokens.
 */
public class LeaderElectionLab {

    static void sleep(int ms) {
        try { Thread.sleep(ms); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }

    static final Random RAND = ThreadLocalRandom.current();

    // ──────────────────────────────────────────────
    // 1. Bully Algorithm
    // ──────────────────────────────────────────────
    static class BullyElection {
        static class Node {
            final int id;
            final List<Node> allNodes;
            volatile int leaderId;
            volatile boolean alive = true;
            private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

            Node(int id, List<Node> allNodes) {
                this.id = id;
                this.allNodes = allNodes;
                this.leaderId = allNodes.stream().mapToInt(n -> n.id).max().orElse(id);
            }

            void startElection() {
                System.out.println("  [Bully] Node " + id + " starting election");
                var higher = allNodes.stream()
                        .filter(n -> n.id > id && n.alive)
                        .toList();
                if (higher.isEmpty()) {
                    becomeLeader();
                    return;
                }
                for (var h : higher) {
                    if (h.alive) {
                        System.out.println("  [Bully] Node " + id + " sends ELECTION to " + h.id);
                        h.receiveElection(this);
                    }
                }
                scheduler.schedule(() -> {
                    if (!alive) return;
                    boolean noResponse = higher.stream().noneMatch(n -> n.alive);
                    if (noResponse) becomeLeader();
                }, 200, TimeUnit.MILLISECONDS);
            }

            void receiveElection(Node from) {
                System.out.println("  [Bully] Node " + id + " received ELECTION from " + from.id);
                from.receiveOK(this);
                startElection();
            }

            void receiveOK(Node from) {
                System.out.println("  [Bully] Node " + id + " received OK from " + from.id);
            }

            void becomeLeader() {
                leaderId = id;
                System.out.println("  [Bully] Node " + id + " is now LEADER");
                for (var n : allNodes) {
                    if (n.id != id && n.alive) {
                        n.leaderId = id;
                    }
                }
            }

            void fail() {
                alive = false;
                System.out.println("  [Bully] Node " + id + " has FAILED");
            }

            void recover(List<Node> all) {
                alive = true;
                System.out.println("  [Bully] Node " + id + " has RECOVERED");
                startElection();
            }
        }

        static void demo() {
            System.out.println("=== Bully Algorithm ===");
            List<BullyElection.Node> nodes = new ArrayList<>();
            for (int i = 1; i <= 4; i++) {
                nodes.add(new BullyElection.Node(i, nodes));
            }
            // Initial leader (highest ID)
            var leader = nodes.stream().max(Comparator.comparingInt(n -> n.id)).orElseThrow();
            System.out.println("  Initial leader: Node " + leader.id);
            sleep(100);

            // Leader fails -> election triggered
            leader.fail();
            sleep(100);
            nodes.get(2).startElection(); // Node 3 starts election
            sleep(300);

            System.out.println("  Final leader: Node " + nodes.stream()
                    .filter(n -> n.alive).findFirst().orElseThrow().leaderId);
            System.out.println();
        }
    }

    // ──────────────────────────────────────────────
    // 2. Raft-inspired Leader Election
    // ──────────────────────────────────────────────
    static class RaftElection {

        enum Role { FOLLOWER, CANDIDATE, LEADER }

        static class RaftNode {
            final int id;
            final List<RaftNode> peers;
            volatile Role role = Role.FOLLOWER;
            volatile int term = 0;
            volatile int votedFor = -1;
            volatile int leaderId = -1;
            final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
            final AtomicReference<ScheduledFuture<?>> electionTimer = new AtomicReference<>();

            RaftNode(int id, List<RaftNode> peers) {
                this.id = id;
                this.peers = peers;
                resetElectionTimer();
            }

            void resetElectionTimer() {
                var prev = electionTimer.getAndSet(null);
                if (prev != null) prev.cancel(false);
                int timeout = 150 + RAND.nextInt(200); // 150-350ms
                var future = scheduler.schedule(this::startElection, timeout, TimeUnit.MILLISECONDS);
                electionTimer.set(future);
            }

            synchronized void startElection() {
                if (role == Role.LEADER) return;
                role = Role.CANDIDATE;
                term++;
                votedFor = id;
                int votes = 1; // vote for self
                System.out.println("  [Raft] Node " + id + " starts election for term " + term);

                for (var peer : peers) {
                    if (peer.id == id) continue;
                    if (peer.requestVote(term, id)) votes++;
                }

                int majority = peers.size() / 2 + 1;
                if (votes >= majority) {
                    becomeLeader();
                } else {
                    role = Role.FOLLOWER;
                    resetElectionTimer();
                }
            }

            synchronized boolean requestVote(int candidateTerm, int candidateId) {
                if (candidateTerm > term) {
                    term = candidateTerm;
                    role = Role.FOLLOWER;
                    votedFor = -1;
                    leaderId = -1;
                }
                if (candidateTerm == term && (votedFor == -1 || votedFor == candidateId)) {
                    votedFor = candidateId;
                    System.out.println("  [Raft] Node " + id + " votes for " + candidateId + " (term " + term + ")");
                    resetElectionTimer();
                    return true;
                }
                return false;
            }

            synchronized void becomeLeader() {
                role = Role.LEADER;
                leaderId = id;
                System.out.println("  [Raft] Node " + id + " is LEADER for term " + term);
                var prev = electionTimer.getAndSet(null);
                if (prev != null) prev.cancel(false);
                // Send heartbeats
                scheduler.scheduleAtFixedRate(() -> {
                    for (var peer : peers) {
                        if (peer.id != id) peer.receiveHeartbeat(term, id);
                    }
                }, 50, 100, TimeUnit.MILLISECONDS);
            }

            synchronized void receiveHeartbeat(int leaderTerm, int leaderId) {
                if (leaderTerm >= term) {
                    term = leaderTerm;
                    this.leaderId = leaderId;
                    if (role != Role.FOLLOWER) {
                        System.out.println("  [Raft] Node " + id + " stepping down, accepting leader " + leaderId);
                    }
                    role = Role.FOLLOWER;
                    resetElectionTimer();
                }
            }
        }

        static void demo() {
            System.out.println("=== Raft Leader Election ===");
            List<RaftNode> nodes = new ArrayList<>();
            for (int i = 0; i < 5; i++) nodes.add(new RaftNode(i, nodes));

            sleep(1500);

            var leader = nodes.stream().filter(n -> n.role == RaftNode.Role.LEADER).findFirst();
            leader.ifPresentOrElse(
                    l -> System.out.println("  Elected leader: Node " + l.id + " (term " + l.term + ")"),
                    () -> System.out.println("  No leader elected yet"));

            // Kill leader
            if (leader.isPresent()) {
                var oldLeader = leader.get();
                System.out.println("  Leader Node " + oldLeader.id + " fails...");
                oldLeader.scheduler.shutdownNow();
                oldLeader.role = RaftNode.Role.FOLLOWER;

                sleep(1500);
                var newLeader = nodes.stream()
                        .filter(n -> n.scheduler != null && !n.scheduler.isShutdown() && n.role == RaftNode.Role.LEADER)
                        .findFirst();
                newLeader.ifPresentOrElse(
                        l -> System.out.println("  New leader: Node " + l.id + " (term " + l.term + ")"),
                        () -> System.out.println("  No new leader elected"));
            }
            System.out.println();
        }
    }

    // ──────────────────────────────────────────────
    // 3. ZooKeeper-style Ephemeral Znode Election
    // ──────────────────────────────────────────────
    static class ZkElection {
        static class ZkSimulator {
            final Map<String, Long> znodes = new ConcurrentHashMap<>(); // path -> creation time
            final AtomicLong seqGen = new AtomicLong();
            final List<Runnable> watchers = new CopyOnWriteArrayList<>();

            synchronized String createEphemeralSequential(String prefix) {
                long seq = seqGen.incrementAndGet();
                String path = prefix + String.format("%010d", seq);
                znodes.put(path, System.currentTimeMillis());
                return path;
            }

            synchronized void delete(String path) {
                znodes.remove(path);
                watchers.forEach(Runnable::run);
            }

            synchronized List<String> getChildren(String prefix) {
                return znodes.keySet().stream()
                        .filter(k -> k.startsWith(prefix))
                        .sorted()
                        .toList();
            }

            void addWatcher(Runnable watcher) {
                watchers.add(watcher);
            }
        }

        static class ElectionNode {
            final int id;
            final ZkSimulator zk;
            String myZnode;
            volatile boolean isLeader = false;
            volatile String leaderId;

            ElectionNode(int id, ZkSimulator zk) {
                this.id = id;
                this.zk = zk;
                participate();
            }

            void participate() {
                myZnode = zk.createEphemeralSequential("/election/node-");
                System.out.println("  [ZK] Node " + id + " created " + myZnode);
                checkLeadership();
            }

            void checkLeadership() {
                var children = zk.getChildren("/election/node-");
                String smallest = children.get(0);
                isLeader = myZnode.equals(smallest);
                leaderId = smallest;
                if (isLeader) {
                    System.out.println("  [ZK] Node " + id + " is LEADER (" + myZnode + ")");
                } else {
                    System.out.println("  [ZK] Node " + id + " is follower, leader is " + smallest);
                }
            }

            void fail() {
                System.out.println("  [ZK] Node " + id + " fails, removing " + myZnode);
                zk.delete(myZnode);
            }
        }

        static void demo() {
            System.out.println("=== ZooKeeper-style Ephemeral Znode Election ===");
            var zk = new ZkSimulator();
            var nodes = new ArrayList<ElectionNode>();
            for (int i = 1; i <= 4; i++) {
                nodes.add(new ElectionNode(i, zk));
            }
            sleep(100);

            // Leader fails -> reelection
            var leader = nodes.stream().filter(n -> n.isLeader).findFirst();
            leader.ifPresent(l -> {
                System.out.println("  Current leader: Node " + l.id + " fails");
                l.fail();
                sleep(100);
                nodes.forEach(ElectionNode::checkLeadership);
            });
            System.out.println();
        }
    }

    // ──────────────────────────────────────────────
    // 4. Lease-based Leadership with Fencing Tokens
    // ──────────────────────────────────────────────
    static class LeaseElection {
        static class LeaseManager {
            final AtomicLong tokenGenerator = new AtomicLong(1);
            volatile String currentLeader = null;
            volatile long currentToken = 0;
            volatile long leaseExpiry = 0;
            final long leaseDurationMs = 300;

            synchronized boolean acquireLease(String nodeId) {
                long now = System.currentTimeMillis();
                if (currentLeader == null || now >= leaseExpiry) {
                    currentLeader = nodeId;
                    currentToken = tokenGenerator.incrementAndGet();
                    leaseExpiry = now + leaseDurationMs;
                    System.out.println("  [Lease] " + nodeId + " acquired lease with token " + currentToken);
                    return true;
                }
                return false;
            }

            synchronized boolean renewLease(String nodeId) {
                if (nodeId.equals(currentLeader)) {
                    leaseExpiry = System.currentTimeMillis() + leaseDurationMs;
                    System.out.println("  [Lease] " + nodeId + " renewed lease, expires at " + leaseExpiry);
                    return true;
                }
                return false;
            }

            synchronized boolean validateWrite(String nodeId, long token) {
                if (nodeId.equals(currentLeader) && token == currentToken
                        && System.currentTimeMillis() < leaseExpiry) {
                    return true;
                }
                System.out.println("  [Fence] REJECTED write from " + nodeId
                        + " (token " + token + ", current token " + currentToken + ")");
                return false;
            }

            synchronized String getLeader() { return currentLeader; }
            synchronized long getCurrentToken() { return currentToken; }
        }

        static class LeasedNode {
            final String id;
            final LeaseManager leaseManager;
            volatile boolean isLeader = false;
            volatile long fenceToken = 0;

            LeasedNode(String id, LeaseManager leaseManager) {
                this.id = id;
                this.leaseManager = leaseManager;
            }

            boolean tryBecomeLeader() {
                if (leaseManager.acquireLease(id)) {
                    isLeader = true;
                    fenceToken = leaseManager.getCurrentToken();
                    return true;
                }
                return false;
            }

            boolean renew() {
                if (isLeader) return leaseManager.renewLease(id);
                return false;
            }

            boolean write(String data) {
                if (!isLeader) {
                    System.out.println("  [Write] " + id + " is not leader, cannot write");
                    return false;
                }
                boolean allowed = leaseManager.validateWrite(id, fenceToken);
                System.out.println("  [Write] " + id + " writes '" + data
                        + "' — " + (allowed ? "ACCEPTED" : "REJECTED (fenced)"));
                return allowed;
            }

            void leaseExpired() {
                isLeader = false;
                System.out.println("  [Lease] " + id + " lease expired, stepping down");
            }
        }

        static void demo() {
            System.out.println("=== Lease-based Leadership with Fencing Tokens ===");
            var leaseManager = new LeaseManager();
            var nodeA = new LeasedNode("node-A", leaseManager);
            var nodeB = new LeasedNode("node-B", leaseManager);

            nodeA.tryBecomeLeader();
            nodeA.write("data-1");

            // Node A's lease expires
            leaseManager.leaseExpiry = System.currentTimeMillis() - 1;
            nodeA.leaseExpired();

            // Node B acquires lease
            nodeB.tryBecomeLeader();
            nodeB.write("data-2");

            // Node A tries to write with stale token (fenced)
            System.out.println("  Node A (old leader) attempts write with stale token...");
            nodeA.write("data-3");

            System.out.println();
        }
    }

    // ──────────────────────────────────────────────
    // Main
    // ──────────────────────────────────────────────
    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════════════╗");
        System.out.println("║  Lab 05: Leader Election Deep-Dive          ║");
        System.out.println("╚══════════════════════════════════════════════╝\n");

        BullyElection.demo();
        RaftElection.demo();
        ZkElection.demo();
        LeaseElection.demo();

        System.out.println("All leader election algorithms demonstrated successfully.");
    }
}
