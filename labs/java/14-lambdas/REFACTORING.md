# Refactoring — Lambdas

## Anonymous Class → Lambda
```java
// Before
Runnable r = new Runnable() {
    @Override public void run() {
        System.out.println("Running");
    }
};

// After
Runnable r = () -> System.out.println("Running");
```

## Nested Anonymous Classes
```java
// Before — hard to read
panel.addMouseListener(new MouseAdapter() {
    public void mouseClicked(MouseEvent e) {
        new Timer(100, new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                System.out.println("click");
            }
        }).start();
    }
});

// After — much cleaner
panel.addMouseListener((MouseAdapter) e -> {
    new Timer(100, evt -> System.out.println("click")).start();
});
```

## Loop → forEach
```java
// Before
for (String s : list) { System.out.println(s); }

// After
list.forEach(System.out::println);
```

## Extracting Complex Lambda to Method Reference
```java
// Before
items.stream()
    .filter(item -> item.getPrice() > 100 && item.getCategory().equals("BOOK"))
    .forEach(item -> System.out.println(item.formatForDisplay()));

// After
items.stream()
    .filter(this::isExpensiveBook)
    .forEach(this::displayItem);
```
