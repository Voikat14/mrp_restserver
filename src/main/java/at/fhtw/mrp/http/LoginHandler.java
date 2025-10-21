package at.fhtw.mrp.http;

import at.fhtw.mrp.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.util.Map;

public class LoginHandler implements HttpHandler {
    private final AuthService auth = new AuthService();
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void handle(HttpExchange ex) throws IOException {
        if (!"POST".equals(ex.getRequestMethod())) {
            ex.sendResponseHeaders(405, -1);
            return;
        }
        var req = mapper.readValue(ex.getRequestBody(), Map.class);
        try {
            String token = auth.login((String) req.get("username"), (String) req.get("password"));
            ex.sendResponseHeaders(200, 0);
            mapper.writeValue(ex.getResponseBody(), Map.of("token", token));
        } catch (Exception e) {
            ex.sendResponseHeaders(401, 0);
            mapper.writeValue(ex.getResponseBody(), Map.of("error", e.getMessage()));
        } finally {
            ex.close();
        }
    }
}
