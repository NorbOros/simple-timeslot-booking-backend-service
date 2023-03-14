package org.simple.booking.service.controller;

import jakarta.validation.Valid;
import org.simple.booking.service.domain.Booking;
import org.simple.booking.service.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;

@Validated
@RestController
@RequestMapping("/v1")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @PostMapping("/book")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Booking> registerBooking(@RequestBody @Valid final Booking booking) {
        final Booking persistedBooking = bookingService.registerBooking(booking);
        return ResponseEntity.created(URI.create(persistedBooking.getId()))
                             .body(persistedBooking);
    }

    @GetMapping("/booked")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<Booking>> getAllBookedTimeSlots() {
        return ResponseEntity.ok(bookingService.getBookedTimeSlots());
    }

    @GetMapping("/booked/timeframe")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<Booking>> getBookedTimeSlotsByTimeFrame(@RequestParam("start")
                                                                       @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm") final LocalDateTime start,
                                                                       @RequestParam("end")
                                                                       @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm") final LocalDateTime end) {
        return ResponseEntity.ok(bookingService.getBookedTimeSlotsByTimeFrame(start, end));
    }

    @GetMapping("/free")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<Booking>> getAllFreeTimeSlots() {
        return ResponseEntity.ok(bookingService.getFreeTimeSlots());
    }

    @GetMapping("/free/timeframe")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<Booking>> getFreeTimeSlotsByTimeFrame(@RequestParam("start")
                                                                     @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm") final LocalDateTime start,
                                                                     @RequestParam("end")
                                                                     @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm") final LocalDateTime end) {
        return ResponseEntity.ok(bookingService.getFreeTimeSlotsByTimeFrame(start, end));
    }

    @GetMapping("/specific-time/{specifiedTime}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Booking> getTimeSlotByTime(@PathVariable("specifiedTime")
                                                     @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm") final LocalDateTime specifiedTime) {
        return ResponseEntity.ok(bookingService.getTimeSlotByTime(specifiedTime));
    }

    @GetMapping("/client/{client}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<Booking>> getAllByUser(@PathVariable("client") final String client) {
        return ResponseEntity.ok(bookingService.getBookingsByClient(client));
    }

}
