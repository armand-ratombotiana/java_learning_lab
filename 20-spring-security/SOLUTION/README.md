# Spring Security Solution

Reference implementation for JWT, OAuth2, and password encoding.

## JWT
- `JwtBuilder` for token creation
- `JwtParser` for validation
- Expiration handling

## Password Encoding
- `BCryptPasswordEncoder` (default)
- `SHA256PasswordEncoder`
- `PasswordEncoder` interface

## OAuth2
- OAuth2 provider configuration
- Authorization URL generation
- Token management with `OAuth2Token`

## Security Config
- JWT/OAuth2 enabled flags
- Permitted/secured paths

## Authentication & Authorization
- `UsernamePasswordAuthentication`
- `AuthorizationManager` for permissions
- `UserDetails` implementation

## Filter Chain
- `SecurityFilterChain`
- `JwtAuthenticationFilter`
- `SecurityContext`

## Test Coverage: 30+ tests