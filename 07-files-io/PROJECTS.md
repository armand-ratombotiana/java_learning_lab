# Java Files I/O Module - PROJECTS.md

---

# 🎯 Mini-Project: File Management Utility

## Project Overview

**Duration**: 3-4 hours  
**Difficulty**: Intermediate  
**Concepts Used**: File I/O, Streams, Buffers, Serialization, Path API

This project demonstrates Java file I/O through a practical file management utility.

---

## Project Structure

```
07-files-io/src/main/java/com/learning/project/
├── Main.java
├── model/
│   └── FileInfo.java
├── service/
│   └── FileService.java
├── util/
│   └── FileUtils.java
└── ui/
    └── FileMenu.java
```

---

## Step 1: FileInfo Model

```java
// model/FileInfo.java
package com.learning.project.model;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.io.File;
import java.io.Serializable;

public class FileInfo implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String name;
    private String path;
    private long size;
    private LocalDateTime lastModified;
    private boolean isDirectory;
    private String extension;
    
    public FileInfo(File file) {
        this.name = file.getName();
        this.path = file.getAbsolutePath();
        this.size = file.length();
        this.lastModified = LocalDateTime.ofInstant(
            java.time.Instant.ofEpochMilli(file.lastModified()),
            ZoneId.systemDefault()
        );
        this.isDirectory = file.isDirectory();
        
        if (!isDirectory && name.contains(".")) {
            this.extension = name.substring(name.lastIndexOf('.') + 1);
        } else {
            this.extension = "";
        }
    }
    
    public String getName() { return name; }
    public String getPath() { return path; }
    public long getSize() { return size; }
    public LocalDateTime getLastModified() { return lastModified; }
    public boolean isDirectory() { return isDirectory; }
    public String getExtension() { return extension; }
    
    public String getFormattedSize() {
        if (isDirectory) return "DIR";
        
        if (size < 1024) return size + " B";
        if (size < 1024 * 1024) return String.format("%.1f KB", size / 1024.0);
        if (size < 1024 * 1024 * 1024) return String.format("%.1f MB", size / (1024.0 * 1024));
        return String.format("%.1f GB", size / (1024.0 * 1024 * 1024));
    }
    
    @Override
    public String toString() {
        return String.format("%-30s %10s %s", name, getFormattedSize(), lastModified);
    }
}
```

---

## Step 2: File Service

```java
// service/FileService.java
package com.learning.project.service;

import com.learning.project.model.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

public class FileService {
    private final Path currentDirectory;
    
    public FileService() {
        this.currentDirectory = Paths.get(System.getProperty("user.dir"));
    }
    
    public FileService(String directory) {
        this.currentDirectory = Paths.get(directory);
    }
    
    public List<FileInfo> listFiles() throws IOException {
        File dir = currentDirectory.toFile();
        File[] files = dir.listFiles();
        
        if (files == null) return new ArrayList<>();
        
        return Arrays.stream(files)
            .map(FileInfo::new)
            .sorted(Comparator.comparing(FileInfo::isDirectory).reversed()
                .thenComparing(FileInfo::getName))
            .collect(Collectors.toList());
    }
    
    public List<FileInfo> listFiles(String pattern) throws IOException {
        return listFiles().stream()
            .filter(f -> f.getName().contains(pattern))
            .collect(Collectors.toList());
    }
    
    public List<FileInfo> listFilesByExtension(String extension) throws IOException {
        return listFiles().stream()
            .filter(f -> f.getExtension().equalsIgnoreCase(extension))
            .collect(Collectors.toList());
    }
    
    public boolean createFile(String filename) throws IOException {
        Path path = currentDirectory.resolve(filename);
        return Files.createFile(path) != null;
    }
    
    public boolean createDirectory(String dirname) throws IOException {
        Path path = currentDirectory.resolve(dirname);
        return Files.createDirectory(path) != null;
    }
    
    public boolean deleteFile(String filename) throws IOException {
        Path path = currentDirectory.resolve(filename);
        return Files.deleteIfExists(path);
    }
    
    public boolean moveFile(String source, String destination) throws IOException {
        Path src = currentDirectory.resolve(source);
        Path dest = currentDirectory.resolve(destination);
        Files.move(src, dest, StandardCopyOption.REPLACE_EXISTING);
        return true;
    }
    
    public boolean copyFile(String source, String destination) throws IOException {
        Path src = currentDirectory.resolve(source);
        Path dest = currentDirectory.resolve(destination);
        Files.copy(src, dest, StandardCopyOption.REPLACE_EXISTING);
        return true;
    }
    
    public String readTextFile(String filename) throws IOException {
        Path path = currentDirectory.resolve(filename);
        return Files.readString(path);
    }
    
    public void writeTextFile(String filename, String content) throws IOException {
        Path path = currentDirectory.resolve(filename);
        Files.writeString(path, content);
    }
    
    public void appendTextFile(String filename, String content) throws IOException {
        Path path = currentDirectory.resolve(filename);
        Files.writeString(path, content + System.lineSeparator(), 
            StandardOpenOption.CREATE, StandardOpenOption.APPEND);
    }
    
    public List<String> readLines(String filename) throws IOException {
        Path path = currentDirectory.resolve(filename);
        return Files.readAllLines(path);
    }
    
    public void writeLines(String filename, List<String> lines) throws IOException {
        Path path = currentDirectory.resolve(filename);
        Files.write(path, lines);
    }
    
    public long getTotalSize() throws IOException {
        return listFiles().stream()
            .filter(f -> !f.isDirectory())
            .mapToLong(FileInfo::getSize)
            .sum();
    }
    
    public Map<String, Long> getSizeByExtension() throws IOException {
        return listFiles().stream()
            .filter(f -> !f.isDirectory())
            .collect(Collectors.groupingBy(
                FileInfo::getExtension,
                Collectors.summingLong(FileInfo::getSize)
            ));
    }
    
    public Path getCurrentDirectory() {
        return currentDirectory;
    }
    
    public boolean changeDirectory(String path) throws IOException {
        if (path.equals("..")) {
            return true; // Handle in caller
        }
        Path newPath = currentDirectory.resolve(path);
        if (Files.exists(newPath) && Files.isDirectory(newPath)) {
            return true;
        }
        return false;
    }
    
    public long getDirectorySize() throws IOException {
        return Files.walk(currentDirectory)
            .filter(p -> p.toFile().isFile())
            .mapToLong(p -> p.toFile().length())
            .sum();
    }
    
    public List<FileInfo> findFiles(String searchTerm) throws IOException {
        return Files.walk(currentDirectory)
            .filter(p -> p.toFile().isFile())
            .map(Path::toFile)
            .map(FileInfo::new)
            .filter(f -> f.getName().toLowerCase().contains(searchTerm.toLowerCase()))
            .collect(Collectors.toList());
    }
}
```

