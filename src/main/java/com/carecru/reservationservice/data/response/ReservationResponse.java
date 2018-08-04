package com.carecru.reservationservice.data.response;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationResponse {

    @NonNull private Long id;
    @NonNull private String personName;
    @NonNull private Integer numberOfPeople;
    @NonNull private TimeSlotResponse timeSlot;
    @NonNull private Integer status;

}
