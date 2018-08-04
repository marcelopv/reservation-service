package com.carecru.reservationservice.data.request;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReservationRequest {

    @NonNull private String personName;
    @NonNull private Integer numberOfPeople;
    @NonNull private Long timeSlot;
    @NonNull private Long restaurantId;

}
