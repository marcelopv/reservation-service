package com.carecru.reservationservice.services;

import com.carecru.reservationservice.ReservationServiceApplication;
import com.carecru.reservationservice.config.DatabaseTestConfig;
import com.carecru.reservationservice.data.request.AddRestaurantRequest;
import com.carecru.reservationservice.entities.RestaurantEntity;
import com.carecru.reservationservice.repositories.RestaurantRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {ReservationServiceApplication.class, DatabaseTestConfig.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD) //this is added in order to each time has its lifecycle
public class RestaurantServiceIntegrationTest {

    @Autowired
    private RestaurantService restaurantService;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Test
    public void test_that_adds_a_restaurant(){
        AddRestaurantRequest addRestaurantRequest = AddRestaurantRequest.builder().name("Pizza Restaurant").build();

        restaurantService.addRestaurant(addRestaurantRequest);

        List<RestaurantEntity> restaurants = restaurantRepository.findAll();
        assertThat(restaurants.size()).isEqualTo(1);
    }

    @Test
    public void test_that_not_adds_a_restaurant_if_it_already_exists(){
        AddRestaurantRequest addRestaurantRequest = AddRestaurantRequest.builder().name("Pizza Restaurant").build();

        restaurantService.addRestaurant(addRestaurantRequest);

        assertThatThrownBy(() -> restaurantService.addRestaurant(addRestaurantRequest))
                .hasMessageContaining("Could not add restaurant since it already exists.");

        List<RestaurantEntity> restaurants = restaurantRepository.findAll();
        assertThat(restaurants.size()).isEqualTo(1);
    }

}
