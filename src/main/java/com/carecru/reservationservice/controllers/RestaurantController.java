package com.carecru.reservationservice.controllers;

import com.carecru.reservationservice.data.request.AddRestaurantRequest;
import com.carecru.reservationservice.data.response.RestaurantResponse;
import com.carecru.reservationservice.services.RestaurantService;
import com.carecru.reservationservice.services.TimeSlotsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping(path = "/api/restaurants")
public class RestaurantController {

    @Autowired
    private RestaurantService restaurantService;

    @Autowired
    private TimeSlotsService timeSlotsService;

    @PostMapping
    public ResponseEntity addRestaurant(@RequestBody AddRestaurantRequest addRestaurantRequest){
        Long restaurantId = restaurantService.addRestaurant(addRestaurantRequest);
        return ResponseEntity.created(URI.create("/api/restaurants/"+restaurantId)).build();
    }

    @GetMapping(path = "/{restaurantId}")
    public ResponseEntity getRestaurant(@PathVariable Long restaurantId){
        RestaurantResponse restaurant = restaurantService.getRestaurant(restaurantId);
        return ResponseEntity.ok(restaurant);
    }

    @GetMapping(path = "/{restaurantId}/freeTimeSlots")
    public ResponseEntity<List<Long>> getFreeTimeSlots(@PathVariable Long restaurantId){
        List<Long> freeTimeSlots = timeSlotsService.getFreeTimeSlots(restaurantId);
        return ResponseEntity.ok(freeTimeSlots);
    }

}
