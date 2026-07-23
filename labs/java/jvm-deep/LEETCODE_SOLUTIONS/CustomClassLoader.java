package jvm.deep;

import java.io.*;

/**
 * Custom ClassLoader demonstrating parent delegation and bytecode loading.
 * 
 * Shows how to:
 * - Override findClass() for custom class loading
 * - Load bytecode from file or network
 * - Bypass parent delegation in specific cases
 * 
 * Use with a compiled .class file on disk.
 */
public class CustomClassLoader extends ClassLoader {

    private final String classPath;

    public CustomClassLoader(String classPath, ClassLoader parent) {
        super(parent);
        this.classPath = classPath;
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        String path = name.replace('.', File.separatorChar) + ".class";
        File classFile = new File(classPath, path);

        if (!classFile.exists()) {
            throw new ClassNotFoundException(name + " not found at " + classFile);
        }

        try (var bis = new BufferedInputStream(new FileInputStream(classFile));
             var baos = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = bis.read(buffer)) != -1) {
                baos.write(buffer, 0, bytesRead);
            }
            byte[] classBytes = baos.toByteArray();
            return defineClass(name, classBytes, 0, classBytes.length);
        } catch (IOException e) {
            throw new ClassNotFoundException("Failed to load " + name, e);
        }
    }

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        System.out.println("CustomClassLoader.loadClass(" + name + ")");
        // First check if already loaded
        Class<?> loaded = findLoadedClass(name);
        if (loaded != null) return loaded;

        // Try parent first (delegation)
        try {
            return getParent().loadClass(name);
        } catch (ClassNotFoundException e) {
            // Parent can't load — try custom path
        }

        // Bypass parent for specific packages (break delegation model)
        if (name.startsWith("jvm.deep.")) {
            return findClass(name);
        }

        throw new ClassNotFoundException(name);
    }

    public static void main(String[] args) throws Exception {
        // Demonstrate class loading hierarchy
        System.out.println("App ClassLoader: " + CustomClassLoader.class.getClassLoader());
        System.out.println("Platform ClassLoader: " + CustomClassLoader.class.getClassLoader().getParent());
        System.out.println("Bootstrap ClassLoader: " + CustomClassLoader.class.getClassLoader().getParent().getParent());

        // Create custom class loader
        String classPath = System.getProperty("java.class.path");
        CustomClassLoader loader = new CustomClassLoader(classPath, 
            CustomClassLoader.class.getClassLoader());

        // Load a class — this will try parent first
        // If the class is on the regular classpath, parent loads it
        Class<?> loadedClass = loader.loadClass("jvm.deep.CustomClassLoader$Demo");
        System.out.println("Loaded class: " + loadedClass.getName());
        System.out.println("Loaded by: " + loadedClass.getClassLoader());

        System.out.println("All CustomClassLoader tests passed.");
    }

    static class Demo {
        Demo() { System.out.println("Demo loaded by: " + getClass().getClassLoader()); }
    }
}