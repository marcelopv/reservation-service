package com.carecru.reservationservice.controllers;

import com.carecru.reservationservice.data.request.CustomerShowUpRequest;
import com.carecru.reservationservice.data.request.RescheduleRequest;
import com.carecru.reservationservice.data.request.ReservationCancellationRequest;
import com.carecru.reservationservice.data.request.ReservationRequest;
import com.carecru.reservationservice.data.response.CustomerShowUpResponse;
import com.carecru.reservationservice.data.response.RescheduleResponse;
import com.carecru.reservationservice.data.response.ReservationCancellationResponse;
import com.carecru.reservationservice.data.response.ReservationResponse;
import com.carecru.reservationservice.services.ReservationBookService;
import com.carecru.reservationservice.services.ReservationCancelService;
import com.carecru.reservationservice.services.ReservationRescheduleService;
import com.carecru.reservationservice.services.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

@RestController
@RequestMapping(path = "/api/reservations")
public class ReservationController {

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private ReservationBookService reservationBookService;

    @Autowired
    private ReservationCancelService reservationCancelService;

    @Autowired
    private ReservationRescheduleService reservationRescheduleService;

    @GetMapping(path = "/{reservationId}")
    public ResponseEntity getReservation(@PathVariable Long reservationId){
        ReservationResponse reservation = reservationService.getReservation(reservationId);
        return ResponseEntity.ok(reservation);
    }

    @PostMapping(consumes = APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity bookReservation(@RequestBody ReservationRequest reservationRequest){
        ReservationResponse reservationResponse = reservationBookService.bookReservation(reservationRequest);
        return ResponseEntity.created(URI.create("/api/reservations/"+reservationResponse.getId())).build();
    }

    @DeleteMapping(consumes = APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity cancelReservation(@RequestBody ReservationCancellationRequest cancellationRequest){
        ReservationCancellationResponse cancellationResponse = reservationCancelService.cancelReservation(cancellationRequest);
        return ResponseEntity.ok(cancellationResponse);
    }

    @PutMapping(consumes = APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity rescheduleReservation(@RequestBody RescheduleRequest rescheduleRequest){
        RescheduleResponse rescheduleResponse = reservationRescheduleService.rescheduleReservation(rescheduleRequest);
        return ResponseEntity.ok(rescheduleResponse);
    }

    @PostMapping(path = "/customerShowUp")
    public ResponseEntity customerShowUp(@RequestBody CustomerShowUpRequest customerShowUpRequest){
        CustomerShowUpResponse customerShowUpResponse = reservationService.customerShowUp(customerShowUpRequest.getReservationId());
        return ResponseEntity.ok(customerShowUpResponse);
    }

    @GetMapping(path = "/restaurants/{restaurantId}")
    public ResponseEntity getReservationsOfRestaurantFromDate(@PathVariable("restaurantId") Long restaurantId, @RequestParam(value = "date", required = false) Long date){
        List<ReservationResponse> reservations = reservationService.getReservationsOfRestaurantAndDate(restaurantId, date);
        return ResponseEntity.ok(reservations);
    }

}
