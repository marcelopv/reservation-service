package com.carecru.reservationservice.services;

import com.carecru.reservationservice.data.request.AddTimeSlotRequest;
import com.carecru.reservationservice.entities.RestaurantEntity;
import com.carecru.reservationservice.entities.TimeSlotEntity;
import com.carecru.reservationservice.repositories.RestaurantRepository;
import com.carecru.reservationservice.repositories.TimeSlotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TimeSlotsService {

    @Autowired
    private TimeSlotRepository timeSlotRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Transactional
    public void addTimeSlot(AddTimeSlotRequest addTimeSlotRequest){
        Timestamp timeSlotAsTimeStamp = Timestamp.from(Instant.ofEpochMilli(addTimeSlotRequest.getTimeSlot()));
        Optional<RestaurantEntity> optionalRestaurant = restaurantRepository.findById(addTimeSlotRequest.getRestaurantId());

        if (!optionalRestaurant.isPresent()) {
            throw new RuntimeException("Could not find restaurant with id "+addTimeSlotRequest.getRestaurantId());
        }
        RestaurantEntity restaurantEntity = optionalRestaurant.get();

        List<TimeSlotEntity> restaurantTimeSlots = restaurantEntity.getTimeSlots();
        boolean restaurantHasTimeSlot = restaurantTimeSlots.stream().anyMatch(p -> p.getTime().equals(timeSlotAsTimeStamp));

        if (restaurantHasTimeSlot) {
            throw new RuntimeException("Could not add timeslot since the restaurant already has it.");
        }

        TimeSlotEntity timeSlot = TimeSlotEntity.builder().time(timeSlotAsTimeStamp).restaurant(restaurantEntity).build();
        timeSlotRepository.save(timeSlot);
    }

    public List<Long> getFreeTimeSlots(Long restaurantId) {
        List<TimeSlotEntity> freeTimeSlots = timeSlotRepository.getFreeTimeSlots(restaurantId);

        return freeTimeSlots.stream()
                .map(TimeSlotEntity::getTime)
                .map(Timestamp::getTime)
                .collect(Collectors.toList());
    }

}
