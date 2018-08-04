package com.carecru.reservationservice.repositories;

import com.carecru.reservationservice.entities.TimeSlotEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
public interface TimeSlotRepository extends JpaRepository<TimeSlotEntity, Long> {

    @Query(value = "select * from time_slots ts " +
            "join timeslot_restaurant on (timeslot_restaurant.timeslot_id = ts.id) " +
            "join restaurants on (restaurants.id = timeslot_restaurant.restaurant_id) " +
            "where restaurants.id = ?1 and " +
            "ts.id not in (select reservations.timeslot_id from reservations where reservations.status = 1)", nativeQuery = true)
    List<TimeSlotEntity> getFreeTimeSlots(Long restaurantId);

    @Query(value = "select * from time_slots ts " +
            "join timeslot_restaurant on (timeslot_restaurant.timeslot_id = ts.id) " +
            "join restaurants on (restaurants.id = timeslot_restaurant.restaurant_id) " +
            "where ts.time = ?1 and restaurants.id = ?2", nativeQuery = true)
    Optional<TimeSlotEntity> findByTimeAndRestaurantId(Timestamp time, Long restaurantId);
}
