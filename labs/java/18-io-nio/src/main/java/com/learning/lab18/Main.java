package com.learning.lab18;

/**
 * Main entry point for Lab 18: I/O & NIO.
 */
public class Main {
    public static void main(String[] args) throws Exception {
        System.out.println("========================================");
        System.out.println("   Lab 18: I/O & NIO");
        System.out.println("========================================\n");

        FileReaderWriterExample.showFileReaderWriter();
        System.out.println();
        NioFilesExample.showNioFiles();
        System.out.println();
        ChannelExample.showChannels();

        System.out.println("\n========================================");
        System.out.println("   Lab 18 Complete");
        System.out.println("========================================");
    }
}
