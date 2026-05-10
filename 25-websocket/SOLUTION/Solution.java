package solution;

import java.net.URI;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import jakarta.websocket.*;

public class WebSocketSolution {

    private final Map<String, Session> sessions = new ConcurrentHashMap<>();
    private SessionContainer sessionContainer;

    public void startServer(int port) {
        sessionContainer = new SessionContainer(port, this);
        sessionContainer.start();
    }

    public void broadcast(String message) {
        sessions.values().forEach(session -> {
            try {
                session.getBasicRemote().sendText(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void sendToUser(String userId, String message) {
        Session session = sessions.get(userId);
        if (session != null && session.isOpen()) {
            try {
                session.getBasicRemote().sendText(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void onOpen(Session session) {
        String userId = session.getQueryString();
        sessions.put(userId, session);
    }

    public void onClose(Session session) {
        sessions.entrySet().removeIf(e -> e.getValue().equals(session));
    }

    public void onMessage(String message) {
        System.out.println("Received: " + message);
    }

    public int getConnectedCount() {
        return sessions.size();
    }

    // STOMP over WebSocket
    public String parseStompFrame(String frame) {
        String[] lines = frame.split("\n");
        if (lines.length > 0 && lines[0].startsWith("CONNECT")) {
            return "CONNECTED\nversion:1.2\n\n";
        }
        if (lines.length > 0 && lines[0].startsWith("SUBSCRIBE")) {
            return "";
        }
        if (lines.length > 0 && lines[0].startsWith("SEND")) {
            return "";
        }
        return null;
    }

    public void sendStompMessage(Session session, String destination, String body) {
        String frame = "MESSAGE\ndestination:" + destination + "\n\n" + body + "\u0000";
        try {
            session.getBasicRemote().sendText(frame);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Heartbeat
    public void sendHeartbeat() {
        broadcast("\n");
    }
}

class SessionContainer {
    private final int port;
    private final WebSocketSolution solution;

    SessionContainer(int port, WebSocketSolution solution) {
        this.port = port;
        this.solution = solution;
    }

    void start() {
        // Server initialization would happen here
    }
}