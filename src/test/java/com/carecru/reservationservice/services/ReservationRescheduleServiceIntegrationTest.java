package com.carecru.reservationservice.services;

import com.carecru.reservationservice.ReservationServiceApplication;
import com.carecru.reservationservice.config.DatabaseTestConfig;
import com.carecru.reservationservice.data.ReservationStatus;
import com.carecru.reservationservice.data.request.RescheduleRequest;
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
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {ReservationServiceApplication.class, DatabaseTestConfig.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD) //this is added in order to each time has its lifecycle
@Transactional
public class ReservationRescheduleServiceIntegrationTest {

    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm");

    @Autowired
    private ReservationRescheduleService reservationRescheduleService;

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
    public void test_that_when_reschedules_a_reservation_marks_old_reservation_as_cancelled_and_creates_new_reservation(){
        String personName = "Christian";
        LocalDateTime oldDateTime = LocalDateTime.now();
        LocalDateTime newDateTime = oldDateTime.plusHours(1);
        RestaurantEntity restaurant = storeRestaurant();
        TimeSlotEntity oldTimeSlot = storeTimeSlot(oldDateTime, restaurant);
        TimeSlotEntity newTimeSlot = storeTimeSlot(newDateTime, restaurant);
        ReservationResponse reservation = storeReservation(personName, oldTimeSlot.getTime().getTime(), restaurant.getId());
        RescheduleRequest rescheduleRequest = anRescheduleRequest(personName, oldTimeSlot.getTime().getTime(), newTimeSlot.getTime().getTime(), restaurant.getId());

        reservationRescheduleService.rescheduleReservation(rescheduleRequest);

        ReservationEntity reservationUpdated = reservationRepository.getOne(reservation.getId());
        assertThat(reservationUpdated.getStatus()).isEqualTo(ReservationStatus.CANCELLED_BETWEEN_01_59_HOUR_AND_1_MINUTE_IN_ADVANCE);
        List<ReservationEntity> newReservationScheduled = reservationRepository.findByPersonNameAndTimeSlot(personName, newTimeSlot);
        assertThat(newReservationScheduled).isNotEmpty();
        assertThat(newReservationScheduled.get(0).getTimeSlot().getTime()).isEqualTo(newTimeSlot.getTime());
    }

    private RestaurantEntity storeRestaurant() {
        return restaurantRepository.save(RestaurantEntity.builder().name("Pizza rest").build());
    }

    private TimeSlotEntity storeTimeSlot(LocalDateTime localDateTime, RestaurantEntity restaurantEntity) {
        Timestamp timeSlot = Timestamp.valueOf(localDateTime);
        TimeSlotEntity timeSlotEntity = TimeSlotEntity.builder().time(timeSlot).restaurant(restaurantEntity).build();
        return timeSlotRepository.save(timeSlotEntity);
    }

    private RescheduleRequest anRescheduleRequest(String personName, Long timeSlot, Long newReservationTime, Long restaurantId) {
        return RescheduleRequest.builder()
                .personName(personName)
                .oldReservationTime(timeSlot)
                .newReservationTime(newReservationTime)
                .restaurantId(restaurantId)
                .build();
    }

    private ReservationResponse storeReservation(String personName, Long timeSlot, Long restaurantId) {
        ReservationRequest reservationRequest = anReservation(personName, timeSlot, restaurantId);
        return reservationBookService.bookReservation(reservationRequest);
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
