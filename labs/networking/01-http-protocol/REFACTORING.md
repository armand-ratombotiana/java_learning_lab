# HTTP Protocol - Refactoring

## Refactoring HTTP Client Code

### Before: Tightly Coupled
```java
public class UserService {
    public User getUser(Long id) throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("https://api.example.com/users/" + id))
            .GET().build();
        HttpResponse<String> response = client.send(request,
            HttpResponse.BodyHandlers.ofString());
        return new ObjectMapper().readValue(response.body(), User.class);
    }
}
```

### After: Decoupled with Interface
```java
public interface HttpClientWrapper {
    <T> T get(String path, Class<T> responseType);
    <T> T post(String path, Object body, Class<T> responseType);
}

public class HttpClientWrapperImpl implements HttpClientWrapper {
    private final HttpClient client;
    private final String baseUrl;
    private final ObjectMapper mapper;

    public HttpClientWrapperImpl(String baseUrl) {
        this.client = HttpClient.newBuilder()
            .version(Version.HTTP_2).build();
        this.baseUrl = baseUrl;
        this.mapper = new ObjectMapper();
    }

    @Override
    public <T> T get(String path, Class<T> responseType) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + path)).GET().build();
            HttpResponse<String> response = client.send(request,
                HttpResponse.BodyHandlers.ofString());
            return mapper.readValue(response.body(), responseType);
        } catch (Exception e) {
            throw new HttpException("GET failed: " + path, e);
        }
    }

    @Override
    public <T> T post(String path, Object body, Class<T> responseType) {
        try {
            String json = mapper.writeValueAsString(body);
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + path))
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .header("Content-Type", "application/json").build();
            HttpResponse<String> response = client.send(request,
                HttpResponse.BodyHandlers.ofString());
            return mapper.readValue(response.body(), responseType);
        } catch (Exception e) {
            throw new HttpException("POST failed: " + path, e);
        }
    }
}
```
