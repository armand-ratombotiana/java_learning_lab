package com.javaacademy.lab43.classloading;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Custom ClassLoader that loads bytecode from a custom directory.
 * Overrides findClass to demonstrate the delegation model.
 */
public class CustomClassLoader extends ClassLoader {

    private final Path classDir;

    public CustomClassLoader(Path classDir, ClassLoader parent) {
        super(parent);
        this.classDir = classDir;
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        String path = name.replace('.', '/') + ".class";
        Path classFile = classDir.resolve(path);
        if (Files.exists(classFile)) {
            try {
                byte[] bytes = Files.readAllBytes(classFile);
                return defineClass(name, bytes, 0, bytes.length);
            } catch (IOException e) {
                throw new ClassNotFoundException(name, e);
            }
        }
        throw new ClassNotFoundException(name);
    }

    public static void main(String[] args) throws Exception {
        System.out.println("=== Custom ClassLoader Demo ===\n");

        Path tmpDir = Files.createTempDirectory("custom-classes");
        System.out.println("Class directory: " + tmpDir);

        CustomClassLoader loader = new CustomClassLoader(tmpDir, ClassLoader.getPlatformClassLoader());
        System.out.println("Parent: " + loader.getParent().getName());

        try {
            loader.loadClass("com.javaacademy.lab43.classloading.CustomClassLoader");
            System.out.println("Loaded class via custom loader (delegated to parent)");
        } catch (ClassNotFoundException e) {
            System.out.println("Class not found (expected): " + e.getMessage());
        }
    }
}
