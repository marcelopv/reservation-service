package com.carecru.reservationservice.entities;

import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity(name = "Restaurants")
@Table(name = "restaurants")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class RestaurantEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    private String name;

    @ManyToMany(cascade = CascadeType.ALL, mappedBy = "restaurants")
    @Singular
    private List<TimeSlotEntity> timeSlots = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "restaurant")
    @Singular
    private List<ReservationEntity> reservations;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "restaurant")
    @Singular
    private List<ReservationHistoryEntity> reservationHistories;

}
