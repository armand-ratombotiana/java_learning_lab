# Mental Models for Understanding Annotations

## Model 1: Sticky Notes on Code

Think of annotations as sticky notes you can attach to any part of your code. Just as you might attach a "Do this first" note to a task, annotations attach metadata to classes, methods, or fields. The code doesn't change—the note is just additional information for tools and developers to read.

## Model 2: Labels in a Warehouse

Annotations are like labels on warehouse boxes:
- The box (class/method) holds the actual item (code behavior)
- The label (annotation) provides metadata: "Fragile", "This side up", "Keep Cool"
- Different people read different labels: shipping staff, inventory managers, QA

## Model 3: Traffic Signs

Think of annotations as traffic signs:
- `@Override` is like a "One Way" sign—it tells the compiler this method overrides another
- `@Deprecated` is like a "Road Closed" sign—it warns that code should not be used
- `@Test` is like a "Speed Limit" sign for test runners—sets expectations for test execution

## Model 4: Recipe Ingredients

Annotations are like recipe labels:
- `@Entity` says "This class is the main ingredient for database storage"
- `@Column` specifies portion size—"Use this column as-is"
- The recipe (framework) reads the labels and knows how to process the ingredients

## Model 5: Contracts and Specifications

Annotations represent contracts:
- `@NotNull` is a contract: "This field must never be null"
- `@Size(min=5, max=50)` is a contract: "String length must be 5-50 characters"
- `@Transactional` is a contract: "This method must run within a transaction"

## Key Insight

Annotations are **metadata carriers**—they don't change code behavior directly but provide information that other code (frameworks, compilers, tools) can use to generate behavior, validate constraints, or provide IDE assistance.