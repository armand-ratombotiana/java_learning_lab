package com.dsacademy.lab35.lct;

public final class DynamicConnectivityLCT {

    private final LinkCutTree lct;

    public DynamicConnectivityLCT() {
        this.lct = new LinkCutTree();
    }

    public void addVertex(int id, int value) {
        lct.makeNode(id, value);
    }

    public void addVertex(int id) {
        addVertex(id, 0);
    }

    public void addEdge(int u, int v) {
        if (lct.connected(u, v)) return;
        lct.makeRoot(u);
        lct.link(u, v);
    }

    public void removeEdge(int u, int v) {
        if (!lct.connected(u, v)) return;
        lct.cut(u, v);
    }

    public boolean isConnected(int u, int v) {
        return lct.connected(u, v);
    }

    public int pathSum(int u, int v) {
        return lct.pathAggregate(u, v);
    }

    public void updateVertex(int id, int newValue) {
        lct.updateNode(id, newValue);
    }

    public int getVertexCount() {
        return lct.size();
    }
}
