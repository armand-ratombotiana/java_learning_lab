# Mock Interview: Java Module System

**Interviewer:** "You're building a plugin system for a document editor. Design it using JPMS services."

**Candidate:** "I'd define a service interface in a base module:

```java
// module com.editor.spi
public interface DocumentExporter {
    String format();
    byte[] export(Document doc);
}
```

Each plugin is a separate module:

```java
// module com.editor.export.pdf
provides com.editor.spi.DocumentExporter
    with com.editor.export.pdf.PdfExporter;
```

The editor discovers plugins at runtime via `ServiceLoader`:

```java
ServiceLoader<DocumentExporter> exporters =
    ServiceLoader.load(DocumentExporter.class);
List<String> formats = exporters.stream()
    .map(p -> p.get().format())
    .toList();
```

**Interviewer:** "How would you prevent plugins from accessing internal APIs?"

**Candidate:** "By not exporting those packages. With qualified exports, I can expose only specific packages to specific modules:

```java
exports com.editor.internal.format to com.editor.export.pdf;
```

This gives fine-grained access control — no reflection hacks needed."
