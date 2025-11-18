package tech.gurugram.rating_app.dto;

public class RatingDto {
    private Long id;
    private Integer score;
    private String comment;
    private Long storeId;

    public RatingDto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Integer getScore() { return score; }
    public void setScore(Integer score) { this.score = score; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public Long getStoreId() { return storeId; }
    public void setStoreId(Long storeId) { this.storeId = storeId; }
}
