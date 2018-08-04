package com.carecru.reservationservice.services;

import com.carecru.reservationservice.ReservationServiceApplication;
import com.carecru.reservationservice.config.DatabaseTestConfig;
import com.carecru.reservationservice.data.*;
import com.carecru.reservationservice.data.request.RescheduleRequest;
import com.carecru.reservationservice.data.request.ReservationCancellationRequest;
import com.carecru.reservationservice.data.request.ReservationRequest;
import com.carecru.reservationservice.data.response.ReservationResponse;
import com.carecru.reservationservice.entities.ReservationEntity;
import com.carecru.reservationservice.entities.ReservationHistoryEntity;
import com.carecru.reservationservice.entities.RestaurantEntity;
import com.carecru.reservationservice.entities.TimeSlotEntity;
import com.carecru.reservationservice.repositories.ReservationHistoryRepository;
import com.carecru.reservationservice.repositories.ReservationRepository;
import com.carecru.reservationservice.repositories.RestaurantRepository;
import com.carecru.reservationservice.repositories.TimeSlotRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {ReservationServiceApplication.class, DatabaseTestConfig.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD) //this is added in order to each time has its lifecycle
@Transactional
public class ReservationServiceIntegrationTest {

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private ReservationBookService reservationBookService;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private TimeSlotRepository timeSlotRepository;

    @Autowired
    private ReservationHistoryRepository reservationHistoryRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Test
    public void test_that_marks_reservation_as_status_customer_show_up(){
        RestaurantEntity restaurant = storeRestaurant();
        TimeSlotEntity timeSlot = storeTimeSlot(15, 11, 30, restaurant);
        ReservationRequest reservationRequest = anReservation("John Doe", timeSlot.getTime().getTime(), restaurant.getId());
        ReservationResponse reservation = reservationBookService.bookReservation(reservationRequest);

        reservationService.customerShowUp(reservation.getId());

        ReservationEntity reservationEntity = reservationRepository.getOne(reservation.getId());
        assertThat(reservationEntity.getStatus()).isEqualTo(ReservationStatus.DONE_CUSTOMER_SHOW_UP);
        assertThat(reservationEntity.getRefund()).isEqualTo(20);
        assertThat(reservationEntity.getDepositFee()).isEqualTo(0);
    }

    private RestaurantEntity storeRestaurant() {
        return restaurantRepository.save(RestaurantEntity.builder().name("Pizza rest").build());
    }

    private TimeSlotEntity storeTimeSlot(int day, int hour, int minute, RestaurantEntity restaurantEntity) {
        LocalDateTime reservationDateTime = LocalDateTime.of(2018, 5, day, hour, minute);
        Timestamp timeSlot = Timestamp.valueOf(reservationDateTime);
        TimeSlotEntity timeSlotEntity = TimeSlotEntity.builder().time(timeSlot).restaurant(restaurantEntity).build();
        return timeSlotRepository.save(timeSlotEntity);
    }

    private ReservationRequest anReservation(String personName, Long timeSlot, Long restaurantId) {
        return ReservationRequest.builder()
                    .numberOfPeople(2)
                    .personName(personName)
                    .timeSlot(timeSlot)
                    .restaurantId(restaurantId)
                    .build();
    }

}
