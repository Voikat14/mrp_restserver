package at.fhtw.mrp.service;

import at.fhtw.mrp.model.User;
import at.fhtw.mrp.repo.UserRepository;
import org.mindrot.jbcrypt.BCrypt;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class AuthService {

    private final UserRepository userRepo = new UserRepository();
    private final Map<String, UUID> activeTokens = new ConcurrentHashMap<>();

    public UUID register(String username, String password) {
        String hash = BCrypt.hashpw(password, BCrypt.gensalt());
        return userRepo.createUser(username, hash);
    }

    public String login(String username, String password) {
        User user = userRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));
        if (!BCrypt.checkpw(password, user.getPasswordHash())) {
            throw new RuntimeException("Invalid credentials");
        }
        String token = username + "-mrpToken";
        activeTokens.put(token, user.getId());
        return token;
    }

    public UUID verify(String token) {
        // zuerst im Speicher prüfen
        if (activeTokens.containsKey(token)) {
            return activeTokens.get(token);
        }

        // Fallback: Token aus Username rekonstruieren
        if (token == null || !token.endsWith("-mrpToken")) {
            throw new RuntimeException("Unauthorized");
        }

        String username = token.substring(0, token.length() - "-mrpToken".length());
        var userOpt = userRepo.findByUsername(username);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("Unauthorized");
        }

        // Optional: beim ersten Mal wieder in activeTokens speichern
        UUID id = userOpt.get().getId();
        activeTokens.put(token, id);
        return id;
    }

}