---

## Step 3: File Utilities

```java
// util/FileUtils.java
package com.learning.project.util;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class FileUtils {
    
    public static void copyDirectory(Path source, Path target) throws IOException {
        Files.walk(source).forEach(sourcePath -> {
            try {
                Path targetPath = target.resolve(source.relativize(sourcePath));
                if (Files.isDirectory(sourcePath)) {
                    Files.createDirectory(targetPath);
                } else {
                    Files.copy(sourcePath, targetPath, 
                        StandardCopyOption.REPLACE_EXISTING);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
    
    public static void deleteDirectory(Path path) throws IOException {
        Files.walk(path)
            .sorted(Comparator.reverseOrder())
            .forEach(p -> {
                try {
                    Files.delete(p);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
    }
    
    public static String getFileExtension(String filename) {
        if (filename == null || filename.isEmpty()) return "";
        int lastDot = filename.lastIndexOf('.');
        return lastDot > 0 ? filename.substring(lastDot + 1) : "";
    }
    
    public static String getFilenameWithoutExtension(String filename) {
        if (filename == null || filename.isEmpty()) return "";
        int lastDot = filename.lastIndexOf('.');
        return lastDot > 0 ? filename.substring(0, lastDot) : filename;
    }
    
    public static boolean isTextFile(String filename) {
        String ext = getFileExtension(filename).toLowerCase();
        Set<String> textExtensions = Set.of(
            "txt", "md", "java", "py", "js", "html", "css", 
            "xml", "json", "csv", "log", "properties"
        );
        return textExtensions.contains(ext);
    }
    
    public static long calculateChecksum(Path path) throws IOException {
        return Files.lines(path)
            .mapToInt(String::hashCode)
            .sum();
    }
    
    public static List<String> findDuplicates(List<Path> paths) {
        try {
            return paths.stream()
                .collect(java.util.stream.Collectors.groupingBy(
                    p -> {
                        try {
                            return calculateChecksum(p);
                        } catch (IOException e) {
                            return 0L;
                        }
                    },
                    java.util.stream.Collectors.mapping(
                        java.util.function.Function.identity(),
                        java.util.stream.Collectors.toList()
                    )
                ))
                .values().stream()
                .filter(list -> list.size() > 1)
                .flatMap(List::stream)
                .map(Path::toString)
                .collect(java.util.stream.Collectors.toList());
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }
    
    public static void compressFile(Path source, Path target) throws IOException {
        try (InputStream is = Files.newInputStream(source);
             OutputStream os = Files.newOutputStream(target);
             java.util.zip.GZIPOutputStream gzos = new java.util.zip.GZIPOutputStream(os)) {
            
            byte[] buffer = new byte[1024];
            int len;
            while ((len = is.read(buffer)) > 0) {
                gzos.write(buffer, 0, len);
            }
        }
    }
    
    public static void decompressFile(Path source, Path target) throws IOException {
        try (InputStream is = Files.newInputStream(source);
             java.util.zip.GZIPInputStream gzis = new java.util.zip.GZIPInputStream(is);
             OutputStream os = Files.newOutputStream(target)) {
            
            byte[] buffer = new byte[1024];
            int len;
            while ((len = gzis.read(buffer)) > 0) {
                os.write(buffer, 0, len);
            }
        }
    }
    
    public static void createZip(Path source, Path target) throws IOException {
        try (java.util.zip.ZipOutputStream zos = 
                new java.util.zip.ZipOutputStream(Files.newOutputStream(target))) {
            
            Files.walk(source).filter(Files::isRegularFile).forEach(path -> {
                try {
                    ZipEntry entry = new ZipEntry(source.relativize(path).toString());
                    zos.putNextEntry(entry);
                    Files.copy(path, zos);
                    zos.closeEntry();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }
    
    public static void extractZip(Path source, Path target) throws IOException {
        try (java.util.zip.ZipInputStream zis = 
                new java.util.zip.ZipInputStream(Files.newInputStream(source))) {
            
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                Path path = target.resolve(entry.getName());
                
                if (entry.isDirectory()) {
                    Files.createDirectories(path);
                } else {
                    Files.createDirectories(path.getParent());
                    Files.copy(zis, path);
                }
                zis.closeEntry();
            }
        }
    }
}
```

