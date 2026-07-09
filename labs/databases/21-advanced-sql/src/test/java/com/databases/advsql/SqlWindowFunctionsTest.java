package com.databases.advsql;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;
import java.util.*;

class SqlWindowFunctionsTest {
    private List<SqlWindowFunctions.Employee> data;
    @BeforeEach void setUp() { data = SqlWindowFunctions.sampleData(); }

    @Test void testRowNumberByDept() {
        var result = SqlWindowFunctions.rowNumber(data, "dept", "salary", true);
        assertEquals(5, result.size());
        var firstIT = result.stream().filter(m -> m.get("dept").equals("IT")).findFirst().orElseThrow();
        assertEquals(1, firstIT.get("rn"));
    }

    @Test void testRankAndDenseRank() {
        var result = SqlWindowFunctions.rankAndDenseRank(data, "salary", true);
        assertEquals(5, result.size());
        var alice = result.stream().filter(m -> (int) m.get("empId") == 1).findFirst().orElseThrow();
        var eve = result.stream().filter(m -> (int) m.get("empId") == 5).findFirst().orElseThrow();
        assertEquals(alice.get("rank"), eve.get("rank"));
        assertEquals(alice.get("denseRank"), eve.get("denseRank"));
    }

    @Test void testNTile() {
        var result = SqlWindowFunctions.nTile(data, 2, "salary", true);
        assertEquals(5, result.size());
        var buckets = result.stream().map(m -> (int) m.get("bucket")).distinct().sorted().toList();
        assertEquals(2, buckets.size());
    }

    @Test void testLagLead() {
        var result = SqlWindowFunctions.lagLead(data, "salary");
        assertEquals(5, result.size());
        assertNull(result.get(0).get("lag"));
        assertEquals(data.get(0).salary(), result.get(1).get("lag"));
    }
}