package com.carecru.reservationservice.services;

import com.carecru.reservationservice.data.*;
import com.carecru.reservationservice.data.response.CustomerShowUpResponse;
import com.carecru.reservationservice.data.response.ReservationResponse;
import com.carecru.reservationservice.data.response.TimeSlotResponse;
import com.carecru.reservationservice.entities.ReservationEntity;
import com.carecru.reservationservice.entities.ReservationHistoryEntity;
import com.carecru.reservationservice.entities.TimeSlotEntity;
import com.carecru.reservationservice.repositories.ReservationHistoryRepository;
import com.carecru.reservationservice.repositories.ReservationRepository;
import com.carecru.reservationservice.repositories.RestaurantRepository;
import com.carecru.reservationservice.repositories.TimeSlotRepository;
import com.carecru.reservationservice.services.rules.FinishReservationRule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ReservationService {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private ReservationHistoryRepository reservationHistoryRepository;

    @Autowired
    private TimeSlotRepository timeSlotRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private List<FinishReservationRule> finishReservationRules;

    public CustomerShowUpResponse customerShowUp(Long reservationId) {
        Optional<ReservationEntity> optionalReservation = reservationRepository.findById(reservationId);

        if (!optionalReservation.isPresent()) {
            throw new RuntimeException("Could not find reservation id " + reservationId + " for customer show up request.");
        }

        ReservationEntity reservation = optionalReservation.get();
        reservation.setStatus(ReservationStatus.DONE_CUSTOMER_SHOW_UP);
        reservation.setDepositFee(0.0);
        reservation.setRefund(FinishReservationRule.DEPOSIT_FEE * reservation.getNumberOfPeople());
        reservationRepository.save(reservation);
        log.info("Customer show up for reservation with id {}", reservation.getId());

        return CustomerShowUpResponse.builder()
                .reservationId(reservation.getId())
                .personName(reservation.getPersonName())
                .refund(reservation.getRefund())
                .depositFee(reservation.getDepositFee())
                .reservationTime(reservation.getTimeSlot().getTime().getTime())
                .restaurantId(reservation.getRestaurant().getId())
                .numberOfPeople(reservation.getNumberOfPeople())
                .build();
    }

    public ReservationResponse getReservation(Long reservationId) {
        Optional<ReservationEntity> optionalReservation = reservationRepository.findById(reservationId);

        if (!optionalReservation.isPresent()) {
            throw new RuntimeException("Could not find reservation with id "+reservationId);
        }

        return asResponse(optionalReservation.get());
    }

    public List<ReservationResponse> getReservationsOfRestaurantAndDate(Long restaurantId, Long date) {
        List<ReservationHistoryEntity> result;

        if (date == null) {
            result = reservationHistoryRepository.findByRestaurantId(restaurantId);
        } else {
            Date reservationTime = Date.from(Instant.ofEpochMilli(date));
            result = reservationHistoryRepository.findByRestaurantIdAndDate(restaurantId, reservationTime);
        }

        return result.stream().map(this::asResponse).collect(Collectors.toList());
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

    private ReservationResponse asResponse(ReservationHistoryEntity reservation) {
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
