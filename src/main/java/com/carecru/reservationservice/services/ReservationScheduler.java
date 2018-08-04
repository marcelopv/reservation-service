package com.carecru.reservationservice.services;

import com.carecru.reservationservice.data.ReservationStatus;
import com.carecru.reservationservice.entities.ReservationEntity;
import com.carecru.reservationservice.entities.ReservationHistoryEntity;
import com.carecru.reservationservice.repositories.ReservationHistoryRepository;
import com.carecru.reservationservice.repositories.ReservationRepository;
import com.carecru.reservationservice.services.rules.FinishReservationRule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@Slf4j
public class ReservationScheduler {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private ReservationHistoryRepository reservationHistoryRepository;

    @Scheduled(cron = "0 0 3 * * *")
    @Transactional
    public void deleteReservationsOfDay(){
        log.info("[DELETION_OF_RESERVATIONS_OF_DAY] Starting deletion of reservations of day.");
        reservationRepository.deleteAll();
        log.info("[DELETION_OF_RESERVATIONS_OF_DAY] Finished deletion of reservations of day.");
    }

    @Scheduled(cron = "0 0 1 * * *")
    @Transactional
    public void markInProgressReservationsAsDone() {
        log.info("[MARK_IN_PROGRESS_RESERVATIONS_AS_DONE] Starting mark in progress reservations as done customer not show up.");
        List<ReservationEntity> inProgressReservations = reservationRepository.findByStatus(ReservationStatus.IN_PROGRESS);
        log.info("[MARK_IN_PROGRESS_RESERVATIONS_AS_DONE] Number of in progress reservations to be mark as done customer not show up: {}", inProgressReservations.size());

        inProgressReservations.forEach(reservation -> {
            reservation.setStatus(ReservationStatus.DONE_CUSTOMER_NOT_SHOW_UP);
            reservation.setRefund(0.0);
            reservation.setDepositFee(FinishReservationRule.DEPOSIT_FEE * reservation.getNumberOfPeople());
            reservationRepository.save(reservation);

            ReservationHistoryEntity reservationHistoryEntity = reservationHistoryRepository.getByReservationId(reservation.getId());
            reservationHistoryEntity.setStatus(ReservationStatus.DONE_CUSTOMER_NOT_SHOW_UP);
            reservationHistoryEntity.setRefund(reservation.getRefund());
            reservationHistoryEntity.setDepositFee(reservation.getDepositFee());
            reservationHistoryRepository.save(reservationHistoryEntity);
            log.info("[MARK_IN_PROGRESS_RESERVATIONS_AS_DONE] Marked reservation id {} as done customer not show up.", reservation.getId());
        });
        log.info("[MARK_IN_PROGRESS_RESERVATIONS_AS_DONE] Finished mark in progress reservations as done customer not show up.");
    }
}
