package org.simple.booking.service.service;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.simple.booking.service.domain.Booking;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

/**
 * TODO It could be replaced by a Repository interface, TimeSlotService used to mock the persisting layer
 * In real world scenario Repository and persisting Layer would be used for storing
 * the booked timeslots
 */
@Getter
@Service
public class TimeSlotService {

    private Set<Booking> timeSlotStore;

    @PostConstruct
    private void init() {
        timeSlotStore = new TreeSet<>(Comparator.comparing(Booking::getStart));
    }

}
