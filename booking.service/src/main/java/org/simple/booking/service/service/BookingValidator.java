package org.simple.booking.service.service;

import org.simple.booking.service.domain.Booking;
import org.simple.booking.service.domain.exception.BookingBadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import static org.simple.booking.service.domain.Constants.BOOKING_ON_WEEKEND;
import static org.simple.booking.service.domain.Constants.DOT;
import static org.simple.booking.service.domain.Constants.END;
import static org.simple.booking.service.domain.Constants.HOURS;
import static org.simple.booking.service.domain.Constants.INVALID_TIMESLOT_LENGTH;
import static org.simple.booking.service.domain.Constants.MAXIMUM_BOOKABLE_TIMESLOT;
import static org.simple.booking.service.domain.Constants.MINIMUM_BOOKABLE_TIMESLOT;
import static org.simple.booking.service.domain.Constants.MINUTES;
import static org.simple.booking.service.domain.Constants.BOOKING_BEYOND_THE_BOOKABLE_TIME_FRAME;
import static org.simple.booking.service.domain.Constants.BOOKING_BEYOND_THE_WORKING_HOURS;
import static org.simple.booking.service.domain.Constants.INVALID_TIMESLOT_START;
import static org.simple.booking.service.domain.Constants.OVERLAPPING_BOOKING;
import static org.simple.booking.service.domain.Constants.START;

@Service
public class BookingValidator {

    @Value("${bookable.frame.start:#{T(java.time.LocalDate).now()}}")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate timeFrameStart;
    @Value("${bookable.frame.end:#{T(java.time.LocalDate).now().plusDays(7)}}")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate timeFrameEnd;
    @Value("${bookable.workday.start:09:00}")
    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime workdayStart;
    @Value("${bookable.workday.end:17:00}")
    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime workdayEnd;
    @Value("${bookable.time-slot.duration.min-minutes:30}")
    private int timeSlotMinDuration;
    @Value("${bookable.time-slot.duration.max-minutes:180}")
    private int timeSlotMaxDuration;

    @Autowired
    private TimeSlotService timeSlotService;

    public void validateBooking(final Booking booking) {
        final long duration = getBookingDuration(booking);
        checkIfBeyondWorkingHours(booking);
        checkIfBeyondWorkingDays(booking);
        checkIfBeyondTheBookableTimeFrame(booking);
        checkBookingStart(booking.getStart().getMinute(), booking.getStart().getSecond());
        checkMinLength(duration);
        checkMaxLength(duration);
        checkLength(duration);
        checkOverLap(booking);
    }

    private void checkIfBeyondWorkingDays(final Booking booking) {
        if (booking.getStart().getDayOfWeek() == DayOfWeek.SATURDAY || booking.getStart().getDayOfWeek() == DayOfWeek.SUNDAY) {
            throw new BookingBadRequestException(BOOKING_ON_WEEKEND);
        }
    }

    private void checkMinLength(final long duration) {
        if (duration < timeSlotMinDuration) {
            throw new BookingBadRequestException(MINIMUM_BOOKABLE_TIMESLOT + timeSlotMinDuration + MINUTES + DOT);
        }
    }

    private void checkMaxLength(final long duration) {
        if (duration > timeSlotMaxDuration) {
            throw new BookingBadRequestException(MAXIMUM_BOOKABLE_TIMESLOT + timeSlotMaxDuration / 60 + HOURS + DOT);
        }
    }

    private void checkLength(final long duration) {
        if (duration % timeSlotMinDuration != 0) {
            throw new BookingBadRequestException(INVALID_TIMESLOT_LENGTH + timeSlotMinDuration + MINUTES + DOT);
        }
    }

    private void checkIfBeyondTheBookableTimeFrame(final Booking booking) {
        if (booking.getStart().toLocalDate().isBefore(timeFrameStart) ||
                booking.getEnd().toLocalDate().isAfter(timeFrameEnd)) {
            throw new BookingBadRequestException(BOOKING_BEYOND_THE_BOOKABLE_TIME_FRAME + START + timeFrameStart + END + timeFrameEnd + DOT);
        }
    }

    private void checkIfBeyondWorkingHours(final Booking booking) {
        if (booking.getStart().toLocalTime().isBefore(workdayStart) ||
                booking.getEnd().toLocalTime().isAfter(workdayEnd)) {
            throw new BookingBadRequestException(BOOKING_BEYOND_THE_WORKING_HOURS + START + workdayStart + END + workdayEnd + DOT);
        }
    }

    private void checkBookingStart(final int minute, final int second) {
        if ((minute != 0 || second != 0) && (minute != 30 || second != 0)) {
            throw new BookingBadRequestException(INVALID_TIMESLOT_START);
        }
    }

    private void checkOverLap(final Booking booking) {
        final Optional<Booking> overlappingBooking = getOverlappingBooking(booking);
        if (overlappingBooking.isPresent()) {
            throw new BookingBadRequestException(OVERLAPPING_BOOKING + overlappingBooking.get().getStart() + END + overlappingBooking.get().getEnd());
        }
    }

    private Optional<Booking> getOverlappingBooking(final Booking booking) {
        return timeSlotService.getTimeSlotStore()
                              .stream()
                              .filter(b -> isOverlap(booking, b))
                              .findFirst();
    }

    private boolean isOverlap(final Booking bookedTimeSlot, final Booking slot) {
        return bookedTimeSlot.getStart().isBefore(slot.getEnd()) && slot.getStart().isBefore(bookedTimeSlot.getEnd());
    }

    private long getBookingDuration(final Booking booking) {
        return Duration.between(booking.getStart(), booking.getEnd()).toMinutes();
    }

}
