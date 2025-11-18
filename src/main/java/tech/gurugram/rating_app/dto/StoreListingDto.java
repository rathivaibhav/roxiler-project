package tech.gurugram.rating_app.dto;

public class StoreListingDto {
    private Long id;
    private String name;
    private String address;
    private String email;

    private Double overallRating;
    private Integer userSubmittedRating;
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public Double getOverallRating() { return overallRating; }
    public void setOverallRating(Double overallRating) { this.overallRating = overallRating; }
    public Integer getUserSubmittedRating() { return userSubmittedRating; }
    public void setUserSubmittedRating(Integer userSubmittedRating) { this.userSubmittedRating = userSubmittedRating; }
}