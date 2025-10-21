package at.fhtw.mrp.repo;

import at.fhtw.mrp.model.User;
import at.fhtw.sampleapp.repository.DatabaseManager;

import java.sql.*;
import java.util.Optional;
import java.util.UUID;

public class UserRepository {

    public Optional<User> findByUsername(String username) {
        try (Connection con = DatabaseManager.INSTANCE.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     "SELECT id, username, password_hash FROM mrp_user WHERE username = ?")) {

            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return Optional.of(new User(
                        rs.getObject("id", UUID.class),
                        rs.getString("username"),
                        rs.getString("password_hash")
                ));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("DB error: findByUsername", e);
        }
    }

    public UUID createUser(String username, String passwordHash) {
        try (Connection con = DatabaseManager.INSTANCE.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     "INSERT INTO mrp_user (username, password_hash) VALUES (?, ?) RETURNING id")) {

            ps.setString(1, username);
            ps.setString(2, passwordHash);
            ResultSet rs = ps.executeQuery();
            rs.next();
            return rs.getObject("id", UUID.class);
        } catch (SQLException e) {
            if (e.getMessage().contains("duplicate")) {
                throw new RuntimeException("Username already exists.");
            }
            throw new RuntimeException("DB error: createUser", e);
        }
    }
}
