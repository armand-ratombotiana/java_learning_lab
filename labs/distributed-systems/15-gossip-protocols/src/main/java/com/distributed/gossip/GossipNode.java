package com.distributed.gossip;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

public class GossipNode implements GossipProtocol {
    private final NodeId self;
    private final int fanout;
    private final long roundIntervalMs;
    private final Map<NodeId, MemberState> members = new ConcurrentHashMap<>();
    private final Map<String, byte[]> messageStore = new ConcurrentHashMap<>();
    private final AtomicLong messageCounter = new AtomicLong(0);
    private ScheduledExecutorService scheduler;
    private volatile boolean running = false;

    public enum State { ALIVE, SUSPECT, DEAD }

    public static class MemberState {
        volatile State state = State.ALIVE;
        final AtomicLong heartbeat = new AtomicLong(0);
        long lastContactTime = System.nanoTime();

        MemberState() { heartbeat.set(System.currentTimeMillis()); }
    }

    public GossipNode(NodeId self, List<NodeId> seeds, int fanout, long roundIntervalMs) {
        this.self = self;
        this.fanout = fanout;
        this.roundIntervalMs = roundIntervalMs;
        for (NodeId seed : seeds) {
            if (!seed.equals(self)) {
                members.put(seed, new MemberState());
            }
        }
    }

    @Override
    public void start() {
        running = true;
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(this::gossipRound, 0, roundIntervalMs, TimeUnit.MILLISECONDS);
    }

    @Override
    public void stop() {
        running = false;
        if (scheduler != null) scheduler.shutdown();
    }

    @Override
    public void broadcast(byte[] data) {
        String msgId = self.id() + "-" + messageCounter.incrementAndGet();
        messageStore.put(msgId, data);
    }

    @Override
    public void onMessage(NodeId from, byte[] data) {
        MemberState state = members.get(from);
        if (state != null) {
            state.lastContactTime = System.nanoTime();
            state.heartbeat.set(System.currentTimeMillis());
        }
    }

    @Override
    public List<NodeId> getKnownPeers() {
        return new ArrayList<>(members.keySet());
    }

    private void gossipRound() {
        if (!running || members.isEmpty()) return;
        List<NodeId> peers = new ArrayList<>(members.keySet());
        Collections.shuffle(peers);
        int count = Math.min(fanout, peers.size());
        for (int i = 0; i < count; i++) {
            NodeId peer = peers.get(i);
            MemberState state = members.get(peer);
            if (state != null) {
                state.lastContactTime = System.nanoTime();
            }
        }
    }

    public int getMemberCount() { return members.size(); }
    public boolean isRunning() { return running; }
    public NodeId getSelf() { return self; }
}
