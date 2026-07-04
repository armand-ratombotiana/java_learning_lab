# Mental Models for Pipeline Architecture

## The Factory Assembly Line
A product moves through stations. Each station performs one operation. The product emerges at the end fully assembled (transformed).

## The Water Pipe
Water flows through a pipe, passing through filters, heaters, and purifiers. Each component transforms the water in some way.

## The Unix Pipe
`cat file | grep pattern | sort | uniq` - each command transforms the data stream and passes it to the next.

## The Checklist
A traveler goes through check-in -> security -> boarding -> flight. Each stage validates and transforms the traveler's status.

## The Food Processor
Ingredients go in at the top, go through blades (grinding), filters (straining), and nozzles (shaping) before coming out the end.

```java
// Unix-like pipeline in Java
public class UnixStylePipeline {
    public static void main(String[] args) {
        List<String> lines = Files.readAllLines(Paths.get("file.txt")); // cat
        lines.stream()
            .filter(line -> line.contains("pattern"))    // grep
            .sorted()                                     // sort
            .distinct()                                   // uniq
            .forEach(System.out::println);                // output
    }
}

// Command pattern pipeline
Pipeline<String, List<String>> textPipeline = new Pipeline<>();
textPipeline
    .addStage(new TextSplitStage())
    .addStage(new FilterStage(pattern))
    .addStage(new SortStage())
    .addStage(new UniqueStage());
```
