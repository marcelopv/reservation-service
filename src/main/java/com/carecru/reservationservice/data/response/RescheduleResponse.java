package com.carecru.reservationservice.data.response;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RescheduleResponse {

    @NonNull private ReservationCancellationResponse oldReservation;
    @NonNull private ReservationResponse newReservation;

}