---

## Step 4: File Menu Interface

```java
// ui/FileMenu.java
package com.learning.project.ui;

import com.learning.project.model.*;
import com.learning.project.service.*;
import com.learning.project.util.*;
import java.util.*;
import java.nio.file.*;

public class FileMenu {
    private Scanner scanner;
    private FileService service;
    private boolean running;
    
    public FileMenu() {
        this.scanner = new Scanner(System.in);
        this.service = new FileService();
        this.running = true;
    }
    
    public void start() {
        System.out.println("\n📁 FILE MANAGEMENT UTILITY");
        System.out.println("===========================");
        
        while (running) {
            displayMenu();
            handleChoice(getChoice());
        }
    }
    
    private void displayMenu() {
        System.out.println("\n--- MAIN MENU ---");
        System.out.println("Current: " + service.getCurrentDirectory());
        System.out.println("1. List Files");
        System.out.println("2. List by Extension");
        System.out.println("3. Search Files");
        System.out.println("4. Create File");
        System.out.println("5. Create Directory");
        System.out.println("6. Read Text File");
        System.out.println("7. Write Text File");
        System.out.println("8. Append to File");
        System.out.println("9. Delete File");
        System.out.println("10. Move File");
        System.out.println("11. Copy File");
        System.out.println("12. View File Info");
        System.out.println("13. Disk Usage");
        System.out.println("14. Compress File");
        System.out.println("15. Exit");
        System.out.print("\nChoice: ");
    }
    
    private int getChoice() {
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }
    
    private void handleChoice(int choice) {
        try {
            switch (choice) {
                case 1 -> listFiles();
                case 2 -> listByExtension();
                case 3 -> searchFiles();
                case 4 -> createFile();
                case 5 -> createDirectory();
                case 6 -> readTextFile();
                case 7 -> writeTextFile();
                case 8 -> appendToFile();
                case 9 -> deleteFile();
                case 10 -> moveFile();
                case 11 -> copyFile();
                case 12 -> viewFileInfo();
                case 13 -> diskUsage();
                case 14 -> compressFile();
                case 15 -> { System.out.println("Goodbye!"); running = false; }
                default -> System.out.println("Invalid choice!");
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    
    private void listFiles() throws Exception {
        var files = service.listFiles();
        System.out.println("\n=== FILES ===");
        System.out.printf("%-30s %10s %s%n", "Name", "Size", "Last Modified");
        System.out.println("-".repeat(60));
        for (var file : files) {
            System.out.println(file);
        }
    }
    
    private void listByExtension() throws Exception {
        System.out.print("Extension: ");
        String ext = scanner.nextLine().trim();
        
        var files = service.listFilesByExtension(ext);
        System.out.println("\n=== " + ext.toUpperCase() + " FILES ===");
        for (var file : files) {
            System.out.println(file);
        }
    }
    
    private void searchFiles() throws Exception {
        System.out.print("Search term: ");
        String term = scanner.nextLine().trim();
        
        var files = service.findFiles(term);
        System.out.println("\n=== SEARCH RESULTS ===");
        for (var file : files) {
            System.out.println(file.getPath());
        }
    }
    
    private void createFile() throws Exception {
        System.out.print("Filename: ");
        String name = scanner.nextLine().trim();
        
        service.createFile(name);
        System.out.println("File created!");
    }
    
    private void createDirectory() throws Exception {
        System.out.print("Directory name: ");
        String name = scanner.nextLine().trim();
        
        service.createDirectory(name);
        System.out.println("Directory created!");
    }
    
    private void readTextFile() throws Exception {
        System.out.print("Filename: ");
        String name = scanner.nextLine().trim();
        
        String content = service.readTextFile(name);
        System.out.println("\n=== CONTENT ===");
        System.out.println(content);
    }
    
    private void writeTextFile() throws Exception {
        System.out.print("Filename: ");
        String name = scanner.nextLine().trim();
        System.out.print("Content: ");
        String content = scanner.nextLine().trim();
        
        service.writeTextFile(name, content);
        System.out.println("File written!");
    }
    
    private void appendToFile() throws Exception {
        System.out.print("Filename: ");
        String name = scanner.nextLine().trim();
        System.out.print("Content to append: ");
        String content = scanner.nextLine().trim();
        
        service.appendTextFile(name, content);
        System.out.println("Content appended!");
    }
    
    private void deleteFile() throws Exception {
        System.out.print("Filename to delete: ");
        String name = scanner.nextLine().trim();
        
        if (service.deleteFile(name)) {
            System.out.println("File deleted!");
        } else {
            System.out.println("File not found!");
        }
    }
    
    private void moveFile() throws Exception {
        System.out.print("Source file: ");
        String source = scanner.nextLine().trim();
        System.out.print("Destination: ");
        String dest = scanner.nextLine().trim();
        
        service.moveFile(source, dest);
        System.out.println("File moved!");
    }
    
    private void copyFile() throws Exception {
        System.out.print("Source file: ");
        String source = scanner.nextLine().trim();
        System.out.print("Destination: ");
        String dest = scanner.nextLine().trim();
        
        service.copyFile(source, dest);
        System.out.println("File copied!");
    }
    
    private void viewFileInfo() throws Exception {
        System.out.print("Filename: ");
        String name = scanner.nextLine().trim();
        
        var files = service.listFiles();
        var file = files.stream()
            .filter(f -> f.getName().equals(name))
            .findFirst();
        
        if (file.isPresent()) {
            var f = file.get();
            System.out.println("\n=== FILE INFO ===");
            System.out.println("Name: " + f.getName());
            System.out.println("Path: " + f.getPath());
            System.out.println("Size: " + f.getFormattedSize());
            System.out.println("Extension: " + f.getExtension());
            System.out.println("Directory: " + f.isDirectory());
            System.out.println("Modified: " + f.getLastModified());
        } else {
            System.out.println("File not found!");
        }
    }
    
    private void diskUsage() throws Exception {
        long totalSize = service.getTotalSize();
        var byExt = service.getSizeByExtension();
        
        System.out.println("\n=== DISK USAGE ===");
        System.out.printf("Total: %.2f KB%n", totalSize / 1024.0);
        
        System.out.println("\nBy Extension:");
        for (var entry : byExt.entrySet()) {
            System.out.printf("  .%s: %.2f KB%n", 
                entry.getKey(), entry.getValue() / 1024.0);
        }
    }
    
    private void compressFile() throws Exception {
        System.out.print("File to compress: ");
        String source = scanner.nextLine().trim();
        System.out.print("Output file: ");
        String target = scanner.nextLine().trim();
        
        Path srcPath = service.getCurrentDirectory().resolve(source);
        Path tgtPath = service.getCurrentDirectory().resolve(target);
        
        FileUtils.compressFile(srcPath, tgtPath);
        System.out.println("File compressed!");
    }
    
    public static void main(String[] args) {
        new FileMenu().start();
    }
}
```

