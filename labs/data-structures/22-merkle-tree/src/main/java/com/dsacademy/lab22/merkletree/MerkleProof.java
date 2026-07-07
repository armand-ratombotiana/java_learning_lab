package com.dsacademy.lab22.merkletree;

import java.util.List;

public class MerkleProof {

    private final int leafIndex;
    private final List<String> auditPath;
    private final int totalLeaves;

    public MerkleProof(int leafIndex, List<String> auditPath, int totalLeaves) {
        this.leafIndex = leafIndex;
        this.auditPath = auditPath;
        this.totalLeaves = totalLeaves;
    }

    public int getLeafIndex() {
        return leafIndex;
    }

    public List<String> getAuditPath() {
        return auditPath;
    }

    public int getTotalLeaves() {
        return totalLeaves;
    }
}
