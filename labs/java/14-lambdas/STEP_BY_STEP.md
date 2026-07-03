# Step by Step — Lambdas

## Step 1: Identify the Anonymous Class
```java
button.addActionListener(new ActionListener() {
    @Override public void actionPerformed(ActionEvent e) {
        frame.dispose();
    }
});
```

## Step 2: Remove the Interface Name
```java
button.addActionListener((ActionEvent e) -> {
    frame.dispose();
});
```

## Step 3: Remove Parameter Type (compiler infers)
```java
button.addActionListener((e) -> {
    frame.dispose();
});
```

## Step 4: Remove Parentheses (single param)
```java
button.addActionListener(e -> {
    frame.dispose();
});
```

## Step 5: Remove Braces and Semicolon (single statement)
```java
button.addActionListener(e -> frame.dispose());
```

## Step 6: Replace with Method Reference (if applicable)
```java
button.addActionListener(e -> frame.dispose());
// No direct method reference here, but for Consumer<String>:
Consumer<String> printer = s -> System.out.println(s);
Consumer<String> printer = System.out::println; // Method reference
```
