package at.fhtw.mrp.service;

import at.fhtw.mrp.dto.MediaCreateRequest;
import at.fhtw.mrp.model.Media;
import at.fhtw.mrp.repo.MediaRepository;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public class MediaService {
    private final MediaRepository repo = new MediaRepository();

    public Media create(UUID userId, MediaCreateRequest req) throws SQLException {
        validate(req);
        Media m = new Media(null, req.title, req.description, req.mediaType,
                req.releaseYear, req.genres, req.ageRestriction, userId);
        return repo.insert(m);
    }

    public Media get(UUID id) throws SQLException {
        return repo.findById(id).orElse(null);
    }

    public List<Media> list() throws SQLException {
        return repo.findAll();
    }

    public boolean update(UUID userId, UUID id, MediaCreateRequest req) throws SQLException {
        validate(req);
        Media m = new Media(id, req.title, req.description, req.mediaType,
                req.releaseYear, req.genres, req.ageRestriction, userId);
        return repo.update(m);
    }

    public boolean delete(UUID userId, UUID id) throws SQLException {
        return repo.delete(id, userId);
    }

    private void validate(MediaCreateRequest req) {
        if (req == null || req.title == null || req.title.isBlank()
                || req.description == null || req.description.isBlank()
                || req.mediaType == null || req.mediaType.isBlank()
                || req.genres == null || req.genres.isEmpty()) {
            throw new IllegalArgumentException("Missing required media fields");
        }
        if (req.releaseYear < 1888) throw new IllegalArgumentException("releaseYear invalid");
        if (req.ageRestriction < 0) throw new IllegalArgumentException("ageRestriction invalid");
    }
}
