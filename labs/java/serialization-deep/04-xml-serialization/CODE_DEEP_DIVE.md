# XML Serialization -- Code Deep Dive

## Main Implementation

### Class Structure
The main class demonstrates XML serialization with JAXB.

**Package**: com.javalab.04

### Core Components
1. **JAXBContext** - Entry point for JAXB operations
2. **Marshaller** - Converts Java objects to XML
3. **Unmarshaller** - Converts XML to Java objects
4. **Annotations** - @XmlRootElement, @XmlElement, @XmlAttribute

### JAXB Annotated POJO
@XmlRootElement @XmlAccessorType(XmlAccessType.FIELD)
public class Person {
    @XmlAttribute private int id;
    @XmlElement(name = "full_name") private String name;
    @XmlElement private Address address;
    @XmlElementWrapper(name = "phone_numbers") @XmlElement(name = "phone") private List<String> phones;
    @XmlTransient private String internalNotes;
}

### Marshalling (Object to XML)
JAXBContext context = JAXBContext.newInstance(Person.class);
Marshaller marshaller = context.createMarshaller();
marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
StringWriter sw = new StringWriter();
marshaller.marshal(person, sw);
String xml = sw.toString();

### Unmarshalling (XML to Object)
Unmarshaller unmarshaller = context.createUnmarshaller();
Person person = (Person) unmarshaller.unmarshal(new File("person.xml"));

### XmlJavaTypeAdapter
@XmlJavaTypeAdapter(DateAdapter.class) private LocalDate birthDate;
public class DateAdapter extends XmlAdapter<String, LocalDate> {
    public LocalDate unmarshal(String v) { return LocalDate.parse(v); }
    public String marshal(LocalDate v) { return v.toString(); }
}

### XStream Example
XStream xstream = new XStream(new StaxDriver());
xstream.alias("person", Person.class);
String xml = xstream.toXML(person);
Person p = (Person) xstream.fromXML(xml);

### Jackson XML Example
XmlMapper xmlMapper = new XmlMapper();
String xml = xmlMapper.writeValueAsString(person);

### Best Practices
1. Always provide a no-arg constructor for JAXB classes
2. Use @XmlJavaTypeAdapter for custom type conversions
3. Generate XML Schema from annotations using schemagen
4. Validate XML against schema during unmarshalling
5. Use StAX for streaming large XML documents
