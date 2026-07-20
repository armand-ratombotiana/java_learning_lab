package com.capstone.agent;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

public class MultiAgentOrchestrator {
    private final Map<String, AgentRuntime> agents = new ConcurrentHashMap<>();
    private final Map<String, List<Message>> messageQueue = new ConcurrentHashMap<>();
    private final AtomicLong messageIdGen = new AtomicLong(0);
    private ExecutorService executor;

    public record Message(long id, String from, String to, String content, String type, long timestamp) {}

    public void registerAgent(String agentId, AgentRuntime agent) {
        agents.put(agentId, agent);
        messageQueue.put(agentId, new CopyOnWriteArrayList<>());
    }

    public void unregisterAgent(String agentId) {
        agents.remove(agentId);
        messageQueue.remove(agentId);
    }

    public void sendMessage(String from, String to, String content, String type) {
        Message msg = new Message(messageIdGen.incrementAndGet(), from, to, content, type, System.currentTimeMillis());
        messageQueue.computeIfAbsent(to, k -> new CopyOnWriteArrayList<>()).add(msg);
    }

    public List<Message> getMessages(String agentId) {
        return List.copyOf(messageQueue.getOrDefault(agentId, List.of()));
    }

    public void processMessages(String agentId) {
        List<Message> messages = messageQueue.get(agentId);
        if (messages == null) return;
        AgentRuntime agent = agents.get(agentId);
        if (agent == null) return;
        for (Message msg : List.copyOf(messages)) {
            agent.executeStep("Received from " + msg.from() + ": " + msg.content());
            messageQueue.get(agentId).remove(msg);
        }
    }

    public void orchestrate(String coordinatorId, List<String> agentIds, String task) {
        AgentRuntime coordinator = agents.get(coordinatorId);
        if (coordinator == null) throw new IllegalArgumentException("Coordinator not found");
        for (String agentId : agentIds) {
            if (!agents.containsKey(agentId)) throw new IllegalArgumentException("Agent not found: " + agentId);
        }
        for (String agentId : agentIds) {
            sendMessage(coordinatorId, agentId, task, "task");
        }
        if (executor == null) executor = Executors.newFixedThreadPool(agentIds.size());
        CountDownLatch latch = new CountDownLatch(agentIds.size());
        for (String agentId : agentIds) {
            executor.submit(() -> {
                try {
                    AgentRuntime agent = agents.get(agentId);
                    if (agent != null) agent.runLoop("Working on: " + task, 10);
                } finally {
                    latch.countDown();
                }
            });
        }
        try { latch.await(30, TimeUnit.SECONDS); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }

    public Collection<AgentRuntime> getAgents() { return agents.values(); }
    public int agentCount() { return agents.size(); }
    public boolean hasAgent(String agentId) { return agents.containsKey(agentId); }

    public void shutdown() {
        if (executor != null) executor.shutdown();
        agents.clear();
        messageQueue.clear();
    }
}
