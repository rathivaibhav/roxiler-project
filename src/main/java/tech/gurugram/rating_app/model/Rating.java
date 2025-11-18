package tech.gurugram.rating_app.model;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "rating")
public class Rating {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    @Column(length = 1000)
    private String comment;
    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;
    @Column(nullable = false)
    private Integer score;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;
    public Rating() { this.createdAt = OffsetDateTime.now(); }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
    public Integer getScore() { return score; }
    public void setScore(Integer score) { this.score = score; }
    public Store getStore() { return store; }
    public void setStore(Store store) { this.store = store; }
}