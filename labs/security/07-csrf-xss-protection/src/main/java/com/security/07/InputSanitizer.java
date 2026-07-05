package com.security07;

import java.util.regex.Pattern;

/**
 * Demonstrates input sanitization to prevent XSS (Cross-Site Scripting).
 * 
 * SECURITY CONCEPT: XSS attacks inject malicious scripts into web pages
 * viewed by other users. The injected script can:
 * - Steal session cookies (document.cookie)
 * - Redirect to phishing sites
 * - Deface the page
 * - Capture keystrokes
 * - Perform actions as the victim
 * 
 * Types of XSS:
 * 1. Stored XSS: malicious input saved to server, served to all users
 * 2. Reflected XSS: malicious input reflected in HTTP response
 * 3. DOM-based XSS: client-side script vulnerability (no server involved)
 * 
 * Defense layers:
 * - Input validation (allowlisting preferred over denylisting)
 * - Output encoding (context-aware: HTML, JavaScript, CSS, URL)
 * - Content Security Policy (CSP) headers
 * - HttpOnly cookies (mitigate cookie theft)
 */
public class InputSanitizer {

    // Patterns for dangerous content
    private static final Pattern SCRIPT_TAG = Pattern.compile(
            "<script[^>]*>.*?</script>", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
    private static final Pattern EVENT_HANDLER = Pattern.compile(
            "\\bon\\w+\\s*=", Pattern.CASE_INSENSITIVE);
    private static final Pattern JAVASCRIPT_PROTOCOL = Pattern.compile(
            "javascript\\s*:", Pattern.CASE_INSENSITIVE);
    private static final Pattern HTML_TAGS = Pattern.compile(
            "<[^>]*>");

    /**
     * Strips all HTML tags from input (basic sanitization).
     * SECURITY: This is a basic defense — not sufficient alone.
     * Context-aware output encoding is required based on where
     * the data will be rendered (HTML body, attribute, JavaScript, etc.).
     */
    public static String stripHtmlTags(String input) {
        if (input == null) return null;
        return HTML_TAGS.matcher(input).replaceAll("");
    }

    /**
     * Removes script tags entirely.
     * SECURITY: Blocklists can be bypassed. Prefer allowlisting
     * safe HTML tags (e.g., <b>, <i>, <p>) instead.
     */
    public static String removeScriptTags(String input) {
        if (input == null) return null;
        return SCRIPT_TAG.matcher(input).replaceAll("");
    }

    /**
     * Escapes HTML special characters to prevent XSS.
     * This is context-specific — for HTML body context:
     *   & → &amp;   < → &lt;   > → &gt;   " → &quot;   ' → &#x27;
     * 
     * For other contexts (JavaScript, CSS, URL), use different encoding!
     */
    public static String escapeHtml(String input) {
        if (input == null) return null;
        return input
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#x27;");
    }

    /**
     * Detects potential XSS attack patterns.
     * SECURITY: Detection is not prevention — always encode output.
     */
    public static boolean containsXssPatterns(String input) {
        if (input == null) return false;
        return SCRIPT_TAG.matcher(input).find()
                || EVENT_HANDLER.matcher(input).find()
                || JAVASCRIPT_PROTOCOL.matcher(input).find();
    }

    /**
     * Allowlist-based sanitization: only allows specified safe HTML tags.
     * SECURITY: Allowlisting is MUCH safer than denylisting.
     */
    public static String allowlistSafeHtml(String input) {
        if (input == null) return null;
        // Remove all tags except safe ones
        Pattern unsafeTags = Pattern.compile(
                "</?(?!b>|/b>|i>|/i>|p>|/p>|br\\s*/?>|strong>|/strong>|em>|/em>)[^>]*>",
                Pattern.CASE_INSENSITIVE);
        return unsafeTags.matcher(input).replaceAll("");
    }

    /**
     * Validates that input contains only alphanumeric characters and basic punctuation.
     * Use this for usernames, search terms, etc.
     */
    public static boolean isAlphanumericSafe(String input) {
        return input != null && input.matches("^[a-zA-Z0-9_\\-.@ ]+$");
    }

    public static void main(String[] args) {
        System.out.println("=== Input Sanitization & XSS Prevention ===\n");

        // Attack scenarios
        String[] testInputs = {
            "<script>alert('XSS')</script>",
            "<img src=x onerror=alert('XSS')>",
            "<a href=\"javascript:alert('XSS')\">Click me</a>",
            "<b>Safe bold text</b>",
            "Normal user input with <i>italics</i>",
            "<scr<script>ipt>alert('nested bypass')</script>"
        };

        for (String input : testInputs) {
            System.out.println("Input: " + input);
            System.out.println("  Contains XSS patterns: " + containsXssPatterns(input));
            System.out.println("  HTML escaped:          " + escapeHtml(input));
            System.out.println("  Tags stripped:         " + stripHtmlTags(input));
            System.out.println("  Allowlist safe HTML:   " + allowlistSafeHtml(input));
            System.out.println();
        }

        System.out.println("--- XSS Defense Layers ---");
        System.out.println("1. Input validation (allowlist)");
        System.out.println("2. Output encoding (context-aware)");
        System.out.println("3. Content-Security-Policy header");
        System.out.println("4. HttpOnly + Secure + SameSite cookies");
        System.out.println("5. Use modern frameworks (React, Angular) with auto-escaping");
    }
}
