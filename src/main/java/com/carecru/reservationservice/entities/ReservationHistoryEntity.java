package com.carecru.reservationservice.entities;

import lombok.*;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity(name = "ReservationHistory")
@Table(name = "reservation_history")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReservationHistoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id")
    private ReservationEntity reservation;

    @NonNull
    @Column(name = "person_name")
    private String personName;

    @NonNull
    @Column(name = "number_people")
    private Integer numberOfPeople;

    @NonNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "timeslot_id")
    private TimeSlotEntity timeSlot;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id")
    private RestaurantEntity restaurant;

    @Column(name = "status")
    private Integer status;

    @Column(name = "deposit_fee")
    private Double depositFee;

    @Column(name = "refund")
    private Double refund;

}
