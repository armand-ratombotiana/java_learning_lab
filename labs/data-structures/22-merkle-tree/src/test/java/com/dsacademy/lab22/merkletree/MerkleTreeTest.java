package com.dsacademy.lab22.merkletree;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.util.List;

public class MerkleTreeTest {

    @Test
    void testSingleLeaf() {
        MerkleTree tree = new MerkleTree(List.of("data"));
        assertNotNull(tree.getRootHash());
        assertFalse(tree.getRootHash().isEmpty());
        assertEquals(1, tree.getLeafCount());
    }

    @Test
    void testMultipleLeaves() {
        List<String> data = List.of("a", "b", "c", "d");
        MerkleTree tree = new MerkleTree(data);
        assertEquals(4, tree.getLeafCount());
        assertNotNull(tree.getRootHash());
    }

    @Test
    void testProofVerification() {
        List<String> data = List.of("tx1", "tx2", "tx3", "tx4");
        MerkleTree tree = new MerkleTree(data);
        String root = tree.getRootHash();
        MerkleVerifier verifier = new MerkleVerifier();

        for (int i = 0; i < data.size(); i++) {
            MerkleProof proof = tree.generateProof(i);
            assertTrue(verifier.verifyProof(data.get(i), proof, root));
        }
    }

    @Test
    void testTamperedDataFails() {
        List<String> data = List.of("valid1", "valid2", "valid3");
        MerkleTree tree = new MerkleTree(data);
        String root = tree.getRootHash();
        MerkleVerifier verifier = new MerkleVerifier();
        MerkleProof proof = tree.generateProof(1);
        assertFalse(verifier.verifyProof("tampered", proof, root));
    }

    @Test
    void testWrongRootFails() {
        List<String> data = List.of("x", "y");
        MerkleTree tree = new MerkleTree(data);
        MerkleVerifier verifier = new MerkleVerifier();
        MerkleProof proof = tree.generateProof(0);
        assertFalse(verifier.verifyProof("x", proof, "0000000000000000000000000000000000000000000000000000000000000000"));
    }

    @Test
    void testOddNumberOfLeaves() {
        List<String> data = List.of("a", "b", "c");
        MerkleTree tree = new MerkleTree(data);
        assertEquals(3, tree.getLeafCount());
        assertNotNull(tree.getRootHash());
        MerkleProof proof = tree.generateProof(2);
        MerkleVerifier verifier = new MerkleVerifier();
        assertTrue(verifier.verifyProof("c", proof, tree.getRootHash()));
    }

    @Test
    void testEmptyTree() {
        List<String> data = List.of();
        MerkleTree tree = new MerkleTree(data);
        assertEquals("", tree.getRootHash());
    }
}
