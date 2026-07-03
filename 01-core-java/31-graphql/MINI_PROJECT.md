# Module 31: GraphQL in Spring Boot - Mini Project

**Project Name**: Social Media Blog GraphQL API  
**Difficulty Level**: Advanced  
**Estimated Time**: 3 hours

---

## 🎯 Objective
Build a GraphQL API using Spring for GraphQL that demonstrates Query fetching, Mutation execution, schema definition, and resolving the N+1 problem using `@BatchMapping`.

## 📝 Requirements

### Core Features

1. **GraphQL Schema Definition**:
   - Create a file `src/main/resources/graphql/schema.graphqls`.
   - Define types: `User` (id, name, email) and `Post` (id, title, content, author: User).
   - Define a `Query` type with: `getUserById(id: ID!): User`, `getAllPosts: [Post]`.
   - Define a `Mutation` type with: `createPost(title: String!, content: String!, authorId: ID!): Post`.

2. **Domain & Data Layer (Mocked)**:
   - Create `User` and `Post` records or classes.
   - Create an in-memory `PostRepository` and `UserRepository` returning dummy data. Ensure `UserRepository.getUsersByIds(List<Long> ids)` is available for batching.

3. **Query & Mutation Controllers**:
   - Create a `BlogController` annotated with `@Controller`.
   - Implement the `getUserById` and `getAllPosts` queries using `@QueryMapping`.
   - Implement the `createPost` mutation using `@MutationMapping`. Use `@Argument` to bind the input parameters.

4. **Solving N+1 with Batch Mapping**:
   - When a client queries `getAllPosts` and requests the nested `author` field for each post, GraphQL will by default make 1 query for the posts, and N queries to fetch the author for each post.
   - Implement a method annotated with `@BatchMapping` (or use `@SchemaMapping` with `DataLoader`) named `author(List<Post> posts)` inside the `BlogController`.
   - The batch mapping method should extract all author IDs from the given list of posts, query the `UserRepository` *once* for all those IDs, and return a `Map<Post, User>` associating each post with its author.

---

## 💡 Solution Blueprint

1. **Schema (`schema.graphqls`)**:
   ```graphql
   type Query {
       getAllPosts: [Post]
   }
   type Mutation {
       createPost(title: String!, content: String!, authorId: ID!): Post
   }
   type User {
       id: ID!
       name: String!
   }
   type Post {
       id: ID!
       title: String!
       content: String!
       author: User!
   }
   ```

2. **Controller Methods**:
   ```java
   @Controller
   public class BlogController {
       private final PostRepository postRepo;
       private final UserRepository userRepo;
       
       // ... constructor ...

       @QueryMapping
       public List<Post> getAllPosts() {
           return postRepo.findAll();
       }

       @MutationMapping
       public Post createPost(@Argument String title, @Argument String content, @Argument Long authorId) {
           return postRepo.save(new Post(title, content, authorId));
       }

       // Solves N+1 Problem
       @BatchMapping(typeName = "Post", field = "author")
       public Map<Post, User> author(List<Post> posts) {
           List<Long> authorIds = posts.stream().map(Post::getAuthorId).distinct().toList();
           Map<Long, User> usersById = userRepo.findAllByIds(authorIds).stream()
               .collect(Collectors.toMap(User::getId, u -> u));
               
           return posts.stream()
               .collect(Collectors.toMap(
                   post -> post, 
                   post -> usersById.get(post.getAuthorId())
               ));
       }
   }
   ```