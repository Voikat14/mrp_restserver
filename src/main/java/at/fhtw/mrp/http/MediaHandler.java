package at.fhtw.mrp.http;

import at.fhtw.mrp.dto.MediaCreateRequest;
import at.fhtw.mrp.dto.MediaResponse;
import at.fhtw.mrp.model.Media;
import at.fhtw.mrp.service.MediaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.UUID;

public class MediaHandler implements HttpHandler {
    private final ObjectMapper mapper = new ObjectMapper();
    private final MediaService service = new MediaService();
    private final SecurityHandler security = new SecurityHandler();

    @Override
    public void handle(HttpExchange ex) throws IOException {
        try {
            String method = ex.getRequestMethod();
            URI uri = ex.getRequestURI();               // /api/media or /api/media/{id}
            String path = uri.getPath();
            String[] parts = path.split("/");
            // parts: ["", "api", "media", "{id?}"]

            UUID userId = security.requireUser(ex.getRequestHeaders()); // throws if invalid

            if (parts.length == 3) { // "/api/media"
                switch (method) {
                    case "GET" -> list(ex);
                    case "POST" -> create(ex, userId);
                    default -> send(ex, 405, Map.of("error", "Method Not Allowed"));
                }
            } else if (parts.length == 4) { // "/api/media/{id}"
                UUID id = UUID.fromString(parts[3]);
                switch (method) {
                    case "GET" -> get(ex, id);
                    case "PUT" -> update(ex, userId, id);
                    case "DELETE" -> delete(ex, userId, id);
                    default -> send(ex, 405, Map.of("error", "Method Not Allowed"));
                }
            } else {
                send(ex, 404, Map.of("error", "Not Found"));
            }
        } catch (IllegalArgumentException e) {
            send(ex, 400, Map.of("error", e.getMessage())); // Bad Request
        } catch (RuntimeException e) {
            // Unauthorized or other runtime problems
            String msg = e.getMessage() == null ? "Unauthorized" : e.getMessage();
            if (msg.toLowerCase().contains("unauthorized") || msg.toLowerCase().contains("authorization"))
                send(ex, 401, Map.of("error", msg));
            else
                send(ex, 400, Map.of("error", msg));
        } catch (Exception e) {
            send(ex, 500, Map.of("error", "Server error: " + e.getMessage()));
        } finally {
            ex.close();
        }
    }

    private void create(HttpExchange ex, UUID userId) throws IOException, SQLException {
        MediaCreateRequest req = mapper.readValue(ex.getRequestBody(), MediaCreateRequest.class);
        Media m = service.create(userId, req);
        send(ex, 201, toDto(m));
    }

    private void list(HttpExchange ex) throws IOException, SQLException {
        List<MediaResponse> list = service.list().stream().map(this::toDto).collect(Collectors.toList());
        send(ex, 200, list);
    }

    private void get(HttpExchange ex, UUID id) throws IOException, SQLException {
        Media m = service.get(id);
        if (m == null) { send(ex, 404, Map.of("error","Not Found")); return; }
        send(ex, 200, toDto(m));
    }

    private void update(HttpExchange ex, UUID userId, UUID id) throws IOException, SQLException {
        MediaCreateRequest req = mapper.readValue(ex.getRequestBody(), MediaCreateRequest.class);
        boolean ok = service.update(userId, id, req);
        if (!ok) { send(ex, 403, Map.of("error","Forbidden (not owner or not found)")); return; }
        Media m = service.get(id);
        send(ex, 200, toDto(m));
    }

    private void delete(HttpExchange ex, UUID userId, UUID id) throws IOException, SQLException {
        boolean ok = service.delete(userId, id);
        if (!ok) { send(ex, 403, Map.of("error","Forbidden (not owner or not found)")); return; }
        sendRaw(ex, 204, new byte[0]);
    }

    private MediaResponse toDto(Media m) {
        return new MediaResponse(m.getId(), m.getTitle(), m.getDescription(), m.getMediaType(),
                m.getReleaseYear(), m.getGenres(), m.getAgeRestriction(), m.getCreatedBy());
    }

    private void send(HttpExchange ex, int status, Object body) throws IOException {
        byte[] bytes = mapper.writeValueAsBytes(body);
        ex.getResponseHeaders().set("Content-Type", "application/json");
        ex.sendResponseHeaders(status, bytes.length);
        ex.getResponseBody().write(bytes);
    }

    private void sendRaw(HttpExchange ex, int status, byte[] bytes) throws IOException {
        ex.sendResponseHeaders(status, bytes.length);
        if (bytes.length > 0) ex.getResponseBody().write(bytes);
    }
}
