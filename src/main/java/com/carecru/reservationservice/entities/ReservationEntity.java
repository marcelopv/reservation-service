package com.carecru.reservationservice.entities;

import lombok.*;

import javax.persistence.*;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "Reservations")
@Table(name = "reservations")
public class ReservationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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

    @Column(name = "status")
    private Integer status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id")
    private RestaurantEntity restaurant;

    @Column(name = "deposit_fee")
    private Double depositFee;

    @Column(name = "refund")
    private Double refund;

    @OneToOne(mappedBy = "reservation", cascade = {CascadeType.MERGE},
            fetch = FetchType.LAZY, optional = false)
    private ReservationHistoryEntity reservationHistory;

}
