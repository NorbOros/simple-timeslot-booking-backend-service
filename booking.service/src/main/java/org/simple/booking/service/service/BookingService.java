package org.simple.booking.service.service;

import org.simple.booking.service.domain.Booking;
import org.simple.booking.service.domain.exception.BookingBadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.simple.booking.service.domain.Constants.NO_BOOKED_TIMESLOT_FOUND;

@Service
public class BookingService {

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

    @Autowired
    private TimeSlotService timeSlotService;
    @Autowired
    private BookingValidator bookingValidator;

    public Booking registerBooking(final Booking booking) {
        bookingValidator.validateBooking(booking);

        // The only purpose of this part is returning an uuid
        final Booking persistedBooking = getBookingToPersist(booking);
        timeSlotService.getTimeSlotStore()
                       .add(persistedBooking);

        return persistedBooking;
    }

    public Booking getTimeSlotByTime(final LocalDateTime specifiedTime) {
        return timeSlotService.getTimeSlotStore().stream()
                              .filter(booking -> filterTimeSlotByDateTime(specifiedTime, booking))
                              .findFirst()
                              .orElseThrow(() -> new BookingBadRequestException(NO_BOOKED_TIMESLOT_FOUND + specifiedTime));
    }

    public List<Booking> getBookingsByTimeFrame(final LocalDateTime start, final LocalDateTime end) {
        return timeSlotService.getTimeSlotStore().stream()
                              .filter(booking -> filterTimeSlotByTimeFrame(start, end, booking))
                              .collect(Collectors.toList());
    }

    public List<Booking> getBookingsByClient(final String client) {
        return timeSlotService.getTimeSlotStore()
                              .stream()
                              .filter(booking -> client.equals(booking.getClient()))
                              .collect(Collectors.toList());
    }

    public List<Booking> getBookedTimeSlots() {
        return new ArrayList(timeSlotService.getTimeSlotStore());
    }

    public List<Booking> getBookedTimeSlotsByTimeFrame(final LocalDateTime start, final LocalDateTime end) {
        return timeSlotService.getTimeSlotStore().stream()
                              .filter(booking -> filterTimeSlotByTimeFrame(start, end, booking))
                              .collect(Collectors.toList());
    }

    public List<Booking> getFreeTimeSlots() {
        return Stream.iterate(timeFrameStart, day -> day.plusDays(1))
                     .limit(ChronoUnit.DAYS.between(timeFrameStart, timeFrameEnd) + 1)
                     .filter(this::filterWeekdays)
                     .map(this::mapFreeTimeSlotsToBooking)
                     .flatMap(List::stream)
                     .collect(Collectors.toList());
    }

    public List<Booking> getFreeTimeSlotsByTimeFrame(final LocalDateTime start, final LocalDateTime end) { // 2023-03-10, 2023-03-15
        return Stream.iterate(timeFrameStart, day -> day.plusDays(1))
                     .limit(ChronoUnit.DAYS.between(timeFrameStart, timeFrameEnd) + 1)
                     .filter(this::filterWeekdays)
                     .map(this::mapFreeTimeSlotsToBooking)
                     .flatMap(List::stream)
                     .filter(booking -> filterTimeSlotByTimeFrame(start, end, booking))
                     .collect(Collectors.toList());
    }

    private boolean filterWeekdays(final LocalDate day) {
        return day.getDayOfWeek() != DayOfWeek.SATURDAY && day.getDayOfWeek() != DayOfWeek.SUNDAY;
    }

    private List<Booking> mapFreeTimeSlotsToBooking(final LocalDate day) {
        return getFreeTimeSlotsByDay(day.atTime(workdayStart), day.atTime(workdayEnd))
                .filter(this::filterOverlappingBooking)
                .collect(Collectors.toList());
    }

    private boolean filterOverlappingBooking(final Booking timeSlot) {
        return timeSlotService.getTimeSlotStore()
                              .stream()
                              .noneMatch(booking -> isOverlap(booking, timeSlot));
    }

    private Stream<Booking> getFreeTimeSlotsByDay(final LocalDateTime workDayStart, final LocalDateTime workDayEnd) {
        return Stream.iterate(workDayStart, time -> time.plusMinutes(timeSlotMinDuration))
                     .limit(workDayStart.until(workDayEnd, ChronoUnit.MINUTES) / timeSlotMinDuration)
                     .map(startTime -> mapFreeTimeSlots(workDayEnd, startTime));
    }

    private Booking mapFreeTimeSlots(final LocalDateTime workDayEnd, final LocalDateTime startTime) {
        LocalDateTime endTime = startTime.plusMinutes(timeSlotMinDuration);
        if (endTime.isAfter(workDayEnd)) endTime = workDayEnd;

        return buildEmptyTimeSlot(startTime, endTime);
    }

    private Booking buildEmptyTimeSlot(final LocalDateTime startTime, final LocalDateTime endTime) {
        return Booking.builder()
                      .start(startTime)
                      .end(LocalDateTime.from(endTime))
                      .build();
    }

    /**
     * The only purpose of this method is mock the
     * behaviour repository layer (Getting an entity Id)
     */
    private Booking getBookingToPersist(final Booking booking) {
        return Booking.builder()
                      .id(UUID.randomUUID().toString())
                      .start(booking.getStart())
                      .end(booking.getEnd())
                      .client(booking.getClient())
                      .build();
    }

    private boolean filterTimeSlotByDateTime(final LocalDateTime specifiedDateTime, final Booking booking) {
        return (booking.getStart().isBefore(specifiedDateTime) || booking.getStart().equals(specifiedDateTime))
                && booking.getEnd().isAfter(specifiedDateTime);
    }

    private boolean filterTimeSlotByTimeFrame(final LocalDateTime start, final LocalDateTime end, final Booking booking) {
        return (booking.getStart().isAfter(start) || booking.getStart().equals(start))
                && booking.getEnd().isBefore(end);
    }

    private boolean isOverlap(final Booking bookedTimeSlot, final Booking slot) {
        return bookedTimeSlot.getStart().isBefore(slot.getEnd()) && slot.getStart().isBefore(bookedTimeSlot.getEnd());
    }

}
