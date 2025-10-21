package at.fhtw.mrp.http;

import at.fhtw.mrp.service.AuthService;
import com.sun.net.httpserver.Headers;
import java.util.UUID;

public class SecurityHandler {
    private final AuthService authService = new AuthService();

    public UUID requireUser(Headers headers) {
        String auth = headers.getFirst("Authorization");
        if (auth == null || !auth.startsWith("Bearer ")) {
            throw new RuntimeException("Missing or invalid Authorization header");
        }

        String token = auth.substring("Bearer ".length()).trim();
        System.out.println("Received token: " + token);

        UUID userId = authService.verify(token);  // verify aufrufen
        System.out.println("Verified userId: " + userId);

        return userId;
    }
}
