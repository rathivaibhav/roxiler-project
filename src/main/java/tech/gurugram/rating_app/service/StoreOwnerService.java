package tech.gurugram.rating_app.service;

import org.springframework.stereotype.Service;
import tech.gurugram.rating_app.dto.RatingUserDto;
import tech.gurugram.rating_app.dto.StoreOwnerDashboardDto;
import tech.gurugram.rating_app.model.Store;
import tech.gurugram.rating_app.repository.RatingRepository;
import tech.gurugram.rating_app.repository.StoreRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StoreOwnerService {

    private final StoreRepository storeRepository;
    private final RatingRepository ratingRepository;

    public StoreOwnerService(StoreRepository storeRepository, RatingRepository ratingRepository) {
        this.storeRepository = storeRepository;
        this.ratingRepository = ratingRepository;
    }

    public List<StoreOwnerDashboardDto> getOwnerDashboard(Long ownerId) {
        List<Store> stores = storeRepository.findByOwnerId(ownerId);

        return stores.stream().map(store -> {
            StoreOwnerDashboardDto dto = new StoreOwnerDashboardDto();
            dto.setId(store.getId());
            dto.setName(store.getName());
            dto.setAddress(store.getAddress());

            Double avgRating = ratingRepository.getAverageRatingByStoreId(store.getId());
            dto.setAverageRating(avgRating != null ? (Math.round(avgRating * 100.0) / 100.0) : null);

            dto.setRatingCount(ratingRepository.getRatingCountByStoreId(store.getId()));

            List<RatingUserDto> ratings = ratingRepository.findRatingsWithUserDetailsByStoreId(store.getId());
            dto.setRatings(ratings);

            return dto;
        }).collect(Collectors.toList());
    }
}