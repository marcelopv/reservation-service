package com.carecru.reservationservice.repositories;

import com.carecru.reservationservice.entities.RestaurantEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RestaurantRepository extends JpaRepository<RestaurantEntity, Long>{

    Optional<RestaurantEntity> findByName(String name);
}
