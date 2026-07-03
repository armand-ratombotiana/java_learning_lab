# Mathematical Foundation — Annotations

## Annotation as Algebraic Data Type
An annotation is a product type (AND):
```
@interface Author {
    String name();     // field 1
    int year();        // field 2
}
```
An instance of `@Author(name="Alice", year=2024)` is a tuple `(String, int)`.

## Annotations as Partial Functions
An annotation maps code elements to metadata:
```
annotations: CodeElement ⇀ Annotation
```
Where `⇀` denotes a partial function (not every element has an annotation).

## Annotation Processing as Fold
Processing all annotations in a compilation unit is a fold over the AST:
```
processor(AllElements, Acc0) = Result
```
