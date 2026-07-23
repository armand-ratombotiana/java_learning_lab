package com.leetcode.merkle;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

/**
 * Custom: Merkle Tree Implementation
 * Hash tree used for data verification in blockchain and distributed systems.
 *
 * Time Complexity: O(n) for building, O(log n) for proof verification
 * Space Complexity: O(n)
 */
public class MerkleTree {

    public static class Node {
        String hash;
        Node left, right;
        Node(String hash) { this.hash = hash; }
    }

    private Node root;
    private final List<String> leaves;

    public MerkleTree(List<String> data) {
        this.leaves = new ArrayList<>(data);
        this.root = buildTree(data);
    }

    private String hash(String data) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(data.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private Node buildTree(List<String> data) {
        List<Node> nodes = new ArrayList<>();
        for (String d : data) nodes.add(new Node(hash(d)));

        while (nodes.size() > 1) {
            List<Node> parents = new ArrayList<>();
            for (int i = 0; i < nodes.size(); i += 2) {
                Node left = nodes.get(i);
                Node right = (i + 1 < nodes.size()) ? nodes.get(i + 1) : left;
                Node parent = new Node(hash(left.hash + right.hash));
                parent.left = left;
                parent.right = right;
                parents.add(parent);
            }
            nodes = parents;
        }
        return nodes.isEmpty() ? null : nodes.get(0);
    }

    public String getRootHash() {
        return root == null ? "" : root.hash;
    }

    public static void main(String[] args) {
        List<String> data = List.of("tx1", "tx2", "tx3", "tx4");
        MerkleTree tree = new MerkleTree(data);
        System.out.println("Root hash: " + tree.getRootHash());

        MerkleTree tree2 = new MerkleTree(List.of("tx1", "tx2", "tx3", "tx4"));
        System.out.println("Same data, same root: " + tree.getRootHash().equals(tree2.getRootHash()) + " (expected: true)");

        MerkleTree tree3 = new MerkleTree(List.of("tx1", "tx2", "tx3", "tx5"));
        System.out.println("Different data, different root: " + !tree.getRootHash().equals(tree3.getRootHash()) + " (expected: true)");

        MerkleTree empty = new MerkleTree(List.of());
        System.out.println("Empty root: " + empty.getRootHash() + " (expected: )");
    }
}
