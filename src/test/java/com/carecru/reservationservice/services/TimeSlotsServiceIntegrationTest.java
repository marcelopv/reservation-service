package com.carecru.reservationservice.services;

import com.carecru.reservationservice.ReservationServiceApplication;
import com.carecru.reservationservice.config.DatabaseTestConfig;
import com.carecru.reservationservice.data.ReservationStatus;
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
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {ReservationServiceApplication.class, DatabaseTestConfig.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD) //this is added in order to each test has its lifecycle
public class TimeSlotsServiceIntegrationTest {

    @Autowired
    private TimeSlotRepository timeSlotRepository;

    @Autowired
    private TimeSlotsService timeSlotsService;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Test
    @Transactional
    public void test_that_not_consider_in_progress_reservations_as_free_time_slots(){
        RestaurantEntity restaurantEntity = storeRestaurant("restaurant");
        TimeSlotEntity notFreeTimeSlot = storeTimeSlot(10, 10, 30, restaurantEntity);
        TimeSlotEntity freeTimeSlot = storeTimeSlot(10, 11, 30, restaurantEntity);

        ReservationEntity reservation1 = ReservationEntity.builder()
                .status(ReservationStatus.IN_PROGRESS)
                .personName("person 1")
                .numberOfPeople(2)
                .timeSlot(notFreeTimeSlot)
                .build();
        reservationRepository.save(reservation1);

        ReservationEntity reservation2 = ReservationEntity.builder()
                .status(ReservationStatus.CANCELLED_BETWEEN_01_59_HOUR_AND_1_MINUTE_IN_ADVANCE)
                .personName("person 2")
                .numberOfPeople(3)
                .timeSlot(freeTimeSlot)
                .build();
        reservationRepository.save(reservation2);

        List<Long> freeTimeSlots = timeSlotsService.getFreeTimeSlots(restaurantEntity.getId());
        assertThat(freeTimeSlots.contains(freeTimeSlot.getTime().getTime())).isTrue();
        assertThat(freeTimeSlots.contains(notFreeTimeSlot.getTime().getTime())).isFalse();
    }

    private RestaurantEntity storeRestaurant(String restaurantName) {
        RestaurantEntity restaurant = RestaurantEntity.builder().id(1L).name(restaurantName).build();
        return restaurantRepository.save(restaurant);
    }

    private TimeSlotEntity storeTimeSlot(int day, int hour, int minute, RestaurantEntity restaurantEntity) {
        LocalDateTime reservationDateTime = LocalDateTime.of(2018, 5, day, hour, minute);
        Timestamp timeSlot = Timestamp.valueOf(reservationDateTime);
        TimeSlotEntity timeSlotEntity = TimeSlotEntity.builder().time(timeSlot).restaurant(restaurantEntity).build();
        return timeSlotRepository.save(timeSlotEntity);
    }

}