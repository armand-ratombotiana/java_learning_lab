package com.javaacademy.lab51.advancedconcurrency;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AdvancedConcurrencyTest {

    @Test
    void testActorModel() throws Exception {
        ActorModelExample.main(new String[]{});
    }

    @Test
    void testReactiveStream() throws InterruptedException {
        ReactiveStreamExample.main(new String[]{});
    }

    @Test
    void testPhaser() {
        PhaserExample.main(new String[]{});
    }

    @Test
    void testExchanger() throws InterruptedException {
        ExchangerExample.main(new String[]{});
    }

    @Test
    void testDisruptorPattern() throws Exception {
        DisruptorPattern.main(new String[]{});
    }

    @Test
    void testParallelStreamInternals() {
        ParallelStreamInternals.main(new String[]{});
    }
}
