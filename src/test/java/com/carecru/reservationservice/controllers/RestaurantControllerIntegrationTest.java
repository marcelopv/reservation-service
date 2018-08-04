package com.carecru.reservationservice.controllers;

import com.carecru.reservationservice.ReservationServiceApplication;
import com.carecru.reservationservice.config.DatabaseTestConfig;
import com.carecru.reservationservice.data.request.AddRestaurantRequest;
import com.carecru.reservationservice.utils.JSONUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {ReservationServiceApplication.class, DatabaseTestConfig.class})
@AutoConfigureMockMvc
public class RestaurantControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void test_that_adds_a_restaurant() throws Exception {
        AddRestaurantRequest addRestaurantRequest = AddRestaurantRequest.builder().name("restaurant 1").build();

        String addRestaurantPayload = JSONUtils.toJson(addRestaurantRequest);

        this.mockMvc.perform(post("/api/restaurants")
                .content(addRestaurantPayload).contentType(APPLICATION_JSON_UTF8))
                .andExpect(status().isCreated());
    }

}
