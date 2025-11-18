package tech.gurugram.rating_app.service;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import tech.gurugram.rating_app.dto.StoreListingDto;
import tech.gurugram.rating_app.model.Rating;
import tech.gurugram.rating_app.model.Store;
import tech.gurugram.rating_app.repository.RatingRepository;
import tech.gurugram.rating_app.repository.StoreRepository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class StoreService {

    private final StoreRepository storeRepository;
    private final RatingRepository ratingRepository;

    public StoreService(StoreRepository storeRepository, RatingRepository ratingRepository) {
        this.storeRepository = storeRepository;
        this.ratingRepository = ratingRepository;
    }

    public List<StoreListingDto> getStoreListings(Long userId, String name, String address) {
        Specification<Store> spec = (root, q, cb) -> cb.conjunction();
        if (StringUtils.hasText(name)) {
            spec = spec.and((root, q, cb) ->
                    cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%"));
        }
        if (StringUtils.hasText(address)) {
            spec = spec.and((root, q, cb) ->
                    cb.like(cb.lower(root.get("address")), "%" + address.toLowerCase() + "%"));
        }

        List<Store> stores = storeRepository.findAll(spec);

        List<Long> storeIds = stores.stream().map(Store::getId).collect(Collectors.toList());
        Map<Long, Integer> userRatingsMap = ratingRepository.findByStoreIdInAndUserId(storeIds, userId)
                .stream()
                .collect(Collectors.toMap(r -> r.getStore().getId(), Rating::getScore));

        return stores.stream().map(store -> {
            StoreListingDto dto = new StoreListingDto();
            dto.setId(store.getId());
            dto.setName(store.getName());
            dto.setAddress(store.getAddress());
            dto.setEmail(store.getEmail());

            Double avg = ratingRepository.getAverageRatingByStoreId(store.getId());
            dto.setOverallRating(avg != null ? (Math.round(avg * 100.0) / 100.0) : null);

            dto.setUserSubmittedRating(userRatingsMap.get(store.getId()));

            return dto;
        }).collect(Collectors.toList());
    }
}