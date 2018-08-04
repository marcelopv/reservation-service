package com.carecru.reservationservice.data.response;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RestaurantResponse {

    @NonNull private Long id;
    @NonNull private String name;

}