---

## Running the Mini-Project

```bash
cd 07-files-io
javac -d out -sourcepath src src/com/learning/project/**/*.java src/com/learning/project/*.java
java -cp out com.learning.project.ui.FileMenu
```

---

## File I/O Concepts Demonstrated

| Concept | Implementation |
|---------|----------------|
| **Path API** | Paths.get(), Path resolution |
| **Files Class** | readString, writeString, copy, move |
| **Streams** | InputStream, OutputStream |
| **Buffered I/O** | BufferedReader, BufferedWriter |
| **Serialization** | Object serialization |
| **ZIP Operations** | Compress/extract |

---

# 🚀 Real-World Project: File Encryption/Decryption Utility

## Project Overview

**Duration**: 15-20 hours  
**Difficulty**: Advanced  
**Concepts Used**: Encryption, Decryption, Secure Random, Key Management, File Processing

This project implements a production-ready file encryption utility with secure key management and batch processing.

---

## Project Structure

```
07-files-io/src/main/java/com/learning/project/
├── Main.java
├── model/
│   ├── EncryptedFile.java
│   └── KeyInfo.java
├── crypto/
│   ├── FileEncryptor.java
│   ├── KeyManager.java
│   └── SecureRandomProvider.java
├── service/
│   ├── EncryptionService.java
│   └── BatchProcessor.java
├── util/
│   └── FileUtils.java
└── ui/
    └── CryptoMenu.java
```

---

## Step 1: Key Manager

