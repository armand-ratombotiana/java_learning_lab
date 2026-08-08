package com.javalab.lab04;

import java.nio.charset.StandardCharsets;

public class MainImplementation {
    
    private int id;
    private String name;
    private int age;
    
    public MainImplementation() {}
    
    public MainImplementation(int id, String name, int age) {
        this.id = id;
        this.name = name;
        this.age = age;
    }
    
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }
    
    public String toXml() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                + "<main id=\"" + id + "\">\n"
                + "  <name>" + escape(name) + "</name>\n"
                + "  <age>" + age + "</age>\n"
                + "</main>";
    }
    
    public static MainImplementation fromXml(String xml) {
        MainImplementation obj = new MainImplementation();
        obj.id = parseAttributeInt(xml, "id");
        obj.name = parseElement(xml, "name");
        obj.age = parseElementInt(xml, "age");
        return obj;
    }
    
    public byte[] toByteArray() {
        return toXml().getBytes(StandardCharsets.UTF_8);
    }
    
    public static MainImplementation fromByteArray(byte[] data) {
        return fromXml(new String(data, StandardCharsets.UTF_8));
    }
    
    private static String parseAttribute(String xml, String attr) {
        String marker = attr + "=\"";
        int start = xml.indexOf(marker);
        if (start < 0) return "";
        start += marker.length();
        int end = xml.indexOf('"', start);
        return end < 0 ? "" : unescape(xml.substring(start, end));
    }
    
    private static int parseAttributeInt(String xml, String attr) {
        return Integer.parseInt(parseAttribute(xml, attr).trim());
    }
    
    private static String parseElement(String xml, String tag) {
        String open = "<" + tag + ">";
        String close = "</" + tag + ">";
        int start = xml.indexOf(open);
        if (start < 0) return "";
        start += open.length();
        int end = xml.indexOf(close, start);
        return end < 0 ? "" : unescape(xml.substring(start, end));
    }
    
    private static int parseElementInt(String xml, String tag) {
        return Integer.parseInt(parseElement(xml, tag).trim());
    }
    
    private static String escape(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;");
    }
    
    private static String unescape(String s) {
        return s.replace("&quot;", "\"").replace("&lt;", "<").replace("&gt;", ">").replace("&amp;", "&");
    }
}