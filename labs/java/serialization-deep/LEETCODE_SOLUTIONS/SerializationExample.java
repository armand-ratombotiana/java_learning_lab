package serialization;

import java.io.*;

/**
 * Java Serialization deep dive — demonstrates Serializable, Externalizable,
 * serialVersionUID, transient, custom writeObject/readObject, and pitfalls.
 * 
 * Time: O(n) for serialization
 * Space: O(n) for output
 */
public class SerializationExample {

    // Serializable class with custom logic
    static class Employee implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;

        private String name;
        private transient int age;     // transient — not serialized
        private String ssn;            // sensitive — custom encryption
        private static int count = 0;  // static fields not serialized

        Employee(String name, int age, String ssn) {
            this.name = name;
            this.age = age;
            this.ssn = ssn;
            count++;
        }

        // Custom serialization
        @Serial
        private void writeObject(ObjectOutputStream out) throws IOException {
            out.defaultWriteObject();
            out.writeInt(age); // serialize transient field manually
            // Encrypt SSN
            out.writeObject(encrypt(ssn));
        }

        @Serial
        private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
            in.defaultReadObject();
            age = in.readInt();         // restore transient
            ssn = decrypt((String) in.readObject()); // decrypt
        }

        private static String encrypt(String s) {
            return new StringBuilder(s).reverse().toString();
        }

        private static String decrypt(String s) {
            return new StringBuilder(s).reverse().toString();
        }

        public String toString() {
            return "Employee{name='" + name + "', age=" + age + ", ssn='" + ssn + "'}";
        }
    }

    // Externalizable — full control over serialization format
    static class Point implements Externalizable {
        private int x, y;

        public Point() { } // Required for Externalizable

        Point(int x, int y) { this.x = x; this.y = y; }

        public void writeExternal(ObjectOutput out) throws IOException {
            out.writeInt(x);
            out.writeInt(y);
        }

        public void readExternal(ObjectInput in) throws IOException {
            x = in.readInt();
            y = in.readInt();
        }

        public boolean equals(Object o) {
            return o instanceof Point p && p.x == x && p.y == y;
        }
    }

    public static void main(String[] args) throws Exception {
        // Serialize/deserialize Employee
        var emp = new Employee("Alice", 30, "123-45-6789");
        
        var baos = new ByteArrayOutputStream();
        try (var oos = new ObjectOutputStream(baos)) {
            oos.writeObject(emp);
        }

        byte[] serialized = baos.toByteArray();
        System.out.println("Serialized size: " + serialized.length + " bytes");

        Employee deserialized;
        try (var ois = new ObjectInputStream(new ByteArrayInputStream(serialized))) {
            deserialized = (Employee) ois.readObject();
        }

        System.out.println("Original: " + emp);
        System.out.println("Deserialized: " + deserialized);
        assert emp.name.equals(deserialized.name);
        assert emp.age == deserialized.age; // preserved via custom writeObject
        assert emp.ssn.equals(deserialized.ssn); // preserved via encryption

        // Externalizable
        var p1 = new Point(3, 4);
        baos = new ByteArrayOutputStream();
        try (var oos = new ObjectOutputStream(baos)) {
            oos.writeObject(p1);
        }
        Point p2;
        try (var ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()))) {
            p2 = (Point) ois.readObject();
        }
        assert p1.equals(p2);

        System.out.println("All SerializationExample tests passed.");
    }
}