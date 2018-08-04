package com.carecru.reservationservice.data.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReservationCancellationResponse {

    private Long id;
    private Long reservationTime;
    private Double depositFee;
    private Double refund;
    private Long restaurantId;

}
