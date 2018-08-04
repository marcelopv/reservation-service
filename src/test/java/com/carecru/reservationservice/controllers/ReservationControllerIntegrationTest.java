package com.carecru.reservationservice.controllers;

import com.carecru.reservationservice.ReservationServiceApplication;
import com.carecru.reservationservice.config.DatabaseTestConfig;
import com.carecru.reservationservice.data.request.*;
import com.carecru.reservationservice.data.response.RescheduleResponse;
import com.carecru.reservationservice.data.response.ReservationCancellationResponse;
import com.carecru.reservationservice.data.response.ReservationResponse;
import com.carecru.reservationservice.data.response.RestaurantResponse;
import com.carecru.reservationservice.utils.JSONUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.tomcat.jni.Local;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import static com.carecru.reservationservice.utils.JSONUtils.toJson;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {ReservationServiceApplication.class, DatabaseTestConfig.class})
@AutoConfigureMockMvc
@Transactional
public class ReservationControllerIntegrationTest {

    private static final String API_RESERVATIONS_URL = "/api/reservations";
    private static final String API_CUSTOMER_SHOW_UP_URL = "/api/reservations/customerShowUp";
    private static final String API_TIME_SLOTS_URL = "/api/timeSlots";
    private static final String API_RESTAURANTS_URL = "/api/restaurants";

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void test_that_books_a_table_successfully() throws Exception {
        String personName = "John Doe";
        long timeSlot = anReservationTime(11, 30);
        RestaurantResponse restaurantResponse = addRestaurantAndGetResponse("Mc Donalds");
        addTimeSlot(timeSlot, restaurantResponse.getId());
        ReservationRequest reservationRequest = ReservationRequest.builder()
                .numberOfPeople(2)
                .personName(personName)
                .timeSlot(timeSlot)
                .restaurantId(restaurantResponse.getId())
                .build();

        ReservationResponse reservationResponse = bookReservationAndGetResponse(reservationRequest);
        assertThat(reservationResponse.getPersonName()).isEqualTo(personName);
    }

