package com.javaacademy.lab43.classloading;

import java.io.DataInputStream;
import java.io.InputStream;

/**
 * Reads and prints the class file structure: magic number, version, constant pool count,
 * access flags, and method count. Parses the binary .class format directly.
 */
public class BytecodeAnalyzer {

    public static void analyze(InputStream in) throws Exception {
        DataInputStream dis = new DataInputStream(in);

        int magic = dis.readInt();
        System.out.printf("Magic Number: 0x%08X (expected: 0xCAFEBABE)%n", magic);

        int minor = dis.readUnsignedShort();
        int major = dis.readUnsignedShort();
        System.out.println("Version: " + major + "." + minor + " (Java " + (major - 44) + ")");

        int cpCount = dis.readUnsignedShort();
        System.out.println("Constant Pool Count: " + cpCount);

        // Skip constant pool for brevity
        for (int i = 1; i < cpCount; i++) {
            byte tag = dis.readByte();
            switch (tag) {
                case 1 -> { int len = dis.readUnsignedShort(); dis.skipBytes(len); }
                case 3, 4 -> dis.skipBytes(4);
                case 5, 6 -> { dis.skipBytes(8); i++; }
                case 7, 8, 16, 19, 20 -> dis.skipBytes(2);
                case 9, 10, 11, 17, 18 -> dis.skipBytes(4);
                case 12 -> dis.skipBytes(4);
                case 15 -> dis.skipBytes(3);
                default -> throw new RuntimeException("Unknown tag: " + tag);
            }
        }

        int accessFlags = dis.readUnsignedShort();
        System.out.println("Access Flags: 0x" + Integer.toHexString(accessFlags));

        int thisClass = dis.readUnsignedShort();
        int superClass = dis.readUnsignedShort();
        System.out.println("This Class: #" + thisClass + ", Super Class: #" + superClass);

        int ifCount = dis.readUnsignedShort();
        System.out.println("Interface Count: " + ifCount);
        dis.skipBytes(ifCount * 2L);

        int fieldCount = dis.readUnsignedShort();
        System.out.println("Field Count: " + fieldCount);

        int methodCount = dis.readUnsignedShort();
        System.out.println("Method Count: " + methodCount);
    }

    public static void main(String[] args) throws Exception {
        System.out.println("=== Bytecode Analyzer ===\n");
        try (InputStream in = BytecodeAnalyzer.class.getResourceAsStream("BytecodeAnalyzer.class")) {
            analyze(in);
        }
    }
}
