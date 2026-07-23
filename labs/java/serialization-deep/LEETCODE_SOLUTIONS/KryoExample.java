package serialization;

/**
 * Kryo serialization demonstration.
 * 
 * Kryo is a fast and efficient binary serialization framework for Java.
 * It's faster than Java serialization and produces smaller output.
 * 
 * Maven dependency:
 *   com.esotericsoftware:kryo:5.5.0
 * 
 * This class demonstrates Kryo's API patterns.
 */
public class KryoExample {

    // Data class (no need for Serializable, no-arg constructor needed)
    static class Player {
        private String name;
        private int score;
        private double accuracy;

        public Player() { } // Kryo requires no-arg constructor

        Player(String name, int score, double accuracy) {
            this.name = name;
            this.score = score;
            this.accuracy = accuracy;
        }

        // Kryo can use fields directly (no getters/setters needed)
        public String toString() {
            return "Player{name='" + name + "', score=" + score + ", accuracy=" + accuracy + "}";
        }

        public boolean equals(Object o) {
            if (!(o instanceof Player p)) return false;
            return name.equals(p.name) && score == p.score && accuracy == p.accuracy;
        }
    }

    static class GameState {
        private String level;
        private Player[] players;
        private long timestamp;

        public GameState() { }

        GameState(String level, Player[] players) {
            this.level = level;
            this.players = players;
            this.timestamp = System.currentTimeMillis();
        }

        public String toString() {
            return "GameState{level='" + level + "', players=" + java.util.Arrays.toString(players) + "}";
        }
    }

    // Simulated Kryo serialization (real Kryo is a binary format)
    static class KryoSimulator {
        // Serialize to byte array (simplified)
        static byte[] serialize(Object obj) {
            try {
                var baos = new java.io.ByteArrayOutputStream();
                var dos = new java.io.DataOutputStream(baos);

                if (obj instanceof Player p) {
                    dos.writeUTF("Player");
                    dos.writeUTF(p.name);
                    dos.writeInt(p.score);
                    dos.writeDouble(p.accuracy);
                } else if (obj instanceof GameState gs) {
                    dos.writeUTF("GameState");
                    dos.writeUTF(gs.level);
                    dos.writeLong(gs.timestamp);
                    dos.writeInt(gs.players.length);
                    for (Player p : gs.players) {
                        dos.writeUTF(p.name);
                        dos.writeInt(p.score);
                        dos.writeDouble(p.accuracy);
                    }
                }
                return baos.toByteArray();
            } catch (java.io.IOException e) {
                throw new RuntimeException(e);
            }
        }

        static Object deserialize(byte[] data) {
            try {
                var dis = new java.io.DataInputStream(new java.io.ByteArrayInputStream(data));
                String type = dis.readUTF();
                return switch (type) {
                    case "Player" -> new Player(dis.readUTF(), dis.readInt(), dis.readDouble());
                    case "GameState" -> {
                        String level = dis.readUTF();
                        dis.readLong(); // timestamp
                        int n = dis.readInt();
                        var players = new Player[n];
                        for (int i = 0; i < n; i++)
                            players[i] = new Player(dis.readUTF(), dis.readInt(), dis.readDouble());
                        yield new GameState(level, players);
                    }
                    default -> throw new IllegalArgumentException("Unknown type: " + type);
                };
            } catch (java.io.IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void main(String[] args) {
        Player p1 = new Player("Alice", 1000, 0.95);
        Player p2 = new Player("Bob", 850, 0.88);
        GameState state = new GameState("Level 5", new Player[]{p1, p2});

        // Serialize
        byte[] data = KryoSimulator.serialize(state);
        System.out.println("Kryo serialized size: " + data.length + " bytes");

        // Deserialize
        GameState deserialized = (GameState) KryoSimulator.deserialize(data);
        System.out.println("Deserialized: " + deserialized);

        assert deserialized.level.equals(state.level);
        assert deserialized.players.length == 2;
        assert deserialized.players[0].equals(p1);
        assert deserialized.players[1].equals(p2);

        System.out.println("All KryoExample tests passed.");
        System.out.println("\nReal Kryo usage:");
        System.out.println("  Kryo kryo = new Kryo();");
        System.out.println("  kryo.register(Player.class);");
        System.out.println("  // Serialize");
        System.out.println("  var output = new Output(new FileOutputStream(\"player.bin\"));");
        System.out.println("  kryo.writeObject(output, player);");
        System.out.println("  output.close();");
        System.out.println("  // Deserialize");
        System.out.println("  var input = new Input(new FileInputStream(\"player.bin\"));");
        System.out.println("  Player p = kryo.readObject(input, Player.class);");
        System.out.println("  input.close();");
    }
}