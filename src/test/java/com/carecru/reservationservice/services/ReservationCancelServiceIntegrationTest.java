package com.carecru.reservationservice.services;

import com.carecru.reservationservice.ReservationServiceApplication;
import com.carecru.reservationservice.config.DatabaseTestConfig;
import com.carecru.reservationservice.data.ReservationStatus;
import com.carecru.reservationservice.data.request.ReservationCancellationRequest;
import com.carecru.reservationservice.data.request.ReservationRequest;
import com.carecru.reservationservice.data.response.ReservationResponse;
import com.carecru.reservationservice.entities.ReservationEntity;
import com.carecru.reservationservice.entities.RestaurantEntity;
import com.carecru.reservationservice.entities.TimeSlotEntity;
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

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {ReservationServiceApplication.class, DatabaseTestConfig.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD) //this is added in order to each time has its lifecycle
@Transactional
public class ReservationCancelServiceIntegrationTest {

    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm");

    @Autowired
    private ReservationCancelService reservationCancelService;

    @Autowired
    private ReservationBookService reservationBookService;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private TimeSlotRepository timeSlotRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Test
    public void test_that_marks_reservation_as_cancelled_24_in_advance(){
        String personName = "Maximilian";
        LocalDateTime currentDateTime = LocalDateTime.ofInstant(Instant.now(), ZoneId.systemDefault());
        LocalDateTime reservationDateTime = currentDateTime.minusHours(24);
        RestaurantEntity restaurant = storeRestaurant();
        storeTimeSlot(reservationDateTime, restaurant);
        int numberOfPeople = 2;
        ReservationResponse reservation = storeReservation(personName, Timestamp.valueOf(reservationDateTime).getTime(), restaurant.getId(), numberOfPeople);
        ReservationCancellationRequest cancellationRequest = anCancellationRequest(reservation.getPersonName(), Timestamp.valueOf(reservationDateTime).getTime(), restaurant.getId());

        reservationCancelService.cancelReservation(cancellationRequest);

        ReservationEntity reservationEntity = reservationRepository.getOne(reservation.getId());
        assertThat(reservationEntity.getStatus()).isEqualTo(ReservationStatus.CANCELLED_24_HOURS_IN_ADVANCE);
        assertThat(reservationEntity.getRefund()).isEqualTo(20);
        assertThat(reservationEntity.getDepositFee()).isEqualTo(0);
    }

    @Test
    public void test_that_marks_reservation_as_cancelled_between_23_59_hours_and_12_hours_in_advance(){
        String personName = "Maximilian";
        LocalDateTime currentDateTime = LocalDateTime.ofInstant(Instant.now(), ZoneId.systemDefault());
        LocalDateTime reservationDateTime = currentDateTime.minusHours(23);
        RestaurantEntity restaurant = storeRestaurant();
        storeTimeSlot(reservationDateTime, restaurant);
        int numberOfPeople = 2;
        ReservationResponse reservation = storeReservation(personName, Timestamp.valueOf(reservationDateTime).getTime(), restaurant.getId(), numberOfPeople);
        ReservationCancellationRequest cancellationRequest = anCancellationRequest(reservation.getPersonName(), Timestamp.valueOf(reservationDateTime).getTime(), restaurant.getId());

        reservationCancelService.cancelReservation(cancellationRequest);

        ReservationEntity reservationEntity = reservationRepository.getOne(reservation.getId());
        assertThat(reservationEntity.getStatus()).isEqualTo(ReservationStatus.CANCELLED_BETWEEN_23_59_HOUR_AND_12_HOUR_IN_ADVANCE);
        assertThat(reservationEntity.getRefund()).isEqualTo(15);
        assertThat(reservationEntity.getDepositFee()).isEqualTo(5);
    }

