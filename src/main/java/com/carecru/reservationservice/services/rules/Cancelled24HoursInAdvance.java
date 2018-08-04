package com.carecru.reservationservice.services.rules;

import com.carecru.reservationservice.data.ReservationStatus;
import com.carecru.reservationservice.entities.ReservationEntity;

public class Cancelled24HoursInAdvance implements FinishReservationRule {

    @Override
    public boolean isApplied(Long differenceHours) {
        return differenceHours >= 24;
    }

    @Override
    public ReservationEntity finish(ReservationEntity reservation) {
        reservation.setStatus(ReservationStatus.CANCELLED_24_HOURS_IN_ADVANCE);
        reservation.setDepositFee(0.0);
        reservation.setRefund(DEPOSIT_FEE * reservation.getNumberOfPeople());
        return reservation;
    }
}