```java
// crypto/KeyManager.java
package com.learning.project.crypto;

import javax.crypto.*;
import javax.crypto.spec.*;
import java.security.*;
import java.security.spec.*;
import java.io.*;
import java.util.*;

public class KeyManager {
    private static final int KEY_SIZE = 256;
    private static final String ALGORITHM = "AES";
    
    public static SecretKey generateKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGen = KeyGenerator.getInstance(ALGORITHM);
        keyGen.init(KEY_SIZE, new SecureRandom());
        return keyGen.generateKey();
    }
    
    public static SecretKey generateKeyFromPassword(String password, byte[] salt) 
            throws NoSuchAlgorithmException {
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, KEY_SIZE);
        SecretKey tmp = factory.generateSecret(spec);
        return new SecretKeySpec(tmp.getEncoded(), ALGORITHM);
    }
    
    public static byte[] generateSalt() {
        byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);
        return salt;
    }
    
    public static void saveKey(SecretKey key, String filename, String password) 
            throws Exception {
        // Derive key from password for encrypting the AES key
        byte[] salt = generateSalt();
        SecretKey encryptionKey = generateKeyFromPassword(password, salt);
        
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, encryptionKey);
        byte[] encryptedKey = cipher.doFinal(key.getEncoded());
        
        try (DataOutputStream dos = new DataOutputStream(
                new FileOutputStream(filename))) {
            dos.writeInt(salt.length);
            dos.write(salt);
            dos.writeInt(encryptedKey.length);
            dos.write(encryptedKey);
        }
    }
    
    public static SecretKey loadKey(String filename, String password) 
            throws Exception {
        try (DataInputStream dis = new DataInputStream(
                new FileInputStream(filename))) {
            int saltLen = dis.readInt();
            byte[] salt = new byte[saltLen];
            dis.readFully(salt);
            
            int keyLen = dis.readInt();
            byte[] encryptedKey = new byte[keyLen];
            dis.readFully(encryptedKey);
            
            SecretKey encryptionKey = generateKeyFromPassword(password, salt);
            
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, encryptionKey);
            byte[] decryptedKey = cipher.doFinal(encryptedKey);
            
            return new SecretKeySpec(decryptedKey, ALGORITHM);
        }
    }
    
    public static void exportKey(SecretKey key, String filename) throws IOException {
        byte[] encoded = key.getEncoded();
        try (FileOutputStream fos = new FileOutputStream(filename)) {
            fos.write(encoded);
        }
    }
    
    public static SecretKey importKey(String filename) throws IOException {
        byte[] encoded;
        try (FileInputStream fis = new FileInputStream(filename)) {
            encoded = fis.readAllBytes();
        }
        return new SecretKeySpec(encoded, ALGORITHM);
    }
}
```

---

## Step 2: File Encryptor

```java
// crypto/FileEncryptor.java
package com.learning.project.crypto;

import javax.crypto.*;
import javax.crypto.spec.*;
import java.io.*;
import java.security.*;
import java.util.*;

public class FileEncryptor {
    private static final String TRANSFORMATION = "AES/GCM/NoPadding";
    private static final int GCM_IV_LENGTH = 12;
    private static final int GCM_TAG_LENGTH = 128;
    
    public static class EncryptedData {
        private final byte[] iv;
        private final byte[] encryptedData;
        
        public EncryptedData(byte[] iv, byte[] encryptedData) {
            this.iv = iv;
            this.encryptedData = encryptedData;
        }
        
        public byte[] getIv() { return iv; }
        public byte[] getEncryptedData() { return encryptedData; }
    }
    
    public static EncryptedData encrypt(byte[] data, SecretKey key) 
            throws Exception {
        byte[] iv = new byte[GCM_IV_LENGTH];
        new SecureRandom().nextBytes(iv);
        
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
        cipher.init(Cipher.ENCRYPT_MODE, key, parameterSpec);
        
        byte[] encryptedData = cipher.doFinal(data);
        return new EncryptedData(iv, encryptedData);
    }
    
    public static byte[] decrypt(EncryptedData encrypted, SecretKey key) 
            throws Exception {
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH, encrypted.getIv());
        cipher.init(Cipher.DECRYPT_MODE, key, parameterSpec);
        
        return cipher.doFinal(encrypted.getEncryptedData());
    }
    
    public static void encryptFile(InputStream input, OutputStream output, 
                                  SecretKey key) throws Exception {
        byte[] iv = new byte[GCM_IV_LENGTH];
        new SecureRandom().nextBytes(iv);
        output.write(iv);
        
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
        cipher.init(Cipher.ENCRYPT_MODE, key, parameterSpec);
        
        byte[] buffer = new byte[8192];
        int bytesRead;
        while ((bytesRead = input.read(buffer)) != -1) {
            byte[] outputBuffer = cipher.update(buffer, 0, bytesRead);
            if (outputBuffer != null) {
                output.write(outputBuffer);
            }
        }
        
        byte[] finalBlock = cipher.doFinal();
        if (finalBlock != null) {
            output.write(finalBlock);
        }
    }
    
    public static void decryptFile(InputStream input, OutputStream output, 
                                  SecretKey key) throws Exception {
        byte[] iv = new byte[GCM_IV_LENGTH];
        input.read(iv);
        
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
        cipher.init(Cipher.DECRYPT_MODE, key, parameterSpec);
        
        byte[] buffer = new byte[8192];
        int bytesRead;
        while ((bytesRead = input.read(buffer)) != -1) {
            byte[] outputBuffer = cipher.update(buffer, 0, bytesRead);
            if (outputBuffer != null) {
                output.write(outputBuffer);
            }
        }
        
        byte[] finalBlock = cipher.doFinal();
        if (finalBlock != null) {
            output.write(finalBlock);
        }
    }
    
    public static String encryptString(String plaintext, SecretKey key) 
            throws Exception {
        byte[] data = plaintext.getBytes("UTF-8");
        EncryptedData encrypted = encrypt(data, key);
        
        // Combine IV and encrypted data
        byte[] combined = new byte[encrypted.getIv().length + encrypted.getEncryptedData().length];
        System.arraycopy(encrypted.getIv(), 0, combined, 0, encrypted.getIv().length);
        System.arraycopy(encrypted.getEncryptedData(), 0, combined, 
            encrypted.getIv().length, encrypted.getEncryptedData().length);
        
        return Base64.getEncoder().encodeToString(combined);
    }
    
    public static String decryptString(String ciphertext, SecretKey key) 
            throws Exception {
        byte[] combined = Base64.getDecoder().decode(ciphertext);
        
        byte[] iv = Arrays.copyOfRange(combined, 0, GCM_IV_LENGTH);
        byte[] encrypted = Arrays.copyOfRange(combined, GCM_IV_LENGTH, combined.length);
        
        EncryptedData encryptedData = new EncryptedData(iv, encrypted);
        byte[] decrypted = decrypt(encryptedData, key);
        
        return new String(decrypted, "UTF-8");
    }
}
```

