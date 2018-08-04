package com.carecru.reservationservice.services;

import com.carecru.reservationservice.data.request.CustomerShowUpRequest;
import com.carecru.reservationservice.data.request.ReservationRequest;
import com.carecru.reservationservice.data.ReservationStatus;
import com.carecru.reservationservice.entities.ReservationEntity;
import com.carecru.reservationservice.entities.RestaurantEntity;
import com.carecru.reservationservice.entities.TimeSlotEntity;
import com.carecru.reservationservice.repositories.ReservationHistoryRepository;
import com.carecru.reservationservice.repositories.ReservationRepository;
import com.carecru.reservationservice.repositories.TimeSlotRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ReservationServiceUnitTest {

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private ReservationHistoryRepository reservationHistoryRepository;

    @Mock
    private TimeSlotRepository timeSlotRepository;

    @InjectMocks
    private ReservationService reservationService;

    @Test
    public void test_that_marks_reservation_as_status_customer_show_up_when_is_requested(){
        LocalDateTime timeSlotDateTime = LocalDateTime.of(2018, 5, 18, 11, 30);
        RestaurantEntity restaurantEntity = RestaurantEntity.builder().id(1L).name("Greek restaurant").build();
        ReservationRequest reservationRequest = anReservationRequest("Joana", Timestamp.valueOf(timeSlotDateTime).getTime(), restaurantEntity.getId());
        ReservationEntity reservationEntity = anReservationEntity(reservationRequest, restaurantEntity);
        CustomerShowUpRequest customerShowUpRequest = anCustomerShowUpRequest(reservationEntity.getId());
        when(reservationRepository.findById(any())).thenReturn(Optional.of(reservationEntity));

        reservationService.customerShowUp(customerShowUpRequest.getReservationId());

        ArgumentCaptor<ReservationEntity> argumentCaptor = ArgumentCaptor.forClass(ReservationEntity.class);
        verify(reservationRepository).save(argumentCaptor.capture());
        assertThat(argumentCaptor.getValue().getStatus()).isEqualTo(ReservationStatus.DONE_CUSTOMER_SHOW_UP);
    }

    private CustomerShowUpRequest anCustomerShowUpRequest(Long id) {
        return CustomerShowUpRequest.builder().reservationId(id).build();
    }

    private ReservationEntity anReservationEntity(ReservationRequest reservationRequest, RestaurantEntity restaurantEntity) {
        Instant timeSlotMilli = Instant.ofEpochMilli(reservationRequest.getTimeSlot());
        Timestamp timeSlot = Timestamp.from(timeSlotMilli);

        TimeSlotEntity timeSlotEntity = TimeSlotEntity.builder()
                .id(1L)
                .time(timeSlot)
                .build();

        return ReservationEntity.builder()
                .id(1L)
                .numberOfPeople(reservationRequest.getNumberOfPeople())
                .personName(reservationRequest.getPersonName())
                .timeSlot(timeSlotEntity)
                .status(ReservationStatus.IN_PROGRESS)
                .restaurant(restaurantEntity)
                .build();
    }

    private ReservationRequest anReservationRequest(String personName, Long timeSlot, Long restaurantId) {
        return ReservationRequest.builder()
                .numberOfPeople(2)
                .personName(personName)
                .timeSlot(timeSlot)
                .restaurantId(restaurantId)
                .build();
    }

}