package reactive;

/**
 * RSocket demonstration — reactive socket protocol.
 * 
 * RSocket is a binary protocol for reactive communication between services.
 * Supports: request-response, request-stream, fire-and-forget, channel (bidirectional).
 * 
 * RSocket vs HTTP:
 * - Multiplexed over single connection
 * - Backpressure-aware
 * - Bi-directional streaming
 * - Session resumption
 * 
 * Maven dependencies:
 *   io.rsocket:rsocket-core:1.1.4
 *   io.rsocket:rsocket-transport-netty:1.1.4
 * 
 * This class demonstrates RSocket communication patterns.
 */
public class RSocketExample {

    // Data classes
    record Request(String payload) {}
    record Response(String message) {}

    // Simulated RSocket service
    interface RSocketService {
        // Request-Response (like HTTP)
        Response requestResponse(Request req);

        // Fire-and-Forget (no response expected)
        void fireAndForget(Request req);

        // Request-Stream (multiple responses)
        java.util.List<Response> requestStream(Request req);

        // Channel (bidirectional stream, simulated as exchange)
        java.util.List<Response> channel(java.util.List<Request> requests);
    }

    static class GreeterService implements RSocketService {
        public Response requestResponse(Request req) {
            System.out.println("RR: " + req.payload());
            return new Response("Hello, " + req.payload() + "!");
        }

        public void fireAndForget(Request req) {
            System.out.println("FNF (logged): " + req.payload());
            // Fire and forget — no response
        }

        public java.util.List<Response> requestStream(Request req) {
            System.out.println("RS: streaming for " + req.payload());
            return java.util.List.of(
                new Response("Stream item 1"),
                new Response("Stream item 2"),
                new Response("Stream item 3")
            );
        }

        public java.util.List<Response> channel(java.util.List<Request> requests) {
            System.out.println("CH: bidirectional with " + requests.size() + " messages");
            return requests.stream()
                .map(req -> new Response("Echo: " + req.payload()))
                .collect(java.util.stream.Collectors.toList());
        }
    }

    static class RSocketClient {
        private final RSocketService service;

        RSocketClient(RSocketService service) { this.service = service; }

        Response call(String payload) {
            return service.requestResponse(new Request(payload));
        }

        void sendAndForget(String payload) {
            service.fireAndForget(new Request(payload));
        }

        java.util.List<Response> stream(String payload) {
            return service.requestStream(new Request(payload));
        }

        java.util.List<Response> channel(String... payloads) {
            var requests = java.util.Arrays.stream(payloads)
                .map(Request::new)
                .collect(java.util.stream.Collectors.toList());
            return service.channel(requests);
        }
    }

    public static void main(String[] args) {
        GSonService server = new GreeterService();
        RSocketClient client = new RSocketClient(server);

        // 1. Request-Response
        Response resp = client.call("World");
        assert resp.message().equals("Hello, World!");
        System.out.println("RR: " + resp.message());

        // 2. Fire-and-Forget
        client.sendAndForget("Log this");
        System.out.println("FNF sent");

        // 3. Request-Stream
        var stream = client.stream("data");
        assert stream.size() == 3;
        System.out.println("RS: received " + stream.size() + " items");

        // 4. Channel (bidirectional)
        var channel = client.channel("A", "B", "C");
        assert channel.size() == 3;
        assert channel.get(0).message().equals("Echo: A");
        System.out.println("CH: received " + channel.size() + " echoes");

        // RSocket lifecycle
        System.out.println("\nRSocket lifecycle:");
        System.out.println("  Setup: client connects, negotiates parameters");
        System.out.println("  LEASE: flow control (optional)");
        System.out.println("  KEEPALIVE: connection health");
        System.out.println("  RESUME: session resumption after disconnect");

        System.out.println("All RSocketExample tests passed.");
    }
}