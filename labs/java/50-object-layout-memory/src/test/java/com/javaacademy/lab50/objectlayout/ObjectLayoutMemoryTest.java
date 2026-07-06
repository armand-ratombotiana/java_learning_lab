package com.javaacademy.lab50.objectlayout;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ObjectLayoutMemoryTest {

    @Test
    void testObjectLayoutInspection() throws Exception {
        ObjectLayoutExample.main(new String[]{});
    }

    @Test
    void testFalseSharing() throws Exception {
        FalseSharingDemo.main(new String[]{});
    }

    @Test
    void testEscapeAnalysis() {
        EscapeAnalysisDemo.main(new String[]{});
    }

    @Test
    void testTlabSimulation() throws Exception {
        TlabSimulationExample.main(new String[]{});
    }

    @Test
    void testCardTable() {
        CardTableExample.main(new String[]{});
    }
}