---

## Step 3: Encryption Service

```java
// service/EncryptionService.java
package com.learning.project.service;

import com.learning.project.crypto.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;

public class EncryptionService {
    private SecretKey key;
    private KeyManager keyManager;
    
    public EncryptionService() {
        this.keyManager = new KeyManager();
    }
    
    public void generateNewKey() throws Exception {
        this.key = KeyManager.generateKey();
    }
    
    public void loadKey(String filename, String password) throws Exception {
        this.key = KeyManager.loadKey(filename, password);
    }
    
    public void saveKey(String filename, String password) throws Exception {
        KeyManager.saveKey(key, filename, password);
    }
    
    public void exportKey(String filename) throws Exception {
        KeyManager.exportKey(key, filename);
    }
    
    public void importKey(String filename) throws Exception {
        this.key = KeyManager.importKey(filename);
    }
    
    public void encryptFile(String inputFile, String outputFile) throws Exception {
        if (key == null) {
            throw new IllegalStateException("No key loaded");
        }
        
        try (FileInputStream fis = new FileInputStream(inputFile);
             FileOutputStream fos = new FileOutputStream(outputFile)) {
            
            FileEncryptor.encryptFile(fis, fos, key);
        }
    }
    
    public void decryptFile(String inputFile, String outputFile) throws Exception {
        if (key == null) {
            throw new IllegalStateException("No key loaded");
        }
        
        try (FileInputStream fis = new FileInputStream(inputFile);
             FileOutputStream fos = new FileOutputStream(outputFile)) {
            
            FileEncryptor.decryptFile(fis, fos, key);
        }
    }
    
    public String encryptText(String plaintext) throws Exception {
        if (key == null) {
            throw new IllegalStateException("No key loaded");
        }
        return FileEncryptor.encryptString(plaintext, key);
    }
    
    public String decryptText(String ciphertext) throws Exception {
        if (key == null) {
            throw new IllegalStateException("No key loaded");
        }
        return FileEncryptor.decryptString(ciphertext, key);
    }
    
    public boolean hasKey() {
        return key != null;
    }
    
    public void removeKey() {
        this.key = null;
    }
    
    public String encryptFileAuto(String filename) throws Exception {
        String encryptedName = filename + ".enc";
        encryptFile(filename, encryptedName);
        return encryptedName;
    }
    
    public String decryptFileAuto(String filename) throws Exception {
        if (!filename.endsWith(".enc")) {
            throw new IllegalArgumentException("File must have .enc extension");
        }
        String decryptedName = filename.substring(0, filename.length() - 4);
        decryptFile(filename, decryptedName);
        return decryptedName;
    }
}
```

---

## Step 4: Batch Processor

