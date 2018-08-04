package com.carecru.reservationservice.entities;

import lombok.*;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "TimeSlots")
@Table(name = "time_slots")
public class TimeSlotEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    private Timestamp time;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "timeSlot")
    private List<ReservationEntity> reservations;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "timeSlot")
    private List<ReservationEntity> reservationHistories;

    @ManyToMany(cascade = {CascadeType.ALL})
    @JoinTable(name = "timeslot_restaurant",
            joinColumns = @JoinColumn(name = "timeslot_id"),
            inverseJoinColumns = @JoinColumn(name = "restaurant_id")
    )
    @Singular
    private List<RestaurantEntity> restaurants;

}
