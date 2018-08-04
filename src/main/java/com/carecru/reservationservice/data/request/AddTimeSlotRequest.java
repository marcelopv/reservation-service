package com.carecru.reservationservice.data.request;

import lombok.*;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddTimeSlotRequest {

    @NonNull private Long timeSlot;
    @NonNull private Long restaurantId;

}
