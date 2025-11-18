package tech.gurugram.rating_app.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "rating")
public class Rating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer score;

    @Column(length = 1000)
    private String comment;

    // store relationship — repository uses findByStoreId(...)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    // user relationship — original core: required
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private Instant createdAt = Instant.now();

    public Rating() {}

    public Rating(Integer score, String comment, Store store, User user) {
        this.score = score;
        this.comment = comment;
        this.store = store;
        this.user = user;
        this.createdAt = Instant.now();
    }

    // getters / setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Integer getScore() { return score; }
    public void setScore(Integer score) { this.score = score; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public Store getStore() { return store; }
    public void setStore(Store store) { this.store = store; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
