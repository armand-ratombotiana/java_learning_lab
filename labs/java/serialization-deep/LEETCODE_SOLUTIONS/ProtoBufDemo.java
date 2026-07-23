package serialization;

/**
 * Protocol Buffers demonstration (conceptual — requires protobuf-java dependency).
 * 
 * Protobuf is a language-neutral, platform-neutral extensible mechanism for
 * serializing structured data. It's smaller, faster, and simpler than XML/JSON.
 * 
 * Schema definition (.proto file):
 *   message Person {
 *     string name = 1;
 *     int32 age = 2;
 *     repeated string hobbies = 3;
 *   }
 * 
 * Compile: protoc --java_out=src/main/java person.proto
 * 
 * Maven dependency:
 *   com.google.protobuf:protobuf-java:3.25.1
 * 
 * This class demonstrates the API patterns.
 */
public class ProtoBufDemo {

    // Simulated protobuf-generated class
    static class Person {
        private final String name;
        private final int age;
        private final java.util.List<String> hobbies;

        private Person(Builder b) {
            this.name = b.name;
            this.age = b.age;
            this.hobbies = java.util.List.copyOf(b.hobbies);
        }

        public String getName() { return name; }
        public int getAge() { return age; }
        public java.util.List<String> getHobbiesList() { return hobbies; }

        // Builder pattern (like protobuf-generated Builder)
        static class Builder {
            String name;
            int age;
            java.util.List<String> hobbies = new java.util.ArrayList<>();

            Builder setName(String v) { this.name = v; return this; }
            Builder setAge(int v) { this.age = v; return this; }
            Builder addHobbies(String v) { this.hobbies.add(v); return this; }
            Person build() { return new Person(this); }
        }

        // Simulated serialization (in real protobuf: toByteArray())
        byte[] toByteArray() {
            try {
                var baos = new java.io.ByteArrayOutputStream();
                var dos = new java.io.DataOutputStream(baos);
                dos.writeUTF(name);
                dos.writeInt(age);
                dos.writeInt(hobbies.size());
                for (String h : hobbies) dos.writeUTF(h);
                return baos.toByteArray();
            } catch (java.io.IOException e) {
                throw new RuntimeException(e);
            }
        }

        // Simulated deserialization (in real protobuf: parseFrom())
        static Person parseFrom(byte[] data) {
            try {
                var dis = new java.io.DataInputStream(new java.io.ByteArrayInputStream(data));
                var b = new Builder();
                b.setName(dis.readUTF());
                b.setAge(dis.readInt());
                int n = dis.readInt();
                for (int i = 0; i < n; i++) b.addHobbies(dis.readUTF());
                return b.build();
            } catch (java.io.IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void main(String[] args) {
        Person p = new Person.Builder()
            .setName("Alice")
            .setAge(30)
            .addHobbies("reading")
            .addHobbies("coding")
            .build();

        byte[] bytes = p.toByteArray();
        System.out.println("Protobuf serialized size: " + bytes.length + " bytes");

        Person parsed = Person.parseFrom(bytes);
        assert parsed.getName().equals("Alice");
        assert parsed.getAge() == 30;
        assert parsed.getHobbiesList().size() == 2;

        System.out.println("All ProtoBufDemo tests passed.");
        System.out.println("Real protobuf usage:");
        System.out.println("  PersonProtos.Person p = PersonProtos.Person.newBuilder()");
        System.out.println("      .setName(\"Alice\").setAge(30).build();");
        System.out.println("  byte[] data = p.toByteArray();");
        System.out.println("  PersonProtos.Person p2 = PersonProtos.Person.parseFrom(data);");
    }
}