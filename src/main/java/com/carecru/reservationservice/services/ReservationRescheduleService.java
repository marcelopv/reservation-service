package com.carecru.reservationservice.services;

import com.carecru.reservationservice.data.ReservationStatus;
import com.carecru.reservationservice.data.request.RescheduleRequest;
import com.carecru.reservationservice.data.response.RescheduleResponse;
import com.carecru.reservationservice.data.response.ReservationCancellationResponse;
import com.carecru.reservationservice.data.response.ReservationResponse;
import com.carecru.reservationservice.data.response.TimeSlotResponse;
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
public class ReservationRescheduleService {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private ReservationCancelService reservationCancelService;

    @Autowired
    private TimeSlotRepository timeSlotRepository;

    @Autowired
    private List<FinishReservationRule> finishReservationRules;

    public RescheduleResponse rescheduleReservation(RescheduleRequest rescheduleRequest) {
        Optional<TimeSlotEntity> optionalOldTimeSlot = isTimeSlotExists(rescheduleRequest.getRestaurantId(), rescheduleRequest.getOldReservationTime());
        Optional<TimeSlotEntity> optionalNewTimeSlot = isTimeSlotExists(rescheduleRequest.getRestaurantId(), rescheduleRequest.getNewReservationTime());
        List<ReservationEntity> reservations = getReservationFromTimeSlot(rescheduleRequest, optionalOldTimeSlot);
        ReservationEntity reservation = reservations.get(0);

        ReservationCancellationResponse cancellationResponse = reservationCancelService.markReservationAsFinished(reservation);

        ReservationEntity newScheduledReservation = createNewScheduledReservation(reservation, optionalNewTimeSlot.get());
        ReservationResponse reservationResponse = asResponse(reservationRepository.save(newScheduledReservation));

        return RescheduleResponse.builder()
                .oldReservation(cancellationResponse)
                .newReservation(reservationResponse)
                .build();
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

    private Optional<TimeSlotEntity> isTimeSlotExists(Long restaurantId, Long reservationTime) {
        Timestamp reservationTimeStamp = Timestamp.from(Instant.ofEpochMilli(reservationTime));
        Optional<TimeSlotEntity> optionalTimeSlot = timeSlotRepository.findByTimeAndRestaurantId(reservationTimeStamp, restaurantId);
        if (!optionalTimeSlot.isPresent()) {
            throw new RuntimeException("Could not find time slot in request with restaurant of id: " + restaurantId);
        }
        return optionalTimeSlot;
    }

    private List<ReservationEntity> getReservationFromTimeSlot(RescheduleRequest rescheduleRequest, Optional<TimeSlotEntity> optionalOldTimeSlot) {
        TimeSlotEntity oldTimeSlot = optionalOldTimeSlot.get();
        List<ReservationEntity> reservations = reservationRepository.findByPersonNameAndTimeSlot(rescheduleRequest.getPersonName(), oldTimeSlot);

        if (reservations.isEmpty()) {
            throw new RuntimeException("Could not find reservation by person name and time slot in rescheduling request: "+ rescheduleRequest);
        }
        return reservations;
    }

    private ReservationEntity createNewScheduledReservation(ReservationEntity reservation, TimeSlotEntity newTimeSlot) {
        return ReservationEntity.builder()
                .personName(reservation.getPersonName())
                .numberOfPeople(reservation.getNumberOfPeople())
                .status(ReservationStatus.IN_PROGRESS)
                .timeSlot(newTimeSlot)
                .build();
    }

}
