package com.dsacademy.lab31.rope;

import java.util.Scanner;

public class RopeEditor {

    private Rope document;
    private int cursor;

    public RopeEditor() {
        this.document = new Rope();
        this.cursor = 0;
    }

    public RopeEditor(String initialText) {
        this.document = new Rope(initialText);
        this.cursor = 0;
    }

    public void insert(String text) {
        document.insert(cursor, text);
        cursor += text.length();
    }

    public void delete(int length) {
        if (length <= 0 || cursor - length < 0) return;
        document.delete(cursor - length, cursor);
        cursor -= length;
    }

    public void moveCursor(int position) {
        if (position < 0) {
            this.cursor = 0;
        } else if (position > document.length()) {
            this.cursor = document.length();
        } else {
            this.cursor = position;
        }
    }

    public char charAt(int index) {
        return document.charAt(index);
    }

    public String substring(int start, int end) {
        return document.substring(start, end);
    }

    public int length() {
        return document.length();
    }

    public int getCursor() {
        return cursor;
    }

    public String getText() {
        return document.toString();
    }

    public Rope getDocument() {
        return document;
    }

    public void run() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Rope Editor - Type :help for commands");
        while (true) {
            System.out.print("> ");
            if (!scanner.hasNextLine()) break;
            String line = scanner.nextLine();
            if (line.equals(":q") || line.equals(":quit")) break;
            if (line.equals(":help")) {
                System.out.println("Commands:");
                System.out.println("  :help          Show this help");
                System.out.println("  :print         Print document");
                System.out.println("  :cursor <n>    Move cursor to position");
                System.out.println("  :delete <n>    Delete n chars before cursor");
                System.out.println("  :len           Print document length");
                System.out.println("  :q             Quit");
                System.out.println("  Anything else is inserted at cursor");
                continue;
            }
            if (line.equals(":print")) {
                System.out.println(getText());
                if (cursor <= getText().length()) {
                    StringBuilder indicator = new StringBuilder();
                    for (int i = 0; i < cursor; i++) indicator.append(' ');
                    indicator.append('^');
                    System.out.println(indicator);
                }
                continue;
            }
            if (line.startsWith(":cursor ")) {
                try {
                    int pos = Integer.parseInt(line.substring(8).trim());
                    moveCursor(pos);
                    System.out.println("Cursor moved to " + cursor);
                } catch (NumberFormatException e) {
                    System.out.println("Invalid position");
                }
                continue;
            }
            if (line.startsWith(":delete ")) {
                try {
                    int n = Integer.parseInt(line.substring(8).trim());
                    delete(n);
                    System.out.println("Deleted " + n + " chars");
                } catch (NumberFormatException e) {
                    System.out.println("Invalid number");
                }
                continue;
            }
            if (line.equals(":len")) {
                System.out.println("Length: " + length());
                System.out.println("Cursor: " + cursor);
                continue;
            }
            insert(line);
        }
        scanner.close();
    }

    public static void main(String[] args) {
        new RopeEditor().run();
    }
}
