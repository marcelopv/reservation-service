package com.carecru.reservationservice.data.request;

import lombok.*;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerShowUpRequest {

    @NonNull private Long reservationId;

}
