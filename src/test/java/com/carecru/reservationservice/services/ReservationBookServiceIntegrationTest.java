package com.carecru.reservationservice.services;

import com.carecru.reservationservice.ReservationServiceApplication;
import com.carecru.reservationservice.config.DatabaseTestConfig;
import com.carecru.reservationservice.data.ReservationStatus;
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
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {ReservationServiceApplication.class, DatabaseTestConfig.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD) //this is added in order to each time has its lifecycle
@Transactional
public class ReservationBookServiceIntegrationTest {

    @Autowired
    private ReservationBookService reservationService;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private TimeSlotRepository timeSlotRepository;

    @Autowired
    private ReservationHistoryRepository reservationHistoryRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Test
    public void test_that_books_a_reservation_for_a_restaurant(){
        LocalDateTime timeSlotDateTime = LocalDateTime.of(2018, 5, 18, 11, 30);
        RestaurantEntity restaurant = storeRestaurant();
        TimeSlotEntity timeSlot = storeTimeSlot(timeSlotDateTime, restaurant);
        ReservationRequest reservationRequest = anReservation("John Doe", timeSlot.getTime().getTime(), restaurant.getId());

        ReservationResponse reservationBooked = reservationService.bookReservation(reservationRequest);

        ReservationEntity reservation = reservationRepository.getOne(reservationBooked.getId());
        assertThat(reservation).isNotNull();
        assertThat(reservation.getRestaurant().getName()).isEqualTo(restaurant.getName());
    }

    @Test
    public void test_that_adds_reservation_to_reservation_history_table_when_books_a_reservation(){
        RestaurantEntity restaurant = storeRestaurant();
        TimeSlotEntity timeSlot = storeTimeSlot(15, 11, 30, restaurant);
        ReservationRequest reservationRequest = anReservation("John Doe", timeSlot.getTime().getTime(), restaurant.getId());

        ReservationResponse reservation = reservationService.bookReservation(reservationRequest);

        List<ReservationHistoryEntity> reservationHistories = reservationHistoryRepository.findByReservationId(reservation.getId());
        assertThat(reservationHistories).isNotEmpty();
        ReservationHistoryEntity reservationHistoryEntity = reservationHistories.get(0);
        assertThat(reservationHistoryEntity.getId()).isNotNull();
        assertThat(reservationHistoryEntity.getStatus()).isNotNull();
        assertThat(reservationHistoryEntity.getDepositFee()).isNull();
        assertThat(reservationHistoryEntity.getRefund()).isNull();
        assertThat(reservationHistoryEntity.getNumberOfPeople()).isNotNull();
        assertThat(reservationHistoryEntity.getPersonName()).isNotNull();
        assertThat(reservationHistoryEntity.getTimeSlot()).isNotNull();
        assertThat(reservationHistoryEntity.getReservation()).isNotNull();
        assertThat(reservationHistoryEntity.getRestaurant()).isNotNull();
    }

    @Test
    public void test_that_not_book_a_reservation_if_is_already_booked(){
        LocalDateTime timeSlotDateTime = LocalDateTime.of(2018, 5, 18, 11, 30);
        RestaurantEntity restaurant = storeRestaurant();
        TimeSlotEntity timeSlot = storeTimeSlot(timeSlotDateTime, restaurant);
        ReservationRequest reservationRequest = anReservation("John Doe", timeSlot.getTime().getTime(), restaurant.getId());

        reservationService.bookReservation(reservationRequest);

        assertThatThrownBy(() -> reservationService.bookReservation(reservationRequest))
                .hasMessageContaining("This reservation is already booked in the slot time.");
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

    private TimeSlotEntity storeTimeSlot(LocalDateTime localDateTime, RestaurantEntity restaurantEntity) {
        Timestamp timeSlot = Timestamp.valueOf(localDateTime);
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
