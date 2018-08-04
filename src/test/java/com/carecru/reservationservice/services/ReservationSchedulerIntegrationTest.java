package com.carecru.reservationservice.services;

import com.carecru.reservationservice.ReservationServiceApplication;
import com.carecru.reservationservice.config.DatabaseTestConfig;
import com.carecru.reservationservice.data.ReservationStatus;
import com.carecru.reservationservice.entities.ReservationEntity;
import com.carecru.reservationservice.entities.ReservationHistoryEntity;
import com.carecru.reservationservice.entities.TimeSlotEntity;
import com.carecru.reservationservice.repositories.ReservationHistoryRepository;
import com.carecru.reservationservice.repositories.ReservationRepository;
import com.carecru.reservationservice.repositories.TimeSlotRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {ReservationServiceApplication.class, DatabaseTestConfig.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD) //this is added in order to each time has its lifecycle
public class ReservationSchedulerIntegrationTest {

    @Autowired
    private ReservationScheduler reservationScheduler;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private ReservationHistoryRepository reservationHistoryRepository;

    @Autowired
    private TimeSlotRepository timeSlotRepository;

    @Autowired
    private ReservationBookService reservationBookService;

    @Test
    @Transactional
    public void test_that_marks_all_in_progress_reservations_from_previous_day_as_customer_not_show_up(){
        ReservationEntity reservation1 = addReservation(ReservationStatus.IN_PROGRESS);
        storeReservationHistory(reservation1);

        reservationScheduler.markInProgressReservationsAsDone();

        ReservationEntity reservationDone1 = reservationRepository.getOne(reservation1.getId());
        assertThat(reservationDone1.getStatus()).isEqualTo(ReservationStatus.DONE_CUSTOMER_NOT_SHOW_UP);
        assertThat(reservationDone1.getRefund()).isEqualTo(0);
        assertThat(reservationDone1.getDepositFee()).isEqualTo(10);

        ReservationHistoryEntity reservationHistory = reservationHistoryRepository.findByReservationId(reservation1.getId()).get(0);

        assertThat(reservationHistory.getStatus()).isEqualTo(ReservationStatus.DONE_CUSTOMER_NOT_SHOW_UP);
        assertThat(reservationHistory.getRefund()).isEqualTo(0);
        assertThat(reservationHistory.getDepositFee()).isEqualTo(10);
    }

    private ReservationEntity addReservation(int status) {
        Timestamp time = Timestamp.valueOf(LocalDateTime.now());
        TimeSlotEntity timeSlotEntity = TimeSlotEntity.builder().time(time).build();
        timeSlotRepository.save(timeSlotEntity);

        ReservationEntity reservation = ReservationEntity.builder()
                .status(status)
                .personName("Enrique")
                .numberOfPeople(1)
                .timeSlot(timeSlotEntity)
                .build();

        return reservationRepository.save(reservation);
    }

    private void storeReservationHistory(ReservationEntity reservation) {
        reservationHistoryRepository.save(ReservationHistoryEntity.builder()
                .reservation(reservation)
                .timeSlot(reservation.getTimeSlot())
                .restaurant(reservation.getRestaurant())
                .status(reservation.getStatus())
                .personName(reservation.getPersonName())
                .numberOfPeople(reservation.getNumberOfPeople())
                .build());
    }

}