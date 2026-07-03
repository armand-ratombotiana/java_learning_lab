# Step-by-Step Guide to String Handling

## Step 1: String Basics
```java
String s1 = "Hello";
String s2 = "World";
String s3 = s1 + " " + s2;  // "Hello World"
int len = s3.length();       // 11
char c = s3.charAt(0);       // 'H'
```

## Step 2: StringBuilder
```java
StringBuilder sb = new StringBuilder();
sb.append("Item: ");
sb.append(42);
sb.append(", Price: $");
sb.append(19.99);
String result = sb.toString();  // "Item: 42, Price: $19.99"
```

## Step 3: Text Blocks
```java
String html = """
    <html>
        <body>
            <h1>Hello, %s</h1>
        </body>
    </html>
    """.formatted(name);
```

## Step 4: String Methods (Java 11+)
```java
"  hello  ".strip();         // "hello"
"".isBlank();                 // true
"hello\nworld".lines().toList();  // ["hello", "world"]
"hi".repeat(3);               // "hihihi"
```

## Step 5: Regex
```java
Pattern p = Pattern.compile("\\d+");
Matcher m = p.matcher("abc123def456");
while (m.find()) {
    System.out.println(m.group());  // 123, 456
}
```

## Step 6: Joining
```java
List<String> items = List.of("a", "b", "c");
String joined = String.join(", ", items);           // "a, b, c"
String joined2 = items.stream().collect(Collectors.joining(", ", "[", "]"));  // "[a, b, c]"
```
