package com.carecru.reservationservice.services.rules;

import com.carecru.reservationservice.data.ReservationStatus;
import com.carecru.reservationservice.entities.ReservationEntity;

public class CancelledBetween12Hours implements FinishReservationRule {

    @Override
    public boolean isApplied(Long differenceHours) {
        return differenceHours >= 2;
    }

    @Override
    public ReservationEntity finish(ReservationEntity reservation) {
        reservation.setStatus(ReservationStatus.CANCELLED_BETWEEN_11_59_HOUR_AND_2_HOUR_IN_ADVANCE);
        reservation.setDepositFee(DEPOSIT_FEE * reservation.getNumberOfPeople() * 0.5);
        reservation.setRefund(DEPOSIT_FEE * reservation.getNumberOfPeople() * 0.5);
        return reservation;
    }
}
