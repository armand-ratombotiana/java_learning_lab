package jvm.deep;

/**
 * Class File Analyzer — parses and displays class file structure.
 * 
 * Class file format (JVM Spec):
 *   magic (4 bytes: 0xCAFEBABE)
 *   minor_version (2 bytes)
 *   major_version (2 bytes)
 *   constant_pool_count (2 bytes)
 *   constant_pool[]
 *   access_flags (2 bytes)
 *   this_class (2 bytes)
 *   super_class (2 bytes)
 *   interfaces_count (2 bytes)
 *   interfaces[]
 *   fields_count (2 bytes)
 *   fields[]
 *   methods_count (2 bytes)
 *   methods[]
 *   attributes_count (2 bytes)
 *   attributes[]
 * 
 * Run with: java ClassFileAnalyzer <path-to-.class>
 */
public class ClassFileAnalyzer {

    public static void main(String[] args) throws Exception {
        String file = args.length > 0 ? args[0] : 
            "C:\\Users\\jratombo-adm\\Desktop\\java_learning_lab\\labs\\java\\LEETCODE_SOLUTIONS\\jvm\\ClassLoaderExample.class";
        
        var data = java.nio.file.Files.readAllBytes(java.nio.file.Paths.get(file));
        java.nio.ByteBuffer buf = java.nio.ByteBuffer.wrap(data);

        int magic = buf.getInt();
        assert magic == 0xCAFEBABE : "Not a valid class file";
        System.out.println("Magic: 0x" + Integer.toHexString(magic));

        int minor = buf.getShort() & 0xFFFF;
        int major = buf.getShort() & 0xFFFF;
        System.out.println("Version: " + major + "." + minor);
        System.out.println("  (Java " + (major - 44) + ")"); // Java 8 = 52, 11 = 55, 17 = 61, 21 = 65

        int cpCount = buf.getShort() & 0xFFFF;
        System.out.println("Constant pool count: " + cpCount);

        // Skip constant pool (simplified)
        for (int i = 1; i < cpCount; i++) {
            byte tag = buf.get();
            switch (tag) {
                case 1 -> { // CONSTANT_Utf8
                    int len = buf.getShort() & 0xFFFF;
                    buf.position(buf.position() + len);
                }
                case 3, 4 -> buf.getInt(); // Integer/Float
                case 5, 6 -> { buf.getLong(); i++; } // Long/Double (2 slots)
                case 7, 8, 16, 19, 20 -> buf.getShort(); // Class/String/MethodType/Module/Package
                case 9, 10, 11 -> { buf.getShort(); buf.getShort(); } // Field/Method/InterfaceMethod ref
                case 12 -> { buf.getShort(); buf.getShort(); } // NameAndType
                case 15 -> buf.get(); // MethodHandle
                case 17, 18 -> { buf.getShort(); buf.getShort(); } // Dynamic/InvokeDynamic
                default -> throw new IllegalArgumentException("Unknown tag: " + tag);
            }
        }

        int accessFlags = buf.getShort() & 0xFFFF;
        System.out.println("Access flags: 0x" + Integer.toHexString(accessFlags));
        System.out.println("  ACC_PUBLIC: " + ((accessFlags & 0x0001) != 0));
        System.out.println("  ACC_SUPER: " + ((accessFlags & 0x0020) != 0));

        int thisClass = buf.getShort() & 0xFFFF;
        int superClass = buf.getShort() & 0xFFFF;
        System.out.println("This class index: " + thisClass);
        System.out.println("Super class index: " + superClass);

        int ifaceCount = buf.getShort() & 0xFFFF;
        System.out.println("Interfaces: " + ifaceCount);
        for (int i = 0; i < ifaceCount; i++) buf.getShort();

        int fieldCount = buf.getShort() & 0xFFFF;
        System.out.println("Fields: " + fieldCount);
        for (int i = 0; i < fieldCount; i++) {
            buf.getShort(); // access_flags
            buf.getShort(); // name_index
            buf.getShort(); // descriptor_index
            int attrCount = buf.getShort() & 0xFFFF;
            for (int j = 0; j < attrCount; j++) {
                buf.getShort(); buf.skipBytes(buf.getInt()); // attribute_name_index + info
            }
        }

        int methodCount = buf.getShort() & 0xFFFF;
        System.out.println("Methods: " + methodCount);
        for (int i = 0; i < methodCount; i++) {
            buf.getShort(); // access_flags
            buf.getShort(); // name_index
            buf.getShort(); // descriptor_index
            int attrCount = buf.getShort() & 0xFFFF;
            for (int j = 0; j < attrCount; j++) {
                buf.getShort(); // attribute_name_index
                int len = buf.getInt();
                buf.position(buf.position() + len);
            }
        }

        System.out.println("Class file analysis complete.");
    }
}