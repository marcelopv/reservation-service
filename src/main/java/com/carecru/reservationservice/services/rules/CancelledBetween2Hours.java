package com.carecru.reservationservice.services.rules;

import com.carecru.reservationservice.data.ReservationStatus;
import com.carecru.reservationservice.entities.ReservationEntity;

public class CancelledBetween2Hours implements FinishReservationRule {

    @Override
    public boolean isApplied(Long differenceHours) {
        return differenceHours >= 0;
    }

    @Override
    public ReservationEntity finish(ReservationEntity reservation) {
        reservation.setStatus(ReservationStatus.CANCELLED_BETWEEN_01_59_HOUR_AND_1_MINUTE_IN_ADVANCE);
        reservation.setDepositFee(DEPOSIT_FEE * reservation.getNumberOfPeople() * 0.75);
        reservation.setRefund(DEPOSIT_FEE * reservation.getNumberOfPeople() * 0.25);
        return reservation;
    }
}
