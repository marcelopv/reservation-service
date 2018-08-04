package com.carecru.reservationservice.config;

import com.carecru.reservationservice.services.rules.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;

@Configuration
public class ApplicationConfig {

    @Bean
    public List<FinishReservationRule> finishReservationRules(){
        return Arrays.asList(
                new Cancelled24HoursInAdvance(),
                new CancelledBetween24Hours(),
                new CancelledBetween12Hours(),
                new CancelledBetween2Hours());
    }

}
