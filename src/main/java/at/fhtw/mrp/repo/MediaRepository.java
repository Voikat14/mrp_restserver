package at.fhtw.mrp.repo;

import at.fhtw.mrp.model.Media;
import at.fhtw.sampleapp.repository.DatabaseManager;

import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.UUID;

public class MediaRepository {

    public Media insert(Media m) throws SQLException {
        String sql = """
            INSERT INTO media (title, description, media_type, release_year, genres, age_restriction, created_by)
            VALUES (?, ?, ?::media_type, ?, ?, ?, ?)
            RETURNING id, created_by
        """;
        try (Connection con = DatabaseManager.INSTANCE.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, m.getTitle());
            ps.setString(2, m.getDescription());
            ps.setString(3, m.getMediaType());
            ps.setInt(4, m.getReleaseYear());
            ps.setArray(5, con.createArrayOf("text", m.getGenres().toArray()));
            ps.setInt(6, m.getAgeRestriction());
            ps.setObject(7, m.getCreatedBy());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Media(
                            rs.getObject("id", UUID.class),
                            m.getTitle(), m.getDescription(), m.getMediaType(),
                            m.getReleaseYear(), m.getGenres(), m.getAgeRestriction(),
                            rs.getObject("created_by", UUID.class)
                    );
                }
            }
        }
        throw new SQLException("Insert failed");
    }

    public Optional<Media> findById(UUID id) throws SQLException {
        String sql = """
            SELECT id, title, description, media_type, release_year, genres, age_restriction, created_by
            FROM media WHERE id = ?
        """;
        try (Connection con = DatabaseManager.INSTANCE.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setObject(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        }
        return Optional.empty();
    }

    public List<Media> findAll() throws SQLException {
        String sql = """
            SELECT id, title, description, media_type, release_year, genres, age_restriction, created_by
            FROM media ORDER BY created_at DESC
        """;
        try (Connection con = DatabaseManager.INSTANCE.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            List<Media> out = new ArrayList<>();
            while (rs.next()) out.add(mapRow(rs));
            return out;
        }
    }

    public boolean update(Media m) throws SQLException {
        String sql = """
            UPDATE media
            SET title=?, description=?, media_type=?::media_type, release_year=?, genres=?, age_restriction=?, updated_at=NOW()
            WHERE id=? AND created_by=?
        """;
        try (Connection con = DatabaseManager.INSTANCE.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, m.getTitle());
            ps.setString(2, m.getDescription());
            ps.setString(3, m.getMediaType());
            ps.setInt(4, m.getReleaseYear());
            ps.setArray(5, con.createArrayOf("text", m.getGenres().toArray()));
            ps.setInt(6, m.getAgeRestriction());
            ps.setObject(7, m.getId());
            ps.setObject(8, m.getCreatedBy());
            return ps.executeUpdate() == 1;
        }
    }

    public boolean delete(UUID id, UUID userId) throws SQLException {
        String sql = "DELETE FROM media WHERE id = ? AND created_by = ?";
        try (Connection con = DatabaseManager.INSTANCE.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setObject(1, id);
            ps.setObject(2, userId);
            return ps.executeUpdate() == 1;
        }
    }

    private Media mapRow(ResultSet rs) throws SQLException {
        UUID id = rs.getObject("id", UUID.class);
        String title = rs.getString("title");
        String description = rs.getString("description");
        String mediaType = rs.getString("media_type");
        int releaseYear = rs.getInt("release_year");
        List<String> genres = Arrays.stream((String[]) rs.getArray("genres").getArray())
                .collect(Collectors.toList());
        int ageRestriction = rs.getInt("age_restriction");
        UUID createdBy = rs.getObject("created_by", UUID.class);
        return new Media(id, title, description, mediaType, releaseYear, genres, ageRestriction, createdBy);
    }
}
