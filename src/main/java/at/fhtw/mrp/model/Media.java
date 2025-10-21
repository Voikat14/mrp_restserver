package at.fhtw.mrp.model;

import java.util.List;
import java.util.UUID;

public class Media {
    private UUID id;
    private String title;
    private String description;
    private String mediaType;
    private int releaseYear;
    private List<String> genres;
    private int ageRestriction;
    private UUID createdBy;

    public Media(UUID id, String title, String description, String mediaType, int releaseYear,
                 List<String> genres, int ageRestriction, UUID createdBy) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.mediaType = mediaType;
        this.releaseYear = releaseYear;
        this.genres = genres;
        this.ageRestriction = ageRestriction;
        this.createdBy = createdBy;
    }

    public UUID getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getMediaType() { return mediaType; }
    public int getReleaseYear() { return releaseYear; }
    public List<String> getGenres() { return genres; }
    public int getAgeRestriction() { return ageRestriction; }
    public UUID getCreatedBy() { return createdBy; }
}
