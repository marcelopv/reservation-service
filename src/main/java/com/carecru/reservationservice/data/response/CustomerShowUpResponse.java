package com.carecru.reservationservice.data.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CustomerShowUpResponse {

    private Long reservationId;
    private String personName;
    private Long reservationTime;
    private Integer numberOfPeople;
    private Double depositFee;
    private Double refund;
    private Long restaurantId;

}
