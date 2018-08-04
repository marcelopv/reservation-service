package com.carecru.reservationservice.data.response;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TimeSlotResponse {

    @NonNull private Long id;
    @NonNull private Long time;

}
