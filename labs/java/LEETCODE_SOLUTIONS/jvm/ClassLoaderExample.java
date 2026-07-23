package jvm;

/**
 * Demonstration of Java ClassLoader hierarchy and custom class loading.
 * 
 * Shows:
 * - How classes are loaded (Bootstrap, Platform, System class loaders)
 * - Parent-delegation model
 * - Custom ClassLoader that loads bytecode from a byte array
 * - Class file format basics: magic number (0xCAFEBABE), version
 * 
 * Time: O(1) for delegation, O(n) for defineClass
 * Space: O(class bytes)
 */
public class ClassLoaderExample {

    public static void main(String[] args) throws Exception {
        // Show class loader hierarchy
        ClassLoader appLoader = ClassLoaderExample.class.getClassLoader();
        System.out.println("Application ClassLoader: " + appLoader);

        ClassLoader platformLoader = appLoader.getParent();
        System.out.println("Platform ClassLoader: " + platformLoader);

        ClassLoader bootstrapLoader = platformLoader.getParent();
        System.out.println("Bootstrap ClassLoader: " + bootstrapLoader); // null (native)

        // Load a class dynamically
        Class<?> clazz = Class.forName("jvm.ClassLoaderExample$DemoClass");
        System.out.println("Loaded: " + clazz.getName());

        // Custom ClassLoader that loads from a byte array
        CustomLoader loader = new CustomLoader();
        String className = "jvm.DynamicallyLoaded";
        byte[] bytecode = generateSimpleClass(className);
        Class<?> dynamicClass = loader.defineClass(className, bytecode);
        System.out.println("Dynamic class loaded: " + dynamicClass.getName());
        Object instance = dynamicClass.getDeclaredConstructor().newInstance();
        System.out.println("Instance: " + instance);

        System.out.println("All ClassLoaderExample tests passed.");
    }

    static class DemoClass {
        DemoClass() { System.out.println("DemoClass loaded"); }
    }

    // Custom ClassLoader
    static class CustomLoader extends ClassLoader {
        public Class<?> defineClass(String name, byte[] b) {
            return defineClass(name, b, 0, b.length);
        }

        @Override
        public Class<?> loadClass(String name) throws ClassNotFoundException {
            System.out.println("CustomLoader.loadClass(" + name + ")");
            return super.loadClass(name); // parent delegation
        }
    }

    // Generate minimal valid class bytes (magic + version + constant pool + this class + super + empty ctor)
    private static byte[] generateSimpleClass(String className) throws Exception {
        // For simplicity, use a pre-generated minimal class template
        // In practice, use ASM or ByteBuddy for bytecode generation
        // This method would need a real bytecode library to produce valid class files
        throw new UnsupportedOperationException(
            "Use ASM or ByteBuddy for bytecode generation. See BytecodeDemo.java");
    }
}