package com.carecru.reservationservice.data.response;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationListResponse {

    @NonNull private List<ReservationResponse> reservations;

}
