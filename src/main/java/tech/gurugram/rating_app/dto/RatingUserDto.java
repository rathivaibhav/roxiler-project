package tech.gurugram.rating_app.dto;

import java.time.OffsetDateTime;

public class RatingUserDto {
    private Long ratingId;
    private Integer score;
    private String comment;
    private OffsetDateTime createdAt;
    private String userName;
    private String userEmail;

    public RatingUserDto(Long ratingId, Integer score, String comment, OffsetDateTime createdAt, String userName, String userEmail) {
        this.ratingId = ratingId;
        this.score = score;
        this.comment = comment;
        this.createdAt = createdAt;
        this.userName = userName;
        this.userEmail = userEmail;
    }
    public Long getRatingId() { return ratingId; }
    public Integer getScore() { return score; }
    public String getComment() { return comment; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public String getUserName() { return userName; }
    public String getUserEmail() { return userEmail; }
}