```java
// service/BatchProcessor.java
package com.learning.project.service;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.*;

public class BatchProcessor {
    private final EncryptionService encryptionService;
    private final ExecutorService executor;
    private final int threadCount;
    
    public class ProcessResult {
        private final String filename;
        private final boolean success;
        private final String message;
        
        public ProcessResult(String filename, boolean success, String message) {
            this.filename = filename;
            this.success = success;
            this.message = message;
        }
        
        public String getFilename() { return filename; }
        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
    }
    
    public BatchProcessor(EncryptionService service, int threadCount) {
        this.encryptionService = service;
        this.threadCount = threadCount;
        this.executor = Executors.newFixedThreadPool(threadCount);
    }
    
    public List<ProcessResult> encryptDirectory(String directory, String pattern) 
            throws Exception {
        Path dirPath = Paths.get(directory);
        
        List<Path> files = Files.walk(dirPath)
            .filter(Files::isRegularFile)
            .filter(p -> p.toString().endsWith(pattern))
            .collect(Collectors.toList());
        
        List<Future<ProcessResult>> futures = files.stream()
            .map(path -> executor.submit(() -> encryptFile(path)))
            .collect(Collectors.toList());
        
        return futures.stream()
            .map(f -> {
                try {
                    return f.get();
                } catch (Exception e) {
                    return new ProcessResult("unknown", false, e.getMessage());
                }
            })
            .collect(Collectors.toList());
    }
    
    private ProcessResult encryptFile(Path path) {
        try {
            String filename = path.toString();
            String encrypted = filename + ".enc";
            
            encryptionService.encryptFile(filename, encrypted);
            
            return new ProcessResult(filename, true, encrypted);
        } catch (Exception e) {
            return new ProcessResult(path.toString(), false, e.getMessage());
        }
    }
    
    public List<ProcessResult> decryptDirectory(String directory) throws Exception {
        Path dirPath = Paths.get(directory);
        
        List<Path> files = Files.walk(dirPath)
            .filter(Files::isRegularFile)
            .filter(p -> p.toString().endsWith(".enc"))
            .collect(Collectors.toList());
        
        List<Future<ProcessResult>> futures = files.stream()
            .map(path -> executor.submit(() -> decryptFile(path)))
            .collect(Collectors.toList());
        
        return futures.stream()
            .map(f -> {
                try {
                    return f.get();
                } catch (Exception e) {
                    return new ProcessResult("unknown", false, e.getMessage());
                }
            })
            .collect(Collectors.toList());
    }
    
    private ProcessResult decryptFile(Path path) {
        try {
            String filename = path.toString();
            String decrypted = filename.substring(0, filename.length() - 4);
            
            encryptionService.decryptFile(filename, decrypted);
            
            return new ProcessResult(filename, true, decrypted);
        } catch (Exception e) {
            return new ProcessResult(path.toString(), false, e.getMessage());
        }
    }
    
    public void shutdown() {
        executor.shutdown();
    }
}
```

---

## Step 5: Crypto Menu Interface

