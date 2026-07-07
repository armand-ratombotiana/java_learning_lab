package com.dsacademy.lab24.xorunrolled;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

public class UnrolledLinkedList<T> implements Iterable<T> {

    private final int blockSize;
    private List<List<T>> blocks;
    private int size;

    public UnrolledLinkedList(int blockSize) {
        if (blockSize < 2) throw new IllegalArgumentException("Block size must be >= 2");
        this.blockSize = blockSize;
        this.blocks = new ArrayList<>();
        this.size = 0;
    }

    public void add(T value) {
        if (blocks.isEmpty() || blocks.get(blocks.size() - 1).size() >= blockSize) {
            blocks.add(new ArrayList<>());
        }
        blocks.get(blocks.size() - 1).add(value);
        size++;
    }

    public void add(int index, T value) {
        if (index < 0 || index > size) throw new IndexOutOfBoundsException();
        if (index == size) { add(value); return; }
        int[] pos = findBlock(index);
        List<T> block = blocks.get(pos[0]);
        block.add(pos[1], value);
        size++;
        rebalance(pos[0]);
    }

    public T get(int index) {
        if (index < 0 || index >= size) throw new IndexOutOfBoundsException();
        int[] pos = findBlock(index);
        return blocks.get(pos[0]).get(pos[1]);
    }

    public T remove(int index) {
        if (index < 0 || index >= size) throw new IndexOutOfBoundsException();
        int[] pos = findBlock(index);
        T val = blocks.get(pos[0]).remove(pos[1]);
        size--;
        if (blocks.get(pos[0]).isEmpty()) {
            blocks.remove(pos[0]);
        }
        return val;
    }

    public int size() { return size; }

    private int[] findBlock(int index) {
        int count = 0;
        for (int i = 0; i < blocks.size(); i++) {
            if (count + blocks.get(i).size() > index) {
                return new int[]{i, index - count};
            }
            count += blocks.get(i).size();
        }
        throw new IndexOutOfBoundsException();
    }

    private void rebalance(int blockIdx) {
        while (blockIdx < blocks.size() && blocks.get(blockIdx).size() > blockSize) {
            List<T> block = blocks.get(blockIdx);
            List<T> newBlock = new ArrayList<>(block.subList(blockSize / 2, block.size()));
            block.subList(blockSize / 2, block.size()).clear();
            blocks.add(blockIdx + 1, newBlock);
            blockIdx++;
        }
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            int blockIdx = 0, elemIdx = 0;
            @Override
            public boolean hasNext() { return blockIdx < blocks.size() && elemIdx < blocks.get(blockIdx).size(); }
            @Override
            public T next() {
                if (!hasNext()) throw new NoSuchElementException();
                T val = blocks.get(blockIdx).get(elemIdx);
                elemIdx++;
                if (elemIdx >= blocks.get(blockIdx).size()) {
                    blockIdx++;
                    elemIdx = 0;
                }
                return val;
            }
        };
    }
}
