package com.dsacademy.lab22.merkletree;

import java.util.List;

public class MerkleTreeExample {

    public static void main(String[] args) {
        List<String> transactions = List.of(
            "Alice pays Bob 10 BTC",
            "Bob pays Carol 5 BTC",
            "Carol pays Dave 2 BTC",
            "Dave pays Alice 1 BTC",
            "Eve pays Frank 8 BTC"
        );

        MerkleTree tree = new MerkleTree(transactions);
        String rootHash = tree.getRootHash();
        System.out.println("Merkle Root: " + rootHash);
        System.out.println("Leaf count: " + tree.getLeafCount());

        int leafIndex = 2;
        MerkleProof proof = tree.generateProof(leafIndex);
        MerkleVerifier verifier = new MerkleVerifier();
        boolean valid = verifier.verifyProof(transactions.get(leafIndex), proof, rootHash);
        System.out.println("Proof for transaction " + leafIndex + " is valid: " + valid);

        boolean tampered = verifier.verifyProof("Tampered data", proof, rootHash);
        System.out.println("Tampered proof is valid: " + tampered);
    }
}
