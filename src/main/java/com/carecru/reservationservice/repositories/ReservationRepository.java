package com.carecru.reservationservice.repositories;

import com.carecru.reservationservice.entities.ReservationEntity;
import com.carecru.reservationservice.entities.RestaurantEntity;
import com.carecru.reservationservice.entities.TimeSlotEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<ReservationEntity, Long> {

    List<ReservationEntity> findByPersonNameAndTimeSlotAndStatus(String personName, TimeSlotEntity timeSlot, Integer status);

    List<ReservationEntity> findByPersonNameAndTimeSlot(String personName, TimeSlotEntity timeSlot);

    List<ReservationEntity> findByStatus(Integer status);

    @Query(value = "select * from reservations " +
            "join time_slots ts on (ts.id = reservations.timeslot_id) "+
            "where reservations.restaurant_id = ?1 and ts.time = ?2", nativeQuery = true)
    List<ReservationEntity> findByRestaurantIdAndDate(Long restaurantId, Timestamp date);
}
