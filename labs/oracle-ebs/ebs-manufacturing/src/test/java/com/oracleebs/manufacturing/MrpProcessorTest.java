package com.oracleebs.manufacturing;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDate;

class MrpProcessorTest {
    private MrpProcessor mrp;

    @BeforeEach
    void setUp() {
        mrp = MrpProcessor.createDefault();
    }

    @Test
    void testTotalDemand() {
        assertEquals(100, mrp.getTotalDemand("FIN001"));
        assertEquals(200, mrp.getTotalDemand("RAW001"));
    }

    @Test
    void testTotalSupply() {
        assertEquals(50, mrp.getTotalSupply("RAW001"));
        assertEquals(30, mrp.getTotalSupply("FIN001"));
    }

    @Test
    void testMrpGeneratesActionsForDeficit() {
        var actions = mrp.runMrp();
        assertFalse(actions.isEmpty());
        assertTrue(actions.stream().anyMatch(a -> a.getItemCode().equals("RAW001")));
    }

    @Test
    void testMrpActionType() {
        var actions = mrp.runMrp();
        var rawAction = actions.stream().filter(a -> a.getItemCode().equals("RAW001")).findFirst();
        assertTrue(rawAction.isPresent());
        assertEquals(MrpProcessor.MrpAction.ActionType.RELEASE_ORDER, rawAction.get().getAction());
    }

    @Test
    void testMrpActionQuantity() {
        var actions = mrp.runMrp();
        var rawAction = actions.stream().filter(a -> a.getItemCode().equals("RAW001")).findFirst();
        assertTrue(rawAction.isPresent());
        assertEquals(150, rawAction.get().getRecommendedQty());
    }

    @Test
    void testLeadTimeApplied() {
        var actions = mrp.runMrp();
        var rawAction = actions.stream().filter(a -> a.getItemCode().equals("RAW001")).findFirst();
        assertTrue(rawAction.isPresent());
        assertEquals(LocalDate.now().plusDays(3), rawAction.get().getActionDate());
    }
}
