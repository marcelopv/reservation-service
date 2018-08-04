package com.carecru.reservationservice.controllers;

import com.carecru.reservationservice.data.request.AddTimeSlotRequest;
import com.carecru.reservationservice.repositories.TimeSlotRepository;
import com.carecru.reservationservice.services.TimeSlotsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/api/timeSlots")
public class TimeSlotsController {

    @Autowired
    private TimeSlotRepository timeSlotRepository;

    @Autowired
    private TimeSlotsService timeSlotsService;

    @PostMapping
    public ResponseEntity addTimeSlot(@RequestBody AddTimeSlotRequest addTimeSlotRequest){
        timeSlotsService.addTimeSlot(addTimeSlotRequest);
        return ResponseEntity.ok().build();
    }
}
