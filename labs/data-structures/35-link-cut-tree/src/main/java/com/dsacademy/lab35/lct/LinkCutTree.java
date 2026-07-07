package com.dsacademy.lab35.lct;

import java.util.HashMap;
import java.util.Map;

public class LinkCutTree {

    private final Map<Integer, LctNode> nodes;

    public LinkCutTree() {
        this.nodes = new HashMap<>();
    }

    public LctNode makeNode(int id) {
        return makeNode(id, 0);
    }

    public LctNode makeNode(int id, int value) {
        if (nodes.containsKey(id)) {
            throw new IllegalArgumentException("Node " + id + " already exists");
        }
        LctNode node = new LctNode(id, value);
        nodes.put(id, node);
        return node;
    }

    public LctNode getNode(int id) {
        return nodes.get(id);
    }

    public void link(int childId, int parentId) {
        LctNode child = nodes.get(childId);
        LctNode parent = nodes.get(parentId);
        if (child == null || parent == null) {
            throw new IllegalArgumentException("Node not found");
        }
        access(child);
        access(parent);
        child.left = parent;
        parent.parent = child;
        child.update();
    }

    public void cut(int id) {
        LctNode node = nodes.get(id);
        if (node == null) throw new IllegalArgumentException("Node not found");
        access(node);
        if (node.left != null) {
            node.left.parent = null;
            node.left = null;
            node.update();
        }
    }

    public void cut(int uId, int vId) {
        LctNode u = nodes.get(uId);
        LctNode v = nodes.get(vId);
        if (u == null || v == null) throw new IllegalArgumentException("Node not found");
        makeRoot(u);
        access(v);
        if (v.left == u || v.right == u) {
            cut(uId);
        }
    }

    public boolean connected(int uId, int vId) {
        if (uId == vId) return true;
        LctNode u = nodes.get(uId);
        LctNode v = nodes.get(vId);
        if (u == null || v == null) return false;
        return findRoot(u) == findRoot(v);
    }

    public LctNode findRoot(int id) {
        LctNode node = nodes.get(id);
        if (node == null) return null;
        return findRoot(node);
    }

    private LctNode findRoot(LctNode node) {
        access(node);
        while (node.left != null) {
            node = node.left;
        }
        splay(node);
        return node;
    }

    public void makeRoot(int id) {
        LctNode node = nodes.get(id);
        if (node == null) throw new IllegalArgumentException("Node not found");
        makeRoot(node);
    }

    private void makeRoot(LctNode node) {
        access(node);
        node.reversed ^= true;
    }

    public int pathAggregate(int uId, int vId) {
        LctNode u = nodes.get(uId);
        LctNode v = nodes.get(vId);
        if (u == null || v == null) throw new IllegalArgumentException("Node not found");
        makeRoot(u);
        access(v);
        return v.aggregate;
    }

    public void updateNode(int id, int newValue) {
        LctNode node = nodes.get(id);
        if (node == null) throw new IllegalArgumentException("Node not found");
        access(node);
        node.value = newValue;
        node.update();
    }

    public void access(LctNode node) {
        LctNode last = null;
        for (LctNode cur = node; cur != null; cur = cur.pathParent) {
            splay(cur);
            cur.right = last;
            cur.update();
            last = cur;
        }
        splay(node);
    }

    private void splay(LctNode node) {
        node.splay();
    }

    public int size() {
        return nodes.size();
    }
}
