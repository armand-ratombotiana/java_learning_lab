package com.databases.plsql;

import java.util.*;
import java.util.regex.*;

public class PlSqlParser {

    public enum TokenType { KEYWORD, IDENTIFIER, STRING, NUMBER, OPERATOR, SYMBOL, COMMENT, EOF }

    public record Token(TokenType type, String value, int line, int col) {}

    public record ParsedBlock(String type, String name, List<String> declarations, List<String> statements, List<String> exceptions) {}

    private static final Set<String> KEYWORDS = Set.of(
        "BEGIN", "END", "DECLARE", "EXCEPTION", "IF", "THEN", "ELSE", "ELSIF",
        "LOOP", "WHILE", "FOR", "IN", "EXIT", "WHEN", "CASE", "WHEN",
        "PROCEDURE", "FUNCTION", "RETURN", "IS", "AS", "PACKAGE", "BODY",
        "TRIGGER", "CURSOR", "SELECT", "INTO", "FROM", "WHERE", "SET",
        "INSERT", "UPDATE", "DELETE", "CREATE", "REPLACE");

    private final String source;
    private int pos = 0, line = 1, col = 1;

    public PlSqlParser(String source) { this.source = source; }

    public List<Token> tokenize() {
        var tokens = new ArrayList<Token>();
        while (pos < source.length()) {
            char c = source.charAt(pos);
            if (Character.isWhitespace(c)) { advance(); continue; }
            if (c == '-' && peek() == '-') { tokens.add(readComment()); continue; }
            if (c == '\'') { tokens.add(readString()); continue; }
            if (Character.isLetter(c)) { tokens.add(readIdentifierOrKeyword()); continue; }
            if (Character.isDigit(c)) { tokens.add(readNumber()); continue; }
            if ("();,.:=+-*/<><=>=".indexOf(c) >= 0) {
                tokens.add(new Token(TokenType.OPERATOR, String.valueOf(c), line, col));
                advance();
            } else {
                tokens.add(new Token(TokenType.SYMBOL, String.valueOf(c), line, col));
                advance();
            }
        }
        tokens.add(new Token(TokenType.EOF, "", line, col));
        return tokens;
    }

    private Token readIdentifierOrKeyword() {
        int start = pos;
        while (pos < source.length() && (Character.isLetterOrDigit(source.charAt(pos)) || source.charAt(pos) == '_')) pos++;
        String word = source.substring(start, pos).toUpperCase();
        if (word.length() == 0) word = "?";
        TokenType type = KEYWORDS.contains(word) ? TokenType.KEYWORD : TokenType.IDENTIFIER;
        return new Token(type, word, line, col);
    }

    private Token readString() {
        int start = pos;
        advance(); // opening '
        while (pos < source.length() && source.charAt(pos) != '\'') {
            if (source.charAt(pos) == '\n') { line++; col = 1; } else { col++; }
            pos++;
        }
        if (pos < source.length()) advance(); // closing '
        return new Token(TokenType.STRING, source.substring(start, pos), line, col);
    }

    private Token readNumber() {
        int start = pos;
        while (pos < source.length() && (Character.isDigit(source.charAt(pos)) || source.charAt(pos) == '.')) pos++;
        return new Token(TokenType.NUMBER, source.substring(start, pos), line, col);
    }

    private Token readComment() {
        int start = pos;
        while (pos < source.length() && source.charAt(pos) != '\n') pos++;
        return new Token(TokenType.COMMENT, source.substring(start, pos), line, col);
    }

    private void advance() {
        if (pos < source.length() && source.charAt(pos) == '\n') { line++; col = 1; }
        else { col++; }
        pos++;
    }

    private char peek() { return pos + 1 < source.length() ? source.charAt(pos + 1) : '\0'; }

    public ParsedBlock parseBlock() {
        // Simplified PL/SQL block parser
        var tokenize = tokenize();
        var decls = new ArrayList<String>();
        var stmts = new ArrayList<String>();
        var excps = new ArrayList<String>();
        String blockType = "ANONYMOUS";
        String blockName = "";

        int i = 0;
        while (i < tokenize.size()) {
            var tok = tokenize.get(i);
            if (tok.type() == TokenType.EOF) break;
            if (tok.value().equals("DECLARE")) { blockType = "DECLARATIVE"; i++; continue; }
            if (tok.value().equals("BEGIN")) { i++; continue; }
            if (tok.value().equals("EXCEPTION")) { blockType = "EXCEPTION"; i++; continue; }
            if (tok.value().equals("END")) { break; }
            if (blockType.equals("EXCEPTION")) {
                excps.add(tok.value());
            } else if (blockType.equals("DECLARATIVE")) {
                decls.add(tok.value());
            } else {
                stmts.add(tok.value());
            }
            i++;
        }
        return new ParsedBlock(blockType, blockName, decls, stmts, excps);
    }

    public static boolean validateSyntax(String plsql) {
        try {
            var parser = new PlSqlParser(plsql);
            var tokens = parser.tokenize();
            int beginCount = 0, endCount = 0;
            for (var t : tokens) {
                if (t.value().equals("BEGIN")) beginCount++;
                if (t.value().equals("END")) endCount++;
            }
            return beginCount == endCount && beginCount > 0;
        } catch (Exception e) {
            return false;
        }
    }

    public static List<String> extractIdentifiers(String plsql) {
        var parser = new PlSqlParser(plsql);
        return parser.tokenize().stream()
            .filter(t -> t.type() == TokenType.IDENTIFIER)
            .map(Token::value)
            .distinct()
            .sorted()
            .toList();
    }
}