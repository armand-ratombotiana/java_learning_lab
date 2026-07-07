package com.dsacademy.lab26.hyperloglog;

import java.util.Arrays;

public class HllRegister {

    private final byte[] registers;
    private final int m;

    public HllRegister(int precision) {
        this.m = 1 << precision;
        this.registers = new byte[m];
    }

    public void set(int index, byte value) {
        if (value > registers[index]) {
            registers[index] = value;
        }
    }

    public byte get(int index) {
        return registers[index];
    }

    public int getM() { return m; }

    public byte[] getRegisters() {
        return Arrays.copyOf(registers, registers.length);
    }

    public void merge(HllRegister other) {
        for (int i = 0; i < m; i++) {
            if (other.registers[i] > registers[i]) {
                registers[i] = other.registers[i];
            }
        }
    }

    public void reset() {
        Arrays.fill(registers, (byte) 0);
    }
}
