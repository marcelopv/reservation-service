package com.carecru.reservationservice.services;

import com.carecru.reservationservice.data.request.AddRestaurantRequest;
import com.carecru.reservationservice.data.response.RestaurantResponse;
import com.carecru.reservationservice.entities.RestaurantEntity;
import com.carecru.reservationservice.repositories.RestaurantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RestaurantService {

    @Autowired
    private RestaurantRepository restaurantRepository;


    public Long addRestaurant(AddRestaurantRequest addRestaurantRequest) {
        Optional<RestaurantEntity> optionalRestaurant = restaurantRepository.findByName(addRestaurantRequest.getName());

        if (optionalRestaurant.isPresent()) {
            throw new RuntimeException("Could not add restaurant since it already exists.");
        }

        RestaurantEntity restaurant = restaurantRepository.save(RestaurantEntity.builder().name(addRestaurantRequest.getName()).build());
        return restaurant.getId();
    }

    public RestaurantResponse getRestaurant(Long restaurantId) {
        Optional<RestaurantEntity> optionalRestaurant = restaurantRepository.findById(restaurantId);

        if (!optionalRestaurant.isPresent()) {
            throw new RuntimeException("Could not find restaurant with id: {}" + restaurantId);
        } else {
            RestaurantEntity restaurantEntity = optionalRestaurant.get();

            return RestaurantResponse.builder()
                    .id(restaurantEntity.getId())
                    .name(restaurantEntity.getName())
                    .build();
        }
    }
}
