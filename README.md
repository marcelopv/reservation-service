Reservation statuses:
- In progress: It is the status of a booked reservation.
- Done customer show up: It is the status of a customer that show up for the reservation
- Done customer not show up: It is the status of a customer that not show up for the reservation;
- Cancelled 24 hours in advance: It is the status of a reservation cancellation 24 hours in advance.
- Cancelled between 23:59 hours and 12 hours in advance: It is the status of a reservation cancellation between 23:59 hours and
12 hours in advance.
- Cancelled between 11:59 hours and 2 hours in advance: It is the status of a reservation cancellation between 11:59 hours and
2 hours in advance.
- Cancelled between 01:59 hours and 1 minute in advance: It is the status of a reservation cancellation between 01:59 hours and
1 minute in advance.

Assumptions that I have made:
- Restaurants are added manually via http request.
- Time slots are related with a restaurant, therefore, they need to be added for each restaurant via http request.
- The time slots were agreed with Frontend team and are provided as milliseconds. The frontend team will be responsible for
displaying the time slots and the ones that are free for reservation by the restaurant.
- The host/hostess employed by the restaurant who receives customer is responsible for finishing the successful reservation.
- In order to finish reservations of the day for customers that didn't show up, I have implemented a scheduler that goes through
all in progress reservations and finishes one by one.

Database setup:
- The application expects a PostgreSQL database running with:
- database: "reservation",
- user: "reservation_user"
- password: "12345"

Testing:
I have used this website to generate timeslots in miliseconds: https://currentmillis.com/

Endpoints:
- Add a restaurant: /api/restaurants (POST)
- Add a time slot: /api/timeSlots (POST)
- Book a reservation: /api/reservations (POST)
- Retrieve a reservation: /api/reservations/{reservationId} (GET)
- Cancel a reservation: /api/reservations (DELETE)
- Reschedule a reservation: /api/reservations (PUT)
- Finish a reservation for customer that show up: /api/reservations/customerShowUp (POST)
- Get free time slots of a restaurant: /api/restaurants/{restaurantId}/freeTimeSlots (GET)
- Get past reservations of a restaurant: /api/reservations/restaurants/{restaurantId} (GET)
- Get past reservations in a specific date of a restaurant: /api/reservations/restaurants/{restaurantId}?date=date (GET)

Running:
To run the application, please run the command: ./gradlew bootRun