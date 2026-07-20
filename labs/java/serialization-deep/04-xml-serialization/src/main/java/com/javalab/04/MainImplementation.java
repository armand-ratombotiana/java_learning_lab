package com.javalab.04;

import jakarta.xml.bind.annotation.*;
import jakarta.xml.bind.*;
import java.io.*;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class MainImplementation {
    
    @XmlAttribute
    private int id;
    
    @XmlElement
    private String name;
    
    @XmlElement
    private int age;
    
    @XmlTransient
    private String internalNotes;
    
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
    
    public String toXml() throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(MainImplementation.class);
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        StringWriter sw = new StringWriter();
        marshaller.marshal(this, sw);
        return sw.toString();
    }
    
    public static MainImplementation fromXml(String xml) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(MainImplementation.class);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        StringReader reader = new StringReader(xml);
        return (MainImplementation) unmarshaller.unmarshal(reader);
    }
    
    public byte[] toByteArray() throws JAXBException {
        return toXml().getBytes(java.nio.charset.StandardCharsets.UTF_8);
    }
    
    public static MainImplementation fromByteArray(byte[] data) throws JAXBException {
        return fromXml(new String(data, java.nio.charset.StandardCharsets.UTF_8));
    }
}
