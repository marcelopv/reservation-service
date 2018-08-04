package com.carecru.reservationservice.data.request;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RescheduleRequest {

    @NonNull private String personName;
    @NonNull private Long oldReservationTime;
    @NonNull private Long newReservationTime;
    @NonNull private Long restaurantId;

}
