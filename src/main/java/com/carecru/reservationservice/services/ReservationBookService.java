package com.carecru.reservationservice.services;

import com.carecru.reservationservice.data.ReservationStatus;
import com.carecru.reservationservice.data.request.ReservationRequest;
import com.carecru.reservationservice.data.response.ReservationResponse;
import com.carecru.reservationservice.data.response.TimeSlotResponse;
import com.carecru.reservationservice.entities.ReservationEntity;
import com.carecru.reservationservice.entities.ReservationHistoryEntity;
import com.carecru.reservationservice.entities.RestaurantEntity;
import com.carecru.reservationservice.entities.TimeSlotEntity;
import com.carecru.reservationservice.repositories.ReservationHistoryRepository;
import com.carecru.reservationservice.repositories.ReservationRepository;
import com.carecru.reservationservice.repositories.TimeSlotRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class ReservationBookService {

    @Autowired
    private ReservationHistoryRepository reservationHistoryRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private TimeSlotRepository timeSlotRepository;

    @Transactional
    public ReservationResponse bookReservation(ReservationRequest request) {
        Optional<TimeSlotEntity> optionalTimeSlot = isTimeSlotExists(request.getRestaurantId(), request.getTimeSlot());

        List<ReservationEntity> userExistingReservations = reservationRepository.findByPersonNameAndTimeSlotAndStatus(
                request.getPersonName(), optionalTimeSlot.get(), ReservationStatus.IN_PROGRESS);

        if (!userExistingReservations.isEmpty()) {
            throw new RuntimeException("This reservation is already booked in the slot time.");
        }

        TimeSlotEntity timeSlot = optionalTimeSlot.get();
        RestaurantEntity restaurant = timeSlot.getRestaurants().get(0);

        ReservationEntity reservationEntity = ReservationEntity.builder()
                .numberOfPeople(request.getNumberOfPeople())
                .personName(request.getPersonName())
                .timeSlot(timeSlot)
                .restaurant(restaurant)
                .status(ReservationStatus.IN_PROGRESS)
                .build();

        ReservationEntity reservation = reservationRepository.save(reservationEntity);
        log.info("Stored reservation of person name: {} successfully.", reservation.getPersonName());

        storeReservationHistory(reservation);
        log.info("Stored reservation with id {} to history table successfully.", reservation.getId());

        return asResponse(reservation);
    }

    private Optional<TimeSlotEntity> isTimeSlotExists(Long restaurantId, Long reservationTime) {
        Timestamp reservationTimeStamp = Timestamp.from(Instant.ofEpochMilli(reservationTime));
        Optional<TimeSlotEntity> optionalTimeSlot = timeSlotRepository.findByTimeAndRestaurantId(reservationTimeStamp, restaurantId);
        if (!optionalTimeSlot.isPresent()) {
            throw new RuntimeException("Could not find time slot in request with restaurant of id: " + restaurantId);
        }
        return optionalTimeSlot;
    }

    private void storeReservationHistory(ReservationEntity reservation) {
        reservationHistoryRepository.save(ReservationHistoryEntity.builder()
                .reservation(reservation)
                .timeSlot(reservation.getTimeSlot())
                .restaurant(reservation.getRestaurant())
                .status(reservation.getStatus())
                .personName(reservation.getPersonName())
                .numberOfPeople(reservation.getNumberOfPeople())
                .depositFee(reservation.getDepositFee())
                .refund(reservation.getRefund())
                .build());
    }

    private ReservationResponse asResponse(ReservationEntity reservation) {
        TimeSlotEntity timeSlot = reservation.getTimeSlot();

        TimeSlotResponse timeSlotResponse = TimeSlotResponse.builder()
                .id(timeSlot.getId())
                .time(timeSlot.getTime().getTime())
                .build();

        return ReservationResponse.builder()
                .id(reservation.getId())
                .numberOfPeople(reservation.getNumberOfPeople())
                .personName(reservation.getPersonName())
                .status(reservation.getStatus())
                .timeSlot(timeSlotResponse)
                .build();
    }

}
