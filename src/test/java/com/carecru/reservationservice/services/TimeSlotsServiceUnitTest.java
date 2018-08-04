package com.carecru.reservationservice.services;

import com.carecru.reservationservice.data.request.AddTimeSlotRequest;
import com.carecru.reservationservice.entities.RestaurantEntity;
import com.carecru.reservationservice.entities.TimeSlotEntity;
import com.carecru.reservationservice.repositories.RestaurantRepository;
import com.carecru.reservationservice.repositories.TimeSlotRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class TimeSlotsServiceUnitTest {

    @Mock
    private RestaurantRepository restaurantRepository;

    @Mock
    private TimeSlotRepository timeSlotRepository;

    @InjectMocks
    private TimeSlotsService timeSlotsService;

    @Test
    public void test_that_not_adds_time_slot_if_it_already_exists(){
        Timestamp timestamp = Timestamp.valueOf(LocalDateTime.now());
        long timeSlot = timestamp.getTime();
        long restaurantId = 1L;
        TimeSlotEntity timeSlotEntity = TimeSlotEntity.builder().time(timestamp).build();
        AddTimeSlotRequest addTimeSlotRequest = AddTimeSlotRequest.builder().timeSlot(timeSlot).restaurantId(restaurantId).build();
        RestaurantEntity restaurant = RestaurantEntity.builder().id(restaurantId).name("restaurant").timeSlot(timeSlotEntity).build();
        Optional<RestaurantEntity> optionalRestaurant = Optional.of(restaurant);
        when(restaurantRepository.findById(restaurantId)).thenReturn(optionalRestaurant);

        assertThatThrownBy(() -> timeSlotsService.addTimeSlot(addTimeSlotRequest))
                .hasMessageContaining("Could not add timeslot since the restaurant already has it");

        verify(timeSlotRepository, never()).save(any());
    }
}