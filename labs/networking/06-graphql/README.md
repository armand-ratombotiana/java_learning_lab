# 06 - GraphQL

## Overview

GraphQL is a query language for APIs developed by Meta. This lab covers GraphQL schema, queries, mutations, subscriptions, resolvers, and DataLoader with Java implementations.

## Learning Objectives
- Design GraphQL schemas
- Implement queries, mutations, and subscriptions
- Use DataLoader for batching and caching
- Handle N+1 query problems

## Quick Start
```java
// Schema definition
type Query {
    bookById(id: ID): Book
    books: [Book]
}
type Book {
    id: ID
    title: String
    author: Author
}

// Java resolver (Spring for GraphQL)
@Controller
public class BookController {
    @QueryMapping
    public Book bookById(@Argument String id) {
        return bookService.findById(id);
    }
}
```
