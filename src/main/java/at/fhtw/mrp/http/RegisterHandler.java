package at.fhtw.mrp.http;

import at.fhtw.mrp.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.util.Map;

public class RegisterHandler implements HttpHandler {
    private final AuthService auth = new AuthService();
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void handle(HttpExchange ex) throws IOException {
        // Nur POST zulassen
        if (!"POST".equalsIgnoreCase(ex.getRequestMethod())) {
            ex.sendResponseHeaders(405, -1); // Method Not Allowed
            ex.close();
            return;
        }

        try {
            // Request-Body lesen
            var req = mapper.readValue(ex.getRequestBody(), Map.class);
            String username = (String) req.get("username");
            String password = (String) req.get("password");

            // User registrieren
            auth.register(username, password);

            // Erfolgsnachricht zurückgeben
            var response = Map.of("message", "User created successfully");
            byte[] jsonBytes = mapper.writeValueAsBytes(response);

            ex.getResponseHeaders().add("Content-Type", "application/json");
            ex.sendResponseHeaders(201, jsonBytes.length);
            ex.getResponseBody().write(jsonBytes);
            ex.getResponseBody().close();

        } catch (Exception e) {
            // Fehlerantwort senden
            var errorResponse = Map.of("error", e.getMessage());
            byte[] jsonBytes = mapper.writeValueAsBytes(errorResponse);

            ex.getResponseHeaders().add("Content-Type", "application/json");
            ex.sendResponseHeaders(400, jsonBytes.length);
            ex.getResponseBody().write(jsonBytes);
            ex.getResponseBody().close();
        }
    }

}