    @Test
    public void test_that_cancels_a_reservation_successfully() throws Exception {
        String personName = "Maria";
        long timeSlot = anReservationTime(2, 0);
        RestaurantResponse restaurantResponse = addRestaurantAndGetResponse("Mc Donalds");
        addTimeSlot(timeSlot, restaurantResponse.getId());
        ReservationRequest reservationRequest = ReservationRequest.builder()
                .numberOfPeople(2)
                .personName(personName)
                .timeSlot(timeSlot)
                .restaurantId(restaurantResponse.getId())
                .build();
        bookReservationAndGetResponse(reservationRequest);

        ReservationCancellationRequest cancellationRequest = ReservationCancellationRequest.builder()
                .restaurantId(restaurantResponse.getId())
                .personName(personName)
                .reservationTime(timeSlot)
                .build();

        String cancellationPayload = toJson(cancellationRequest);

        this.mockMvc.perform(delete(API_RESERVATIONS_URL)
                .content(cancellationPayload).contentType(APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());
    }

    @Test
    public void test_that_reschedules_a_reservation_successfully() throws Exception {
        String personName = "Edward";
        long oldReservationTime = anReservationTime(LocalDateTime.now().plusHours(1));
        long newReservationTime = anReservationTime(LocalDateTime.now());
        RestaurantResponse restaurantResponse = addRestaurantAndGetResponse("Mc Donalds");
        addTimeSlot(oldReservationTime, restaurantResponse.getId());
        addTimeSlot(newReservationTime, restaurantResponse.getId());

        ReservationRequest reservationRequest = ReservationRequest.builder()
                .numberOfPeople(2)
                .personName(personName)
                .timeSlot(oldReservationTime)
                .restaurantId(restaurantResponse.getId())
                .build();

        bookReservationAndGetResponse(reservationRequest);

        RescheduleRequest rescheduleRequest = RescheduleRequest.builder()
                .personName(personName)
                .oldReservationTime(oldReservationTime)
                .newReservationTime(newReservationTime)
                .restaurantId(restaurantResponse.getId())
                .build();

        String reschedulePayload = toJson(rescheduleRequest);

        String rescheduleContent = this.mockMvc.perform(put(API_RESERVATIONS_URL)
                .content(reschedulePayload).contentType(APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        RescheduleResponse rescheduleResponse = objectMapper.readValue(rescheduleContent, RescheduleResponse.class);
        assertThat(rescheduleResponse.getOldReservation().getRefund()).isGreaterThan(0);
        assertThat(rescheduleResponse.getOldReservation().getDepositFee()).isGreaterThan(0);
    }

    @Test
    public void test_that_requests_reservation_as_customer_show_up() throws Exception {
        String personName = "Edward";
        long oldReservationTime = anReservationTime(LocalDateTime.now().plusHours(1));
        RestaurantResponse restaurantResponse = addRestaurantAndGetResponse("Mc Donalds");
        addTimeSlot(oldReservationTime, restaurantResponse.getId());

        ReservationRequest reservationRequest = ReservationRequest.builder()
                .numberOfPeople(2)
                .personName(personName)
                .timeSlot(oldReservationTime)
                .restaurantId(restaurantResponse.getId())
                .build();

        ReservationResponse reservationResponse = bookReservationAndGetResponse(reservationRequest);

        CustomerShowUpRequest customerShowUpRequest = CustomerShowUpRequest.builder().reservationId(reservationResponse.getId()).build();

        String customerShowUpPayload = toJson(customerShowUpRequest);

        this.mockMvc.perform(post(API_CUSTOMER_SHOW_UP_URL)
                .content(customerShowUpPayload).contentType(APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());
    }

    @Test
    @Transactional
    public void test_that_returns_free_time_slots_of_a_restaurant_successfully() throws Exception {
        RestaurantResponse restaurantId1 = addRestaurantAndGetResponse("Greek restaurant");
        RestaurantResponse restaurantId2 = addRestaurantAndGetResponse("Pizza restaurant");

        long timeSlot1 = anReservationTime(11, 30);
        long timeSlot2 = anReservationTime(12, 30);
        long timeSlot3 = anReservationTime(13, 30);
        addTimeSlot(timeSlot1, restaurantId1.getId());
        addTimeSlot(timeSlot2, restaurantId1.getId());
        addTimeSlot(timeSlot3, restaurantId2.getId());

        ReservationRequest reservationRequest3 = ReservationRequest.builder()
                .numberOfPeople(2)
                .personName("John Doe")
                .timeSlot(timeSlot3)
                .restaurantId(restaurantId2.getId())
                .build();
        bookReservationAndGetResponse(reservationRequest3);

        MockHttpServletResponse response = this.mockMvc.perform(get("/api/restaurants/"+restaurantId1.getId()+"/freeTimeSlots")
                .contentType(APPLICATION_JSON_UTF8)).andReturn().getResponse();

        ObjectMapper objectMapper = new ObjectMapper();
        List timeSlots = objectMapper.readValue(response.getContentAsString(), List.class);
        assertThat(timeSlots.contains(timeSlot1)).isTrue();
        assertThat(timeSlots.contains(timeSlot2)).isTrue();
        assertThat(timeSlots.contains(timeSlot3)).isFalse();
    }

    private long anReservationTime(int hour, int minute) {
        LocalDateTime reservationDateTime = LocalDateTime.of(2018, 5, 20, hour, minute);
        return Timestamp.valueOf(reservationDateTime).getTime();
    }

    private long anReservationTime(LocalDateTime reservationDateTime) {
        return Timestamp.valueOf(reservationDateTime).getTime();
    }

    private void addTimeSlot(long timeSlot, long restaurantId) throws Exception {
        AddTimeSlotRequest addTimeSlotRequest = AddTimeSlotRequest.builder().restaurantId(restaurantId).timeSlot(timeSlot).build();
        String addTimeSlotPayload = toJson(addTimeSlotRequest);

        this.mockMvc.perform(post(API_TIME_SLOTS_URL)
                .content(addTimeSlotPayload).contentType(APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());
    }

    public RestaurantResponse addRestaurantAndGetResponse(String restaurantName) throws Exception {
        AddRestaurantRequest addRestaurantRequest = AddRestaurantRequest.builder().name(restaurantName).build();

        String addRestaurantPayload = toJson(addRestaurantRequest);

        MockHttpServletResponse mvcResult = this.mockMvc.perform(post(API_RESTAURANTS_URL)
                .content(addRestaurantPayload).contentType(APPLICATION_JSON_UTF8))
                .andReturn().getResponse();

        String restaurantUrl = mvcResult.getHeader("Location");

        String contentAsString = this.mockMvc.perform(get(restaurantUrl)).andReturn().getResponse().getContentAsString();
        return objectMapper.readValue(contentAsString, RestaurantResponse.class);
    }

    public ReservationResponse bookReservationAndGetResponse(ReservationRequest reservationRequest) throws Exception {
        String reservationPayload = toJson(reservationRequest);

        MockHttpServletResponse mvcResult = this.mockMvc.perform(post(API_RESERVATIONS_URL)
                .content(reservationPayload).contentType(APPLICATION_JSON_UTF8))
                .andExpect(status().isCreated())
                .andReturn().getResponse();

        String reservationUrl = mvcResult.getHeader("Location");

        String contentAsString = this.mockMvc.perform(get(reservationUrl)).andReturn().getResponse().getContentAsString();
        return objectMapper.readValue(contentAsString, ReservationResponse.class);
    }

}