```java
// ui/CryptoMenu.java
package com.learning.project.ui;

import com.learning.project.service.*;
import com.learning.project.crypto.*;
import java.util.*;

public class CryptoMenu {
    private Scanner scanner;
    private EncryptionService service;
    private BatchProcessor batchProcessor;
    private boolean running;
    
    public CryptoMenu() {
        this.scanner = new Scanner(System.in);
        this.service = new EncryptionService();
        this.batchProcessor = new BatchProcessor(service, 4);
        this.running = true;
    }
    
    public void start() {
        System.out.println("\n🔐 FILE ENCRYPTION UTILITY");
        System.out.println("==========================");
        
        while (running) {
            displayMenu();
            handleChoice(getChoice());
        }
        
        batchProcessor.shutdown();
    }
    
    private void displayMenu() {
        System.out.println("\n--- MAIN MENU ---");
        System.out.println("1. Generate New Key");
        System.out.println("2. Save Key to File");
        System.out.println("3. Load Key from File");
        System.out.println("4. Export Key (Raw)");
        System.out.println("5. Import Key (Raw)");
        System.out.println("6. Encrypt File");
        System.out.println("7. Decrypt File");
        System.out.println("8. Encrypt Text");
        System.out.println("9. Decrypt Text");
        System.out.println("10. Batch Encrypt");
        System.out.println("11. Batch Decrypt");
        System.out.println("12. Remove Key");
        System.out.println("13. Exit");
        System.out.print("\nChoice: ");
    }
    
    private int getChoice() {
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }
    
    private void handleChoice(int choice) {
        try {
            switch (choice) {
                case 1 -> generateKey();
                case 2 -> saveKey();
                case 3 -> loadKey();
                case 4 -> exportKey();
                case 5 -> importKey();
                case 6 -> encryptFile();
                case 7 -> decryptFile();
                case 8 -> encryptText();
                case 9 -> decryptText();
                case 10 -> batchEncrypt();
                case 11 -> batchDecrypt();
                case 12 -> removeKey();
                case 13 -> { System.out.println("Goodbye!"); running = false; }
                default -> System.out.println("Invalid choice!");
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    
    private void generateKey() throws Exception {
        service.generateNewKey();
        System.out.println("New AES-256 key generated!");
    }
    
    private void saveKey() {
        if (!service.hasKey()) {
            System.out.println("No key loaded!");
            return;
        }
        System.out.print("Key filename: ");
        String filename = scanner.nextLine().trim();
        System.out.print("Password: ");
        String password = scanner.nextLine().trim();
        
        try {
            service.saveKey(filename, password);
            System.out.println("Key saved securely!");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    
    private void loadKey() {
        System.out.print("Key filename: ");
        String filename = scanner.nextLine().trim();
        System.out.print("Password: ");
        String password = scanner.nextLine().trim();
        
        try {
            service.loadKey(filename, password);
            System.out.println("Key loaded!");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    
    private void exportKey() {
        if (!service.hasKey()) {
            System.out.println("No key loaded!");
            return;
        }
        System.out.print("Export filename: ");
        String filename = scanner.nextLine().trim();
        
        try {
            service.exportKey(filename);
            System.out.println("Key exported!");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    
    private void importKey() {
        System.out.print("Import filename: ");
        String filename = scanner.nextLine().trim();
        
        try {
            service.importKey(filename);
            System.out.println("Key imported!");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    
    private void encryptFile() {
        if (!service.hasKey()) {
            System.out.println("No key loaded! Generate or load a key first.");
            return;
        }
        
        System.out.print("File to encrypt: ");
        String input = scanner.nextLine().trim();
        System.out.print("Output file: ");
        String output = scanner.nextLine().trim();
        
        try {
            service.encryptFile(input, output);
            System.out.println("File encrypted: " + output);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    
    private void decryptFile() {
        if (!service.hasKey()) {
            System.out.println("No key loaded!");
            return;
        }
        
        System.out.print("File to decrypt: ");
        String input = scanner.nextLine().trim();
        System.out.print("Output file: ");
        String output = scanner.nextLine().trim();
        
        try {
            service.decryptFile(input, output);
            System.out.println("File decrypted: " + output);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    
    private void encryptText() {
        if (!service.hasKey()) {
            System.out.println("No key loaded!");
            return;
        }
        
        System.out.print("Text to encrypt: ");
        String text = scanner.nextLine().trim();
        
        try {
            String encrypted = service.encryptText(text);
            System.out.println("Encrypted: " + encrypted);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    
    private void decryptText() {
        if (!service.hasKey()) {
            System.out.println("No key loaded!");
            return;
        }
        
        System.out.print("Text to decrypt: ");
        String text = scanner.nextLine().trim();
        
        try {
            String decrypted = service.decryptText(text);
            System.out.println("Decrypted: " + decrypted);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    
    private void batchEncrypt() {
        if (!service.hasKey()) {
            System.out.println("No key loaded!");
            return;
        }
        
        System.out.print("Directory: ");
        String dir = scanner.nextLine().trim();
        
        try {
            var results = batchProcessor.encryptDirectory(dir, ".txt");
            System.out.println("\n=== BATCH ENCRYPT RESULTS ===");
            for (var r : results) {
                System.out.printf("%s: %s%n", r.getFilename(), 
                    r.isSuccess() ? r.getMessage() : "FAILED: " + r.getMessage());
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    
    private void batchDecrypt() {
        if (!service.hasKey()) {
            System.out.println("No key loaded!");
            return;
        }
        
        System.out.print("Directory: ");
        String dir = scanner.nextLine().trim();
        
        try {
            var results = batchProcessor.decryptDirectory(dir);
            System.out.println("\n=== BATCH DECRYPT RESULTS ===");
            for (var r : results) {
                System.out.printf("%s: %s%n", r.getFilename(), 
                    r.isSuccess() ? r.getMessage() : "FAILED: " + r.getMessage());
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    
    private void removeKey() {
        service.removeKey();
        System.out.println("Key removed from memory!");
    }
    
    public static void main(String[] args) {
        new CryptoMenu().start();
    }
}
```

---

## Running the Real-World Project

```bash
cd 07-files-io
javac -d out -sourcepath src src/com/learning/project/**/*.java src/com/learning/project/*.java
java -cp out com.learning.project.ui.CryptoMenu
```

---

## Advanced File I/O Concepts

| Concept | Implementation |
|---------|----------------|
| **AES Encryption** | GCM mode for secure encryption |
| **Key Management** | PBKDF2 key derivation |
| **GCM Mode** | Authenticated encryption |
| **Batch Processing** | Concurrent file encryption |
| **Base64 Encoding** | Text encoding for encrypted data |
| **Secure Random** | Cryptographically secure random |

---

## Extensions

1. Add RSA encryption for key exchange
2. Implement digital signatures
3. Add secure delete (overwrite)
4. Integrate with cloud storage
5. Add key rotation support

---

## Next Steps

After completing this module:
- **Module 5**: Add concurrent file processing
- **Module 8**: Genericize the encryption framework
- **Module 6**: Add exception handling for file operations