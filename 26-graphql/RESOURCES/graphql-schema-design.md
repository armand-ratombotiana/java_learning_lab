# GraphQL Schema Design

## Basic Schema Structure

```graphql
type Query {
  users: [User!]!
  user(id: ID!): User
}

type Mutation {
  createUser(input: CreateUserInput!): User!
  updateUser(id: ID!, input: UpdateUserInput!): User
  deleteUser(id: ID!): Boolean!
}

type Subscription {
  userCreated: User!
}

type User {
  id: ID!
  name: String!
  email: String!
  posts: [Post!]!
  createdAt: DateTime!
}
```

## Type System

```
┌─────────────────────────────────────────────────────────────────┐
│  TYPE KINDS                                                      │
├─────────────────────────────────────────────────────────────────┤
│  Scalar         Primitives (String, Int, Float, Boolean, ID)   │
│  Object         Custom types with fields                       │
│  Interface      Contract for types                             │
│  Union          Union of types without shared fields           │
│  Enum           Fixed set of values                            │
│  Input          Complex input arguments                        │
│  List           [Type]                                          │
│  Non-Null       Type!                                          │
└─────────────────────────────────────────────────────────────────┘
```

## Advanced Types

```graphql
# Interface
interface Node {
  id: ID!
}

interface Person {
  name: String!
  email: String!
}

# Implement
type User implements Person {
  id: ID!
  name: String!
  email: String!
  posts: [Post!]!
}

# Union
union SearchResult = User | Post | Comment

# Enum
enum Role {
  ADMIN
  USER
  GUEST
}

enum OrderStatus {
  PENDING
  PROCESSING
  SHIPPED
  DELIVERED
}

# Input Type
input CreateUserInput {
  name: String!
  email: String!
  role: Role = USER
}

input UpdateUserInput {
  name: String
  email: String
}
```

## Schema Design Patterns

```
┌─────────────────────────────────────────────────────────────────┐
│  NESTED FETCHING                                                 │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  query {                                                         │
│    user(id: "1") {                                               │
│      name                                                        │
│      posts {          # N+1 problem - resolve in batch          │
│        title                                                      │
│        comments {                                               │
│          text                                                   │
│        }                                                        │
│      }                                                           │
│    }                                                             │
│  }                                                               │
└─────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────┐
│  PAGINATION                                                      │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  type Connection {                                              │
│    edges: [Edge!]!                                              │
│    pageInfo: PageInfo!                                         │
│    totalCount: Int!                                            │
│  }                                                              │
│                                                                  │
│  type Edge {                                                    │
│    node: User!                                                 │
│    cursor: String!                                             │
│  }                                                              │
│                                                                  │
│  type PageInfo {                                                │
│    hasNextPage: Boolean!                                       │
│    hasPreviousPage: Boolean!                                  │
│    startCursor: String                                         │
│    endCursor: String                                           │
│  }                                                              │
│                                                                  │
│  query {                                                        │
│    users(first: 10, after: "cursor") {                         │
│      edges { node { name } }                                   │
│      pageInfo { hasNextPage }                                  │
│    }                                                            │
│  }                                                              │
└─────────────────────────────────────────────────────────────────┘
```

## Resolver Patterns

```javascript
// Basic resolver
const resolvers = {
  Query: {
    user: (parent, args, context, info) => {
      return db.users.findById(args.id);
    }
  },
  User: {
    posts: (parent, args, context) => {
      // parent is the user object
      return db.posts.findByUserId(parent.id);
    }
  }
};

// DataLoader for N+1 prevention
const userLoader = new DataLoader(async (ids) => {
  const users = await db.users.findByIds(ids);
  return ids.map(id => users.find(u => u.id === id));
});

const resolvers = {
  User: {
    posts: (parent) => postLoader.loadMany(parent.postIds)
  }
};
```

## Custom Scalars

```graphql
scalar DateTime
scalar Email
scalar URL

type User {
  id: ID!
  createdAt: DateTime!
  email: Email!
  website: URL
}

// Resolver
const resolvers = {
  DateTime: new GraphQLScalarType({
    name: 'DateTime',
    serialize: (value) => value.toISOString(),
    parseValue: (value) => new Date(value),
    parseLiteral: (ast) => new Date(ast.value)
  })
};
```

## Directives

```graphql
# Built-in
query GetUsers($includePosts: Boolean!) {
  users {
    name
    posts @include(if: $includePosts) {
      title
    }
  }
}

query GetUser($withEmail: Boolean!) {
  users {
    name
    email @skip(if: $withEmail)
  }
}

# Custom directive
directive @uppercase on FIELD_DEFINITION

type Query {
  user: User @uppercase
}

# Implementation
const uppercaseDirective = {
  FieldDefinition: {
    visitFieldDefinition(field, { report }) {
      const resolve = field.resolve || defaultResolve;
      field.resolve = async (source, args, context, info) => {
        const result = await resolve(source, args, context, info);
        if (typeof result === 'string') return result.toUpperCase();
        return result;
      };
    }
  }
};
```

## Federation (Schema Stitching)

```graphql
# Service A - Users
type User @key(fields: "id") {
  id: ID!
  name: String!
}

# Service B - Posts
type Post @key(fields: "id") {
  id: ID!
  title: String!
  author: User @external
}

extend type User @key(fields: "id") {
  posts: [Post]
}
```

## Best Practices

1. **Use interfaces** for shared behavior
2. **Avoid deep nesting** - max 3-4 levels
3. **Use connections** for pagination
4. **Name input types** explicitly
5. **Version with evolution**, not versions
6. **Add descriptions** for documentation
7. **Use proper nullability** - `!` vs optional
8. **Leverage fragments** for reusable selection sets