    @Test
    public void test_that_marks_reservation_as_cancelled_between_11_59_hours_and_2_hours_in_advance(){
        String personName = "Maximilian";
        LocalDateTime currentDateTime = LocalDateTime.ofInstant(Instant.now(), ZoneId.systemDefault());
        LocalDateTime reservationDateTime = currentDateTime.minusHours(11);
        RestaurantEntity restaurant = storeRestaurant();
        storeTimeSlot(reservationDateTime, restaurant);
        int numberOfPeople = 2;
        ReservationResponse reservation = storeReservation(personName, Timestamp.valueOf(reservationDateTime).getTime(), restaurant.getId(), numberOfPeople);
        ReservationCancellationRequest cancellationRequest = anCancellationRequest(reservation.getPersonName(), Timestamp.valueOf(reservationDateTime).getTime(), restaurant.getId());

        reservationCancelService.cancelReservation(cancellationRequest);

        ReservationEntity reservationEntity = reservationRepository.getOne(reservation.getId());
        assertThat(reservationEntity.getStatus()).isEqualTo(ReservationStatus.CANCELLED_BETWEEN_11_59_HOUR_AND_2_HOUR_IN_ADVANCE);
        assertThat(reservationEntity.getRefund()).isEqualTo(10);
        assertThat(reservationEntity.getDepositFee()).isEqualTo(10);
    }

    @Test
    public void test_that_marks_reservation_as_cancelled_between_01_59_hours_and_1_minute_in_advance(){
        String personName = "Maximilian";
        LocalDateTime currentDateTime = LocalDateTime.ofInstant(Instant.now(), ZoneId.systemDefault());
        LocalDateTime reservationDateTime = currentDateTime.minusHours(1);
        RestaurantEntity restaurant = storeRestaurant();
        storeTimeSlot(reservationDateTime, restaurant);
        int numberOfPeople = 2;
        ReservationResponse reservation = storeReservation(personName, Timestamp.valueOf(reservationDateTime).getTime(), restaurant.getId(), numberOfPeople);
        ReservationCancellationRequest cancellationRequest = anCancellationRequest(reservation.getPersonName(), Timestamp.valueOf(reservationDateTime).getTime(), restaurant.getId());

        reservationCancelService.cancelReservation(cancellationRequest);

        ReservationEntity reservationEntity = reservationRepository.getOne(reservation.getId());
        assertThat(reservationEntity.getStatus()).isEqualTo(ReservationStatus.CANCELLED_BETWEEN_01_59_HOUR_AND_1_MINUTE_IN_ADVANCE);
        assertThat(reservationEntity.getRefund()).isEqualTo(5);
        assertThat(reservationEntity.getDepositFee()).isEqualTo(15);
    }

    private RestaurantEntity storeRestaurant() {
        return restaurantRepository.save(RestaurantEntity.builder().name("Pizza rest").build());
    }

    private TimeSlotEntity storeTimeSlot(LocalDateTime localDateTime, RestaurantEntity restaurantEntity) {
        Timestamp timeSlot = Timestamp.valueOf(localDateTime);
        TimeSlotEntity timeSlotEntity = TimeSlotEntity.builder().time(timeSlot).restaurant(restaurantEntity).build();
        return timeSlotRepository.save(timeSlotEntity);
    }

    private ReservationResponse storeReservation(String personName, Long timeSlot, Long restaurantId, int numberOfPeople) {
        ReservationRequest reservationRequest = anReservation(personName, timeSlot, restaurantId, numberOfPeople);
        return reservationBookService.bookReservation(reservationRequest);
    }

    private ReservationCancellationRequest anCancellationRequest(String personName, Long timeSlot, Long restaurantId) {
        return ReservationCancellationRequest.builder()
                .personName(personName)
                .reservationTime(timeSlot)
                .restaurantId(restaurantId)
                .build();
    }

    private ReservationRequest anReservation(String personName, Long timeSlot, Long restaurantId, int numberOfPeople) {
        return ReservationRequest.builder()
                    .numberOfPeople(numberOfPeople)
                    .personName(personName)
                    .timeSlot(timeSlot)
                    .restaurantId(restaurantId)
                    .build();
    }

}
