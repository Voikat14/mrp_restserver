package at.fhtw.mrp.dto;

import java.util.List;

public class MediaCreateRequest {
    public String title;
    public String description;
    public String mediaType;     // "MOVIE" | "SERIES" | "GAME"
    public int releaseYear;
    public List<String> genres;
    public int ageRestriction;
}
