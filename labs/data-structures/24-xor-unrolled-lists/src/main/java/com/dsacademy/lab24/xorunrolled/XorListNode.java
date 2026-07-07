package com.dsacademy.lab24.xorunrolled;

public class XorListNode<T> {
    T value;
    long xorPointer;

    XorListNode(T value) {
        this.value = value;
        this.xorPointer = 0;
    }
}
