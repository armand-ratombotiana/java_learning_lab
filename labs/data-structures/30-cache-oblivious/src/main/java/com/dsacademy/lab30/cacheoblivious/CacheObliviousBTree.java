package com.dsacademy.lab30.cacheoblivious;

import java.util.ArrayList;
import java.util.List;

public class CacheObliviousBTree {

    private final int branchFactor;
    private final List<Integer> keys;
    private final List<CacheObliviousBTree> children;
    private boolean isLeaf;

    public CacheObliviousBTree(int branchFactor) {
        this.branchFactor = branchFactor;
        this.keys = new ArrayList<>();
        this.children = new ArrayList<>();
        this.isLeaf = true;
    }

    public void insert(int key) {
        CacheObliviousBTree root = this;
        if (root.keys.size() == branchFactor - 1) {
            CacheObliviousBTree newRoot = new CacheObliviousBTree(branchFactor);
            newRoot.isLeaf = false;
            newRoot.children.add(root);
            root.splitChild(newRoot, 0);
            root = newRoot;
        }
        root.insertNonFull(key);
    }

    private void insertNonFull(int key) {
        int i = keys.size() - 1;
        if (isLeaf) {
            while (i >= 0 && keys.get(i) > key) {
                i--;
            }
            keys.add(i + 1, key);
        } else {
            while (i >= 0 && keys.get(i) > key) {
                i--;
            }
            i++;
            CacheObliviousBTree child = children.get(i);
            if (child.keys.size() == branchFactor - 1) {
                child.splitChild(this, i);
                if (keys.get(i) < key) {
                    i++;
                }
            }
            children.get(i).insertNonFull(key);
        }
    }

    private void splitChild(CacheObliviousBTree parent, int childIndex) {
        CacheObliviousBTree child = parent.children.get(childIndex);
        CacheObliviousBTree newChild = new CacheObliviousBTree(branchFactor);
        newChild.isLeaf = child.isLeaf;
        int mid = (branchFactor - 1) / 2;
        int midKey = child.keys.get(mid);
        newChild.keys.addAll(child.keys.subList(mid + 1, child.keys.size()));
        child.keys.subList(mid, child.keys.size()).clear();
        if (!child.isLeaf) {
            newChild.children.addAll(child.children.subList(mid + 1, child.children.size()));
            child.children.subList(mid + 1, child.children.size()).clear();
        }
        parent.keys.add(childIndex, midKey);
        parent.children.add(childIndex + 1, newChild);
    }

    public boolean contains(int key) {
        int i = 0;
        while (i < keys.size() && key > keys.get(i)) {
            i++;
        }
        if (i < keys.size() && keys.get(i) == key) return true;
        if (isLeaf) return false;
        return children.get(i).contains(key);
    }

    public int size() {
        int count = keys.size();
        if (!isLeaf) {
            for (CacheObliviousBTree child : children) {
                count += child.size();
            }
        }
        return count;
    }

    public int height() {
        if (isLeaf) return 1;
        return 1 + children.get(0).height();
    }
}
