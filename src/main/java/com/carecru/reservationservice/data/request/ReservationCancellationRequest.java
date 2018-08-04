package com.carecru.reservationservice.data.request;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReservationCancellationRequest {

    @NonNull private Long reservationTime;
    @NonNull private String personName;
    @NonNull private Long restaurantId;

}
