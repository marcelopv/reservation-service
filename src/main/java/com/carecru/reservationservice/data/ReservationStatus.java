package com.carecru.reservationservice.data;

public interface ReservationStatus {

    int IN_PROGRESS = 1;
    int DONE_CUSTOMER_SHOW_UP = 2;
    int CANCELLED_24_HOURS_IN_ADVANCE = 3;
    int CANCELLED_BETWEEN_23_59_HOUR_AND_12_HOUR_IN_ADVANCE = 4;
    int CANCELLED_BETWEEN_11_59_HOUR_AND_2_HOUR_IN_ADVANCE = 5;
    int CANCELLED_BETWEEN_01_59_HOUR_AND_1_MINUTE_IN_ADVANCE = 6;
    int DONE_CUSTOMER_NOT_SHOW_UP = 7;

}
