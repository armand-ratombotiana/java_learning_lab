# Mental Models for REST

## Resource Model
Think of your API as a collection of resources, not functions.

`
/users          -> collection of user resources
/users/{id}     -> single user resource
/users/{id}/orders -> sub-collection
`

## Stateless Request Model
Each request is independent. The server does not store client state.

`
Client -> Request(token, data) -> Server
Server -> Response(status, data) -> Client
`

## CRUD Mapping
`
POST   /users   -> Create
GET    /users/1 -> Read
PUT    /users/1 -> Update (replace)
PATCH  /users/1 -> Partial update
DELETE /users/1 -> Delete
`

## Richardson Maturity Model
Level 0: XML/JSON over HTTP (RPC)
Level 1: Resources (individual URIs)
Level 2: HTTP verbs (GET, POST, PUT, DELETE)
Level 3: HATEOAS (hypermedia controls)
