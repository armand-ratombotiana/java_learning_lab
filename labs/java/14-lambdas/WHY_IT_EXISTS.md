# Why Lambdas Exist

## Before Lambdas — Anonymous Classes
```java
button.addActionListener(new ActionListener() {
    @Override
    public void actionPerformed(ActionEvent e) {
        System.out.println("Clicked");
    }
});
```

## With Lambdas
```java
button.addActionListener(e -> System.out.println("Clicked"));
```

## Why They Were Introduced
- Enable functional-style programming in Java
- Enable the Streams API (Java 8)
- Reduce boilerplate compared to anonymous classes
- Support deferred execution and higher-order functions
