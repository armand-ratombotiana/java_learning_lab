# GraphQL Exercises

## Exercise 1: Design a Blog Schema
Create a GraphQL schema for a blog with:
- Users (id, name, email, posts)
- Posts (id, title, content, author, comments)
- Comments (id, text, author, post)
- Queries: getUser, getPosts, getComments
- Mutations: createPost, addComment

## Exercise 2: Implement a Simple Resolver
Write a Java resolver that fetches user posts from a mock data store.

## Exercise 3: Add Pagination
Modify a query to support cursor-based pagination for posts.

## Exercise 4: Implement a Mutation
Create a mutation to update a user's profile with input validation.

## Exercise 5: Add Subscriptions
Implement a simple subscription for new comments on a post.

## Bonus: Build a Simple GraphQL Server
Using graphql-java, create a minimal server with one query and one mutation.