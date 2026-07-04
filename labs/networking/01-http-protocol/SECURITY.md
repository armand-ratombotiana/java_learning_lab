# HTTP Protocol - Security

## HTTPS Enforcement
```java
@Configuration
public class HttpsRedirectConfig {
    @Bean
    public ServletWebServerFactory servletContainer() {
        TomcatServletWebServerFactory factory = new TomcatServletWebServerFactory();
        Connector connector = new Connector(TomcatServletWebServerFactory.DEFAULT_PROTOCOL);
        connector.setScheme("http");
        connector.setPort(8080);
        connector.setRedirectPort(8443);
        factory.addAdditionalTomcatConnectors(connector);
        return factory;
    }
}
```

## Security Headers
```java
@Configuration
public class SecurityHeadersConfig {
    @Bean
    public Filter securityHeadersFilter() {
        return (request, response, chain) -> {
            HttpServletResponse httpResponse = (HttpServletResponse) response;
            httpResponse.setHeader("Strict-Transport-Security", "max-age=31536000; includeSubDomains");
            httpResponse.setHeader("X-Content-Type-Options", "nosniff");
            httpResponse.setHeader("X-Frame-Options", "DENY");
            httpResponse.setHeader("X-XSS-Protection", "1; mode=block");
            chain.doFilter(request, response);
        };
    }
}
```

## TLS Configuration
```java
SSLContext sslContext = SSLContext.getInstance("TLSv1.3");
sslContext.init(null, null, new SecureRandom());
HttpClient client = HttpClient.newBuilder()
    .sslContext(sslContext)
    .sslParameters(new SSLParameters() {{
        setProtocols(new String[]{"TLSv1.3", "TLSv1.2"});
        setCipherSuites(new String[]{
            "TLS_AES_128_GCM_SHA256",
            "TLS_AES_256_GCM_SHA384"
        });
    }})
    .build();
```

## Rate Limiting
```java
public class RateLimitedClient {
    private final HttpClient client;
    private final Semaphore semaphore;

    public RateLimitedClient(int maxRequestsPerSecond) {
        this.client = HttpClient.newHttpClient();
        this.semaphore = new Semaphore(maxRequestsPerSecond);
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(() -> semaphore.release(
            maxRequestsPerSecond - semaphore.availablePermits()),
            1, 1, TimeUnit.SECONDS);
    }

    public CompletableFuture<HttpResponse<String>> send(HttpRequest request) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                semaphore.acquire();
                return client.send(request, BodyHandlers.ofString());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }
}
```
