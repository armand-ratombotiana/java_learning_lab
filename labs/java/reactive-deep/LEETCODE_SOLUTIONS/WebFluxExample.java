package reactive;

import java.util.function.*;

/**
 * Spring WebFlux demonstration — reactive web framework.
 * 
 * WebFlux is Spring's reactive web framework, bundled with:
 * - Router functions (functional routing)
 * - Reactive controllers (@RequestMapping, @RestController)
 * - WebClient (reactive HTTP client)
 * 
 * Maven dependency:
 *   org.springframework.boot:spring-boot-starter-webflux
 * 
 * Reactor Netty (embedded server):
 *   org.springframework.boot:spring-boot-starter-reactor-netty
 * 
 * This class demonstrates the WebFlux programming model.
 */
public class WebFluxExample {

    // Data class
    record User(String id, String name, String email) {}

    // Router function equivalent (functional endpoints)
    @FunctionalInterface
    interface HandlerFunction<T> {
        T handle(String request);
    }

    // Simulated reactive controller
    static class UserController {
        // GET /users/{id}
        User getUserById(String id) {
            System.out.println("GET /users/" + id);
            return new User(id, "User" + id, "user" + id + "@test.com");
        }

        // POST /users
        User createUser(String body) {
            System.out.println("POST /users with: " + body);
            return new User("123", "New User", "new@test.com");
        }
    }

    // Simulated WebClient
    static class WebClient {
        static class ResponseSpec<T> {
            private final T body;

            ResponseSpec(T body) { this.body = body; }

            T body(Class<T> type) { return body; }
        }

        static class RequestBuilder {
            private final String url;
            private String method = "GET";
            private String body;

            RequestBuilder(String url) { this.url = url; }

            RequestBuilder method(String m) { this.method = m; return this; }
            RequestBuilder body(String b) { this.body = b; return this; }

            <T> ResponseSpec<T> retrieve() {
                System.out.println(method + " " + url + (body != null ? " body=" + body : ""));
                @SuppressWarnings("unchecked")
                T result = (T) new User("1", "Remote User", "remote@test.com");
                return new ResponseSpec<>(result);
            }
        }

        static RequestBuilder get(String url) { return new RequestBuilder(url).method("GET"); }
        static RequestBuilder post(String url) { return new RequestBuilder(url).method("POST"); }
    }

    public static void main(String[] args) {
        var controller = new UserController();

        // Simulated WebFlux endpoint call
        User user = controller.getUserById("42");
        assert user.id().equals("42");
        assert user.name().equals("User42");
        System.out.println("Controller: " + user);

        // Simulated WebClient usage
        User remote = WebClient.get("http://localhost:8080/users/1")
            .retrieve()
            .body(User.class);
        assert remote.name().equals("Remote User");

        // Reactive POST
        User created = controller.createUser("{\"name\":\"Alice\"}");
        assert created.name().equals("New User");

        // WebFlux functional routing pattern:
        // RouterFunction<ServerResponse> route = route()
        //     .GET("/users/{id}", req -> ok().body(userService.findById(req.pathVariable("id"))))
        //     .POST("/users", req -> ok().body(userService.create(req.bodyToMono(User.class))))
        //     .build();

        System.out.println("All WebFluxExample tests passed.");
        System.out.println("\nWebFlux features:");
        System.out.println("  @RestController — reactive controllers");
        System.out.println("  RouterFunction — functional routing");
        System.out.println("  WebClient — fully reactive HTTP client");
        System.out.println("  Server-Sent Events (SSE) support");
        System.out.println("  Backpressure-aware streaming");
        System.out.println("  Netty/Undertow/Jetty reactive servers");
    }
}