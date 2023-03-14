package org.simple.booking.service.domain;

public class Constants {

    public static final String DOT = ".";
    public static final String END = " end: ";
    public static final String START = " start: ";

    public static final String HOURS = " hours";
    public static final String MINUTES = " minutes";
    public static final String MINIMUM_BOOKABLE_TIMESLOT = "Invalid booking, the minimum bookable timeslot: ";
    public static final String BOOKING_ON_WEEKEND = "Invalid booking, booking can be done only on weekdays.";
    public static final String MAXIMUM_BOOKABLE_TIMESLOT = "Invalid booking, the maximum bookable timeslot: ";
    public static final String BOOKING_BEYOND_THE_WORKING_HOURS = "Invalid booking, the passed booking beyond the working hours,";
    public static final String BOOKING_BEYOND_THE_BOOKABLE_TIME_FRAME = "Invalid booking, the passed booking beyond the bookable timeframe,";
    public static final String INVALID_TIMESLOT_LENGTH = "Invalid booking, the timeslot length can only be integer multiples of the minimum bookable timeslot: ";
    public static final String INVALID_TIMESLOT_START = "Invalid booking, booked timeslot can only start from :00 or :30.";
    public static final String NO_BOOKED_TIMESLOT_FOUND = "No booked timeslot found for the provided data: ";
    public static final String OVERLAPPING_BOOKING = "Invalid request, the passed timeslot overlaps with a booked one: ";
}
