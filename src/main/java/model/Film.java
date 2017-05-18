package model;

public class Film {
    public static final String FILM_ID = "filmId";
    public static final String TITLE = "title";
    public static final String LENGTH = "length";
    public static final String SPECIAL_FEATURES = "specialFeatures";
    public static final String DESCRIPTION = "description";
    
    private Short filmId;

    private String title;

    private Short length;

    private String specialFeatures;

    private String description;

    public Short getFilmId() {
        return filmId;
    }

    public void setFilmId(Short filmId) {
        this.filmId = filmId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title == null ? null : title.trim();
    }

    public Short getLength() {
        return length;
    }

    public void setLength(Short length) {
        this.length = length;
    }

    public String getSpecialFeatures() {
        return specialFeatures;
    }

    public void setSpecialFeatures(String specialFeatures) {
        this.specialFeatures = specialFeatures == null ? null : specialFeatures.trim();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description == null ? null : description.trim();
    }
}