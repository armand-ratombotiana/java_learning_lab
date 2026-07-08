# Architecture: Security

`
Request â”€â”€â–¶ [Rate Limiter] â”€â”€â–¶ [CORS Filter] â”€â”€â–¶ [CSRF Filter]
    â”‚                                                   â”‚
    â–¼                                                   â–¼
[Authentication Filter] â—€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€ [SecurityContext]
    â”‚
    â–¼
[Authorization Check] â”€â”€â–¶ [Input Validation] â”€â”€â–¶ [Business Logic]
                                                      â”‚
                                                      â–¼
                                              [Output Encoding]
                                                      â”‚
                                                      â–¼
                                              [Secure Response]
`
"@

Write-Doc (Join-Path C:\Users\jratombo-adm\Desktop\java_learning_lab\labs\backend\20-backend-security-deep "PERFORMANCE.md") @"
# Performance: Security

- Rate limiting adds ~1ms overhead per request
- CSRF token validation is negligible
- Input validation with Bean Validation adds ~2-5ms
- CORS header check adds ~0.5ms
- Consider caching validated results
- Use async validation for expensive checks
- Monitor rate limiter metrics
