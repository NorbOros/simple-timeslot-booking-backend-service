package org.simple.booking.service;

import org.junit.jupiter.api.Test;
import org.simple.booking.service.domain.Booking;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BookingServiceIT extends AbstractBookingServiceIT {

    protected static final String BOOK_URL = "/book";
    protected static final String JOHN_DOE = "John Doe";
    protected static final LocalDateTime START_03_03_10_10_00 = LocalDateTime.of(2023, 3, 10, 10, 0, 0);
    protected static final LocalDateTime START_03_03_10_09_30 = LocalDateTime.of(2023, 3, 10, 9, 30, 0);
    protected static final LocalDateTime START_03_03_10_05_00 = LocalDateTime.of(2023, 3, 10, 10, 5, 0);
    protected static final LocalDateTime END_03_03_10_07_00 = LocalDateTime.of(2023, 3, 10, 10, 7, 0);
    protected static final LocalDateTime END_03_03_10_11_00 = LocalDateTime.of(2023, 3, 10, 11, 0, 0);
    protected static final LocalDateTime END_03_03_10_10_30 = LocalDateTime.of(2023, 3, 10, 10, 30, 0);
    protected static final LocalDateTime END_03_03_10_15_00 = LocalDateTime.of(2023, 3, 10, 15, 0, 0);
    protected static final LocalDateTime END_03_0310_11_37 = LocalDateTime.of(2023, 3, 10, 11, 37, 0);
    protected static final LocalDateTime END_03_03_16_11_30 = LocalDateTime.of(2023, 3, 16, 11, 30, 0);


    @Test
    public void invalidRequestTooLargeTimeSlot() {
        final Booking bookingTooLarge = getBooking(JOHN_DOE, START_03_03_10_10_00, END_03_03_10_15_00);
        final ResponseEntity<String> response = restTemplate.postForEntity(getURL() + BOOK_URL, bookingTooLarge, String.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid booking, the maximum bookable timeslot: 3 hours.", response.getBody());
    }

    @Test
    public void invalidRequestTooSmallTimeSlot() {
        final Booking tooSmallBooking = getBooking(JOHN_DOE, START_03_03_10_10_00, END_03_03_10_07_00);
        final ResponseEntity<String> response = restTemplate.postForEntity(getURL() + BOOK_URL, tooSmallBooking, String.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid booking, the minimum bookable timeslot: 30 minutes.", response.getBody());
    }

    @Test
    public void invalidRequestInvalidLength() {
        final Booking invalidLength = getBooking(JOHN_DOE, START_03_03_10_10_00, END_03_0310_11_37);
        final ResponseEntity<String> response = restTemplate.postForEntity(getURL() + BOOK_URL, invalidLength, String.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid booking, the timeslot length can only be integer multiples of the minimum bookable timeslot: 30 minutes.", response.getBody());
    }

    @Test
    public void invalidRequestBeyondTimeFrame() {
        final Booking beyondTheTimeFrame = getBooking(JOHN_DOE, START_03_03_10_10_00, END_03_03_16_11_30);
        final ResponseEntity<String> response = restTemplate.postForEntity(getURL() + BOOK_URL, beyondTheTimeFrame, String.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid booking, the passed booking beyond the bookable timeframe, start: 2023-03-10 end: 2023-03-13.", response.getBody());
    }

    @Test
    public void invalidRequestWrongStart() {
        final Booking bookingInvalidStart = getBooking(JOHN_DOE, START_03_03_10_05_00, END_03_03_10_11_00);
        final ResponseEntity<String> response = restTemplate.postForEntity(getURL() + BOOK_URL, bookingInvalidStart, String.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid booking, booked timeslot can only start from :00 or :30.", response.getBody());
    }

    @Test
    public void overlappingTimeSlots() {
        final Booking booking = getBooking(JOHN_DOE, START_03_03_10_10_00, END_03_03_10_11_00);
        final Booking overlappingBooking = getBooking(JOHN_DOE, START_03_03_10_09_30, END_03_03_10_10_30);
        final ResponseEntity<Booking> response = restTemplate.postForEntity(getURL() + BOOK_URL, booking, Booking.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        verifyBooking(booking, response.getBody());

        final ResponseEntity<String> overlappingResponse = restTemplate.postForEntity(getURL() + BOOK_URL, overlappingBooking, String.class);
        assertEquals(HttpStatus.BAD_REQUEST, overlappingResponse.getStatusCode());
        assertEquals("Invalid request, the passed timeslot overlaps with an existing one: 2023-03-10T10:00 end: 2023-03-10T11:00", overlappingResponse.getBody());
    }

    private void verifyBooking(final Booking expected, final Booking result) {
        assertEquals(expected.getStart(), result.getStart());
        assertEquals(expected.getEnd(), result.getEnd());
        assertEquals(expected.getClient(), result.getClient());
    }

    private Booking getBooking(final String client, final LocalDateTime start, final LocalDateTime end) {
        return Booking.builder()
                      .client(client)
                      .start(start)
                      .end(end)
                      .build();
    }
}
