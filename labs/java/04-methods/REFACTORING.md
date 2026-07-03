# Refactoring Methods

## Extract Method

Before: 30-line block inside a method — does several things.
After: Extract into smaller methods with descriptive names.

## Rename Method

Before: `public void proc() { ... }`
After: `public void processOrder() { ... }` — verb phrase, camelCase, descriptive.

## Reduce Parameter Count

Before: `public void createUser(String name, int age, String email, String phone, String address)`
After: Create `UserProfile` object with those fields.

## Replace Loop with Stream

Before: `for (int x : list) if (x > 0) result.add(x);`
After: `var result = list.stream().filter(x -> x > 0).toList();`

## Replace Anonymous Inner Class with Lambda

Before: `button.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) { ... } });`
After: `button.addActionListener(e -> { ... });`

## Add @Override Annotation

Check all methods that override superclass/interface methods and add `@Override`.

## Split Method with Boolean Parameter

Before: `process(true)` — unclear what true means.
After: `processUrgently()` and `processNormally()`.
