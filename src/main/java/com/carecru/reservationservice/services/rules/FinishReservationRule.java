package com.carecru.reservationservice.services.rules;

import com.carecru.reservationservice.entities.ReservationEntity;

public interface FinishReservationRule {

    double DEPOSIT_FEE = 10;

    boolean isApplied(Long differenceHours);

    ReservationEntity finish(ReservationEntity reservation);
}
