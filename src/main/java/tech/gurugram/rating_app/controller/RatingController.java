package tech.gurugram.rating_app.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.gurugram.rating_app.config.SecurityUtil;
import tech.gurugram.rating_app.model.Rating;
import tech.gurugram.rating_app.model.Store;
import tech.gurugram.rating_app.model.User;
import tech.gurugram.rating_app.repository.RatingRepository;
import tech.gurugram.rating_app.repository.StoreRepository;
import tech.gurugram.rating_app.repository.UserRepository;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class RatingController {

    private final RatingRepository ratingRepository;
    private final StoreRepository storeRepository;
    private final UserRepository userRepository;

    public RatingController(RatingRepository r, StoreRepository s, UserRepository u) {
        this.ratingRepository = r;
        this.storeRepository = s;
        this.userRepository = u;
    }

    private List<Map<String, Object>> enrichRatingsWithUserData(List<Rating> ratings) {
        return ratings.stream().map(r -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", r.getId());
            map.put("score", r.getScore());
            map.put("comment", r.getComment());
            map.put("createdAt", r.getCreatedAt());
            if (r.getUser() != null) {
                map.put("user", Map.of(
                        "id", r.getUser().getId(),
                        "name", r.getUser().getName(),
                        "email", r.getUser().getEmail()
                ));
            } else {
                map.put("user", null);
            }
            return map;
        }).collect(Collectors.toList());
    }
    @PostMapping("/ratings")
    public ResponseEntity<?> createOrUpdateRating(@RequestBody RatingRequest req) {
        if (req.storeId == null || req.score == null) {
            return ResponseEntity.badRequest().body("storeId_and_score_required");
        }
        if (req.score < 1 || req.score > 5) {
            return ResponseEntity.badRequest().body("score_must_be_between_1_and_5");
        }

        Long userId = SecurityUtil.currentUserId();
        if (userId == null) {
            return ResponseEntity.status(401).body("not_authenticated");
        }

        Store store = storeRepository.findById(req.storeId)
                .orElse(null);
        if (store == null) {
            return ResponseEntity.badRequest().body("store_not_found");
        }

        User user = userRepository.findById(userId).get();

        Rating rating = ratingRepository.findByStoreIdAndUserId(req.storeId, userId)
                .orElse(new Rating());

        rating.setStore(store);
        rating.setUser(user);
        rating.setScore(req.score);
        rating.setComment(req.comment);

        Rating saved = ratingRepository.save(rating);

        return ResponseEntity.ok(saved);
    }

    public static class RatingRequest {
        public Integer score;
        public String comment;
        public Long storeId;
    }
}