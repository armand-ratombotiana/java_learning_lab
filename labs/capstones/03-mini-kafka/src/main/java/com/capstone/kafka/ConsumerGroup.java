package com.capstone.kafka;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class ConsumerGroup {
    private final String groupId;
    private final Map<Integer, String> partitionAssignments = new ConcurrentHashMap<>();
    private final List<String> members = new CopyOnWriteArrayList<>();
    private String leaderId;

    public ConsumerGroup(String groupId) {
        this.groupId = groupId;
    }

    public void join(String memberId) {
        if (!members.contains(memberId)) members.add(memberId);
        if (leaderId == null) leaderId = memberId;
    }

    public void leave(String memberId) {
        members.remove(memberId);
        partitionAssignments.entrySet().removeIf(e -> e.getValue().equals(memberId));
        if (leaderId != null && leaderId.equals(memberId) && !members.isEmpty()) {
            leaderId = members.get(0);
        }
    }

    public void assignPartition(int partition, String memberId) {
        partitionAssignments.put(partition, memberId);
    }

    public void rebalance(List<Integer> partitions) {
        if (members.isEmpty()) return;
        partitionAssignments.clear();
        int partitionsPerMember = partitions.size() / members.size();
        int remainder = partitions.size() % members.size();
        int idx = 0;
        for (int i = 0; i < members.size(); i++) {
            String member = members.get(i);
            int count = partitionsPerMember + (i < remainder ? 1 : 0);
            for (int j = 0; j < count && idx < partitions.size(); j++) {
                partitionAssignments.put(partitions.get(idx++), member);
            }
        }
    }

    public String getAssignment(int partition) {
        return partitionAssignments.get(partition);
    }

    public Map<Integer, String> getAllAssignments() { return Map.copyOf(partitionAssignments); }

    public List<String> getMembers() { return List.copyOf(members); }

    public String getLeader() { return leaderId; }

    public String getGroupId() { return groupId; }

    public int memberCount() { return members.size(); }

    public boolean hasMember(String memberId) { return members.contains(memberId); }
}
