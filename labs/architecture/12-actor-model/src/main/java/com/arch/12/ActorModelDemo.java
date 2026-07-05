package com.arch.actor;

public class ActorModelDemo {
    public static void main(String[] args) throws InterruptedException {
        ActorSystem system = new ActorSystem();

        system.register("printer", new Actor() {
            public void onMessage(Object message) {
                System.out.println("Actor received: " + message);
            }
        });

        system.register("counter", new Actor() {
            private int count = 0;
            public void onMessage(Object message) {
                count++;
                System.out.println("Counter actor: " + count + " messages processed");
            }
        });

        system.send("printer", "Hello Actor Model!");
        system.send("counter", "increment");
        system.send("counter", "increment");
        system.send("counter", "increment");

        Thread.sleep(500);
        system.shutdown();
    }
}
