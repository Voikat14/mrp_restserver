package at.fhtw.mrp.dto;

import java.util.List;
import java.util.UUID;

public class MediaResponse {
    public UUID id;
    public String title;
    public String description;
    public String mediaType;
    public int releaseYear;
    public List<String> genres;
    public int ageRestriction;
    public UUID createdBy;

    public MediaResponse(UUID id, String title, String description, String mediaType, int releaseYear,
                         List<String> genres, int ageRestriction, UUID createdBy) {
        this.id = id; this.title = title; this.description = description; this.mediaType = mediaType;
        this.releaseYear = releaseYear; this.genres = genres; this.ageRestriction = ageRestriction;
        this.createdBy = createdBy;
    }
}
