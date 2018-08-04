package com.carecru.reservationservice.repositories;

import com.carecru.reservationservice.entities.ReservationHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

@Repository
public interface ReservationHistoryRepository extends JpaRepository<ReservationHistoryEntity, Long> {

    List<ReservationHistoryEntity> findByReservationId(Long reservationId);

    List<ReservationHistoryEntity> findByRestaurantId(Long restaurantId);

    @Query(value = "select * from reservation_history rh "+
            "join time_slots on (rh.timeslot_id = time_slots.id) " +
            "where restaurant_id = ?1 and date_part('dow', time_slots.time) = date_part('dow', cast(?2 as date))"
            ,nativeQuery = true)
    List<ReservationHistoryEntity> findByRestaurantIdAndDate(Long restaurantId, Date date);

    ReservationHistoryEntity getByReservationId(Long reservationId);
}
