package tech.gurugram.rating_app.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import tech.gurugram.rating_app.model.Rating;
import tech.gurugram.rating_app.model.User;
import tech.gurugram.rating_app.model.Store;
import tech.gurugram.rating_app.repository.RatingRepository;
import tech.gurugram.rating_app.repository.UserRepository;
import tech.gurugram.rating_app.repository.StoreRepository;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/ratings")
public class RatingController {

    private final RatingRepository ratingRepository;
    private final UserRepository userRepository;
    private final StoreRepository storeRepository;

    public RatingController(RatingRepository ratingRepository,
                            UserRepository userRepository,
                            StoreRepository storeRepository) {
        this.ratingRepository = ratingRepository;
        this.userRepository = userRepository;
        this.storeRepository = storeRepository;
    }

    @GetMapping
    public List<Rating> listAll() {
        return ratingRepository.findAll();
    }

    @GetMapping("/store/{storeId}")
    public ResponseEntity<List<Rating>> listForStore(@PathVariable Long storeId) {
        List<Rating> list = ratingRepository.findByStoreId(storeId);
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Rating> get(@PathVariable Long id) {
        return ratingRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // create rating; expects JSON { "score":5, "comment":"...", "username":"admin", "storeId": 1 }
    @PostMapping
    public ResponseEntity<?> create(@RequestBody CreateRatingRequest req) {
        Optional<User> userOpt = userRepository.findByUsername(req.getUsername());
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("User not found: " + req.getUsername());
        }
        Optional<Store> storeOpt = storeRepository.findById(req.getStoreId());
        if (storeOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Store not found: " + req.getStoreId());
        }
        User user = userOpt.get();
        Store store = storeOpt.get();
        Rating r = new Rating(req.getScore(), req.getComment(), store, user);
        Rating saved = ratingRepository.save(r);
        return ResponseEntity.created(URI.create("/api/ratings/" + saved.getId())).body(saved);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!ratingRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        ratingRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    public static class CreateRatingRequest {
        private Integer score;
        private String comment;
        private String username;
        private Long storeId;

        public CreateRatingRequest() {}

        public Integer getScore() { return score; }
        public void setScore(Integer score) { this.score = score; }

        public String getComment() { return comment; }
        public void setComment(String comment) { this.comment = comment; }

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }

        public Long getStoreId() { return storeId; }
        public void setStoreId(Long storeId) { this.storeId = storeId; }
    }
}
