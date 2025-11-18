package tech.gurugram.rating_app.dto;

import java.util.List;

public class StoreOwnerDashboardDto {
    private Long id;
    private String name;
    private String address;
    private Double averageRating;
    private Long ratingCount;
    private List<RatingUserDto> ratings;
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public Double getAverageRating() { return averageRating; }
    public void setAverageRating(Double averageRating) { this.averageRating = averageRating; }
    public Long getRatingCount() { return ratingCount; }
    public void setRatingCount(Long ratingCount) { this.ratingCount = ratingCount; }
    public List<RatingUserDto> getRatings() { return ratings; }
    public void setRatings(List<RatingUserDto> ratings) { this.ratings = ratings; }
}