package com.carecru.reservationservice.services;

import com.carecru.reservationservice.data.request.ReservationCancellationRequest;
import com.carecru.reservationservice.data.response.ReservationCancellationResponse;
import com.carecru.reservationservice.entities.ReservationEntity;
import com.carecru.reservationservice.entities.TimeSlotEntity;
import com.carecru.reservationservice.repositories.ReservationRepository;
import com.carecru.reservationservice.repositories.TimeSlotRepository;
import com.carecru.reservationservice.services.rules.FinishReservationRule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class ReservationCancelService {

    @Autowired
    private List<FinishReservationRule> finishReservationRules;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private TimeSlotRepository timeSlotRepository;

    public ReservationCancellationResponse cancelReservation(ReservationCancellationRequest cancellationRequest) {
        Optional<TimeSlotEntity> optionalTimeSlot = isTimeSlotExists(cancellationRequest.getRestaurantId(), cancellationRequest.getReservationTime());

        List<ReservationEntity> reservations = reservationRepository.findByPersonNameAndTimeSlot(cancellationRequest.getPersonName(), optionalTimeSlot.get());

        if (reservations.isEmpty()) {
            throw new RuntimeException("Could not find reservation in cancellation request: " + cancellationRequest);
        }

        ReservationEntity reservation = reservations.get(0);
        return markReservationAsFinished(reservation);
    }

    private Optional<TimeSlotEntity> isTimeSlotExists(Long restaurantId, Long reservationTime) {
        Timestamp reservationTimeStamp = Timestamp.from(Instant.ofEpochMilli(reservationTime));
        Optional<TimeSlotEntity> optionalTimeSlot = timeSlotRepository.findByTimeAndRestaurantId(reservationTimeStamp, restaurantId);
        if (!optionalTimeSlot.isPresent()) {
            throw new RuntimeException("Could not find time slot in request with restaurant of id: " + restaurantId);
        }
        return optionalTimeSlot;
    }

    public ReservationCancellationResponse markReservationAsFinished(ReservationEntity reservation) {
        Timestamp reservationTime = reservation.getTimeSlot().getTime();
        LocalDateTime reservationLocalTime = reservationTime.toLocalDateTime();
        LocalDateTime currentLocalTime = LocalDateTime.ofInstant(Instant.now(), ZoneId.systemDefault());

        Long differenceHours = Duration.between(reservationLocalTime, currentLocalTime).abs().toHours();

        for (FinishReservationRule finishReservationRule : finishReservationRules) {
            if (finishReservationRule.isApplied(differenceHours)){
                ReservationEntity finishedReservation = finishReservationRule.finish(reservation);
                reservationRepository.save(finishedReservation);
                log.info("Marked reservation with id {} as finished with status {}", reservation.getId(), reservation.getStatus());

                return ReservationCancellationResponse.builder()
                        .id(reservation.getId())
                        .reservationTime(reservation.getTimeSlot().getTime().getTime())
                        .depositFee(reservation.getDepositFee())
                        .refund(reservation.getRefund())
                        .restaurantId(reservation.getRestaurant().getId())
                        .build();
            }
        }

        throw new RuntimeException("Could not mark reservation as finished");
    }

}
