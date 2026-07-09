package com.databases.plsql;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;

class PlSqlParserTest {
    @Test void shouldTokenizeSimpleBlock() {
        var parser = new PlSqlParser("BEGIN NULL; END;");
        var tokens = parser.tokenize();
        assertTrue(tokens.size() >= 4);
        assertEquals("BEGIN", tokens.get(0).value());
        assertEquals("END", tokens.get(tokens.size() - 2).value());
    }

    @Test void shouldValidateSyntax() {
        assertTrue(PlSqlParser.validateSyntax("BEGIN DBMS_OUTPUT.PUT_LINE('hi'); END;"));
        assertFalse(PlSqlParser.validateSyntax("BEGIN SELECT 1 FROM DUAL;"));
    }

    @Test void shouldExtractIdentifiers() {
        var ids = PlSqlParser.extractIdentifiers("BEGIN v_cnt := v_cnt + 1; END;");
        assertTrue(ids.contains("V_CNT"));
    }

    @Test void shouldParseBlock() {
        var parser = new PlSqlParser("""
            DECLARE v_num NUMBER;
            BEGIN v_num := 1; END;
            """);
        var block = parser.parseBlock();
        assertNotNull(block);
    }
}