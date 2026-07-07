package com.dsacademy.lab35.lct;

public final class LctNode {

    int id;
    LctNode left, right, parent;
    LctNode pathParent;
    int value;
    int aggregate;
    boolean reversed;

    public LctNode(int id) {
        this(id, 0);
    }

    public LctNode(int id, int value) {
        this.id = id;
        this.value = value;
        this.aggregate = value;
    }

    public int getId() {
        return id;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
        update();
    }

    public int getAggregate() {
        return aggregate;
    }

    public boolean isRoot() {
        return parent == null || (parent.left != this && parent.right != this);
    }

    void push() {
        if (reversed) {
            reversed = false;
            LctNode tmp = left;
            left = right;
            right = tmp;
            if (left != null) left.reversed ^= true;
            if (right != null) right.reversed ^= true;
        }
    }

    void update() {
        aggregate = value;
        if (left != null) aggregate += left.aggregate;
        if (right != null) aggregate += right.aggregate;
    }

    void rotate() {
        LctNode p = parent;
        LctNode g = p.parent;
        if (p.left == this) {
            p.left = right;
            if (right != null) right.parent = p;
            right = p;
        } else {
            p.right = left;
            if (left != null) left.parent = p;
            left = p;
        }
        p.parent = this;
        parent = g;
        if (g != null) {
            if (g.left == p) g.left = this;
            else if (g.right == p) g.right = this;
        }
        p.update();
        update();
    }

    void splay() {
        while (!isRoot()) {
            LctNode p = parent;
            LctNode g = p.parent;
            if (g != null) g.push();
            p.push();
            push();
            if (!p.isRoot()) {
                if ((g.left == p) == (p.left == this)) {
                    p.rotate();
                } else {
                    rotate();
                }
            }
            rotate();
        }
        push();
    }
}
