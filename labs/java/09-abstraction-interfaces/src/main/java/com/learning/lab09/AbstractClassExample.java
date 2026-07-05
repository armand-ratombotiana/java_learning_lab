package com.learning.lab09;

/**
 * Demonstrates abstract classes with a template method pattern.
 */
public class AbstractClassExample {

    public static void showAbstractClass() {
        System.out.println("=== Abstract Classes (Template Method) ===");

        DataExporter csv = new CsvExporter();
        csv.export("data.csv");

        System.out.println();

        DataExporter json = new JsonExporter();
        json.export("data.json");
    }
}

abstract class DataExporter {
    public final void export(String filename) {
        openFile(filename);
        writeHeader();
        writeData();
        writeFooter();
        closeFile();
    }

    protected abstract void writeHeader();
    protected abstract void writeData();
    protected abstract void writeFooter();

    private void openFile(String filename) {
        System.out.println("Opening file: " + filename);
    }

    private void closeFile() {
        System.out.println("Closing file");
    }
}

class CsvExporter extends DataExporter {
    @Override protected void writeHeader() { System.out.println("Writing CSV header: id,name,value"); }
    @Override protected void writeData() { System.out.println("Writing CSV data rows"); }
    @Override protected void writeFooter() { System.out.println("Writing CSV footer: end of records"); }
}

class JsonExporter extends DataExporter {
    @Override protected void writeHeader() { System.out.println("Writing JSON opening bracket"); }
    @Override protected void writeData() { System.out.println("Writing JSON objects"); }
    @Override protected void writeFooter() { System.out.println("Writing JSON closing bracket"); }
}
